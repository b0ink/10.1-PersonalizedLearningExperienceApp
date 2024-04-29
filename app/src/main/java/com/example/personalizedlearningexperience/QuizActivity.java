package com.example.personalizedlearningexperience;

import android.content.Intent;
import android.os.Bundle;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_QUIZ_ID = "extra_quiz_id";

    private AuthManager authManager;
    private TextView tvQuestion1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if(intent == null || !intent.hasExtra(EXTRA_QUIZ_ID)){
            finish();
        }

        tvQuestion1 = findViewById(R.id.tvQuestion1);

        authManager = new AuthManager(this);
        ArrayList<Quiz> quizzes = new ArrayList<>();

        Call<ResponsePost> call = RetrofitClient.getInstance()
                .getAPI().getUsersQuizzes(authManager.getToken());

        call.enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                String quizData = response.body().message;
                quizzes.addAll(QuizParser.parseQuizzes(quizData));
                //TODO: questionAdapater + recyclerView for the quiz questions
                tvQuestion1.setText(quizzes.get(0).topic);
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable throwable) {

            }
        });


    }
}