package com.example.personalizedlearningexperience;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView tvQuizTopic;

    private Button btnSubmitQuiz;

    private Quiz selectedQuiz;

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
        if (intent == null || !intent.hasExtra(EXTRA_QUIZ_ID)) {
            finish();
        }

        int quizID = intent.getIntExtra(EXTRA_QUIZ_ID, -1);

        if (quizID == -1) {
            finish();
            return;
        }

        selectedQuiz = null;

        tvQuizTopic = findViewById(R.id.tvQuizTopic);
        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz);

        btnSubmitQuiz.setOnClickListener(view -> {
            if(selectedQuiz == null) return;

            Boolean unanswered = false;
            for(QuizQuestion q : selectedQuiz.questions){
                if(q.usersGuess.isEmpty()){
                    unanswered = true;
                }
            }

            if(unanswered){
                Toast.makeText(this, "Please answer all the questions before submitting!", Toast.LENGTH_LONG).show();
                return;
            }

            for(QuizQuestion q : selectedQuiz.questions){
                authManager.saveUsersGuess(selectedQuiz, q);
            }

        });
        authManager = new AuthManager(this);
        ArrayList<Quiz> quizzes = new ArrayList<>();


        ArrayList<QuizQuestion> questions = new ArrayList<>();
        RecyclerView recycler = findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        QuizQuestionAdapter adapter = new QuizQuestionAdapter(this, questions);
        recycler.setAdapter(adapter);


        Call<ResponsePost> call = RetrofitClient.getInstance()
                .getAPI().getUsersQuizzes(authManager.getToken());

        call.enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                String quizData = response.body().message;
                quizzes.addAll(QuizParser.parseQuizzes(QuizActivity.this, quizData));
                //TODO: questionAdapater + recyclerView for the quiz questions
//                tvQuestion1.setText(quizzes.get(0).topic);
                for (Quiz quiz : quizzes) {
                    if (quiz.id == quizID){
                        selectedQuiz = quiz;
                        break;
                    }
                }

                if(selectedQuiz == null){
                    return;
                }

//                for(QuizQuestion q : selectedQuiz.questions){
//                    q.usersGuess = authManager.getUsersGuess(selectedQuiz, q);
//                }

                tvQuizTopic.setText("AI Generated Quiz:\n"+selectedQuiz.getFormattedTopic());


                questions.addAll(selectedQuiz.questions);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable throwable) {

            }
        });


    }
}