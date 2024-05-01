package com.example.personalizedlearningexperience;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_QUIZ_ID = "extra_quiz_id";
    public static final String EXTRA_QUIZ_LOAD_RESULTS = "extra_quiz_load_results";

    private AuthManager authManager;
    private TextView tvQuizTopic;

    private Button btnSubmitQuiz;
    private GifImageView gifSpinner;
    private ImageButton btnGoBack;

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
        Boolean loadResults = intent.getBooleanExtra(EXTRA_QUIZ_LOAD_RESULTS, false);

        if (quizID == -1) {
            finish();
            return;
        }

        selectedQuiz = null;

        tvQuizTopic = findViewById(R.id.tvQuizTopic);
        btnSubmitQuiz = findViewById(R.id.btnSubmitQuiz);
        gifSpinner = findViewById(R.id.gifSpinner);
        gifSpinner.setVisibility(View.VISIBLE);
        btnGoBack = findViewById(R.id.btnGoBack);

        btnGoBack.setOnClickListener(view -> {
            Intent homeIntent = new Intent(QuizActivity.this, MainActivity.class);
            startActivity(homeIntent);
            finish();
            return;
        });

        btnSubmitQuiz.setOnClickListener(view -> {
            if (selectedQuiz == null) return;

            if (loadResults) {
                Intent homeIntent = new Intent(QuizActivity.this, MainActivity.class);
                startActivity(homeIntent);
                finish();
                return;
            }

            Boolean unanswered = false;
            for (QuizQuestion q : selectedQuiz.questions) {
                if (q.usersGuess.isEmpty()) {
                    unanswered = true;
                }
            }

            if (unanswered) {
                Toast.makeText(this, "Please answer all the questions before submitting!", Toast.LENGTH_LONG).show();
                return;
            }

            for (QuizQuestion q : selectedQuiz.questions) {
                authManager.saveUsersGuess(selectedQuiz, q);
            }


            // Refreshes the activity - since user now has saved responses it will load as a results page
            Intent refreshIntent = new Intent(QuizActivity.this, QuizActivity.class);
            refreshIntent.putExtra(EXTRA_QUIZ_ID, quizID);
            refreshIntent.putExtra(EXTRA_QUIZ_LOAD_RESULTS, true);
            startActivity(refreshIntent);
            finish();

        });
        authManager = new AuthManager(this);
        ArrayList<Quiz> quizzes = new ArrayList<>();


        ArrayList<QuizQuestion> questions = new ArrayList<>();
        RecyclerView recycler = findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ArrayList<String> placeholderQuestions = new ArrayList<>();
        placeholderQuestions.add("");
        placeholderQuestions.add("");
        placeholderQuestions.add("");
        placeholderQuestions.add("");
        questions.add(new QuizQuestion("Loading...", placeholderQuestions, ""));
        questions.add(new QuizQuestion("Loading...", placeholderQuestions, ""));
        questions.add(new QuizQuestion("Loading...", placeholderQuestions, ""));
        questions.add(new QuizQuestion("Loading...", placeholderQuestions, ""));
        questions.add(new QuizQuestion("Loading...", placeholderQuestions, ""));
        QuizQuestionAdapter adapter = new QuizQuestionAdapter(this, questions);
        recycler.setAdapter(adapter);


        Call<ResponsePost> call = RetrofitClient.getInstance()
                .getAPI().getUsersQuizzes(authManager.getToken());

        call.enqueue(new Callback<ResponsePost>() {
            @Override
            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                String quizData = response.body().message;
                quizzes.addAll(QuizParser.parseQuizzes(QuizActivity.this, quizData));
//                tvQuestion1.setText(quizzes.get(0).topic);
                for (Quiz quiz : quizzes) {
                    if (quiz.id == quizID) {
                        selectedQuiz = quiz;
                        break;
                    }
                }

                if (selectedQuiz == null) {
                    return;
                }

                gifSpinner.setVisibility(View.GONE);

//                for(QuizQuestion q : selectedQuiz.questions){
//                    q.usersGuess = authManager.getUsersGuess(selectedQuiz, q);
//                }

                tvQuizTopic.setText("Quiz:\n" + selectedQuiz.getFormattedTopic());
                if (loadResults) {
                    tvQuizTopic.setText("Your Results:\n" + selectedQuiz.getFormattedTopic() + " quiz");
                    btnSubmitQuiz.setText("Go back home");
                }
                questions.clear();
                questions.addAll(selectedQuiz.questions);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable throwable) {

            }
        });


    }
}