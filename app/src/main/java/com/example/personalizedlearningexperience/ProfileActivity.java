package com.example.personalizedlearningexperience;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

        //TODO: check if logged in


        btnLogout = findViewById(R.id.btnLogout);
        btnShareProfile = findViewById(R.id.btnShareProfile);
        btnUpgradeAccount = findViewById(R.id.btnUpgradeAccount);

        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        tvCorrectlyAnswered = findViewById(R.id.tvCorrectlyAnswered);
        tvIncorrectAnswers = findViewById(R.id.tvIncorrectAnswers);

        tvUsername.setText(authManager.getJwtProperty("username"));
        tvEmail.setText(authManager.getJwtProperty("email"));

        btnLogout.setOnClickListener(view->{
            authManager.logout();
            startActivity(new Intent(this, AccountLoginActivity.class));
            finish();
        });

        btnUpgradeAccount.setOnClickListener(view -> {
            startActivity(new Intent(this, UpgradeAccountActivity.class));
        });

        btnShareProfile.setOnClickListener(view ->{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Check out my profile!");
            intent.putExtra(Intent.EXTRA_TEXT, "https://deakin.edu.au/profile/"+authManager.getJwtProperty("username")+"/quizzes");
            startActivity(Intent.createChooser(intent, "Share"));
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