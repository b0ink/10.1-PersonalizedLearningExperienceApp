package com.example.personalizedlearningexperience;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.personalizedlearningexperience.API.AuthManager;
import com.example.personalizedlearningexperience.API.RetrofitClient;
import com.example.personalizedlearningexperience.API.models.ResponsePost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvTotalQuestions;
    private TextView tvCorrectlyAnswered;
    private TextView tvIncorrectAnswers;
    private TextView tvAccountType;

    private Button btnLogout;
    private Button btnShareProfile;
    private Button btnUpgradeAccount;


    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authManager = new AuthManager(this);

        if (authManager.getToken() == null || !authManager.isTokenValid()) {
            Intent intent = new Intent(this, AccountLoginActivity.class);
            startActivity(intent);
            finish();
        }


        ((ImageButton)findViewById(R.id.btnGoBack)).setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnLogout = findViewById(R.id.btnLogout);
        btnShareProfile = findViewById(R.id.btnShareProfile);
        btnUpgradeAccount = findViewById(R.id.btnUpgradeAccount);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        tvCorrectlyAnswered = findViewById(R.id.tvCorrectlyAnswered);
        tvIncorrectAnswers = findViewById(R.id.tvIncorrectAnswers);
        tvAccountType = findViewById(R.id.tvAccountType);

        tvUsername.setText(authManager.getJwtProperty("username"));
        tvEmail.setText(authManager.getJwtProperty("email"));

        btnLogout.setOnClickListener(view->{
            authManager.logout();
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
        });


        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("extra_upgrade")){
            String upgradeType = intent.getStringExtra("extra_upgrade");
            tvAccountType.setText("Account type: " + upgradeType);
        }else{
            tvAccountType.setText("Account type: Basic");
        }
        btnUpgradeAccount.setOnClickListener(view -> {
            startActivity(new Intent(this, UpgradeAccountActivity.class));
        });

        btnShareProfile.setOnClickListener(view ->{
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my profile!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, BuildConfig.SHARE_URL +authManager.getJwtProperty("username")+"/quizzes");
            startActivity(Intent.createChooser(shareIntent, "Share"));
        });

        Call<ResponsePost> call = RetrofitClient.getInstance()
                .getAPI().getUsersQuizzes(authManager.getToken());

        call.enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                if (!response.isSuccessful()) {
                    System.out.println("Error occurred!");
                    return;
                }

                String quizData = response.body().message;
                ArrayList<Quiz> quizzes = new ArrayList<>();
                quizzes.addAll(QuizParser.parseQuizzes(ProfileActivity.this, quizData));

                int totalQuestions = 0;
                int wrongAnswered = 0;
                int correctAnswered = 0;
                for (Quiz quiz : quizzes) {
                    totalQuestions += quiz.getTotalQuestions();
                    correctAnswered += quiz.getCorrectAnswers();
                    wrongAnswered += quiz.getWrongAnswers();
                }

                tvTotalQuestions.setText(String.valueOf(totalQuestions));
                tvCorrectlyAnswered.setText(String.valueOf(correctAnswered));
                tvIncorrectAnswers.setText(String.valueOf(wrongAnswered));
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable throwable) {
                System.out.println("BAD ERROR" + throwable.getMessage());
            }
        });

    }
}