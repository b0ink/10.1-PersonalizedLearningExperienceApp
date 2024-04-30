package com.example.personalizedlearningexperience;

import android.accounts.Account;
import android.content.Context;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.example.personalizedlearningexperience.API.RetrofitClient;
import com.example.personalizedlearningexperience.API.models.ResponsePost;
import com.example.personalizedlearningexperience.API.AuthManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnApiCall;
    private TextView tvText;

    private AuthManager authManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        btnRegister = findViewById(R.id.register);
//        btnApiCall = findViewById(R.id.apicall);
        tvText = findViewById(R.id.textView);

        authManager = new AuthManager(this);

        // DEBUG -> view InterestsActivity
//        Intent intent = new Intent(this, InterestsActivity.class);
//        startActivity(intent);
//        finish();


//        // Check if user is logged in
        if (authManager.getToken() == null || !authManager.isTokenValid()) {
            Intent intent = new Intent(this, AccountLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Check for enough topics/interests have been set
        ArrayList<String> interests = authManager.getInterests();
        if (interests == null || interests.size() < 3) {
            Intent intent = new Intent(this, InterestsActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        //TODO: if no quizzes -> call API with one of user's interest to generate new quiz
        //TODO: API creates empty row first, calls Llama, populates row with response
        //TODO: this way no additional quizzes are made until the initial call is complete

        System.out.println(authManager.getToken());

        tvText.setText("Welcome back, \n" + authManager.getJwtProperty("username") + "!");


        //TODO: save interests in preferences, if none exist, redirect to interestsAcitivity to select new ones

//        -> create new quiz -> on response, reload activity using:
//        finish();
//        startActivity(getIntent());


        ArrayList<Quiz> quizzes = new ArrayList<>();
//        quizzes.add(new Quiz(1, "iPhone", new ArrayList<>()));
        RecyclerView recycler = findViewById(R.id.tasksRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        TasksAdapter adapter = new TasksAdapter(this, quizzes);
        recycler.setAdapter(adapter);

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
                System.out.println(quizData);

                quizzes.addAll(QuizParser.parseQuizzes(MainActivity.this, quizData));
                for(Quiz quiz : quizzes){
                    quiz.loaded = true;
                    System.out.println("Correct answers for quiz " + quiz.topic + ": " + quiz.getCorrectAnswers());
                }

                Collections.reverse(quizzes); // Newest at top

                Collections.sort(quizzes, Comparator.comparing(Quiz::userHasAttempted));


                if (quizzes.size() < 3 && interests.size() > 0) {
                    String randomTopic = interests.get(new Random().nextInt(interests.size()));

                    Quiz placeholderQuiz = new Quiz(-1, "GENERATING QUIZ...");
                    placeholderQuiz.loaded = true;
                    quizzes.add(0, placeholderQuiz);
                    adapter.notifyItemInserted(0);

                    Call<ResponsePost> newQuizCall = RetrofitClient.getInstance()
                            .getAPI().createNewQuiz(authManager.getToken(), randomTopic);
                    newQuizCall.enqueue(new Callback<ResponsePost>() {
                        @Override
                        public void onResponse(Call<ResponsePost> newQuizCall, Response<ResponsePost> response) {
                            String jsonString = response.body().message;
                            ArrayList<Quiz> newQuizzes = QuizParser.parseQuizzes(MainActivity.this, jsonString);
                            quizzes.remove(0);
//                            adapter.notifyItemRemoved(0);
                            quizzes.add(0, newQuizzes.get(0)); // TODO error handle
//                            adapter.notifyItemInserted(0);
                            adapter.notifyItemChanged(0);

//                            finish();
//                            startActivity(getIntent());
                        }

                        @Override
                        public void onFailure(Call<ResponsePost> newQuizCall, Throwable throwable) {
                            System.out.println(throwable.getMessage());
                            quizzes.remove(0);
                            adapter.notifyItemRemoved(0);
                            Toast.makeText(MainActivity.this, "An error occurred generating the quiz, please try again.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                //TODO: if quiz list is empty, generate a new quiz
                //TODO: refer to localStorage if this topic has been completed, to generate a new one
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponsePost> call, Throwable throwable) {
                System.out.println("BAD ERROR" + throwable.getMessage());

            }
        });

    }


//    public static ArrayList<QuizQuestion> parseQuizQuestions(String json) {
//        ArrayList<QuizQuestion> quizQuestions = new ArrayList<>();
//        try {
//            if (json != null) {
//                JSONArray jsonArray = new JSONArray(json);
////                JSONArray jsonArray = jsonObject.getJSONArray("Topics");
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject questionObject = jsonArray.getJSONObject(i);
//
//                    String question = questionObject.getString("Topic");
//                    JSONArray questions = questionObject.getJSONArray("Questions");
//
//                    quizQuestions.add(jsonArray.getString(i));
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return quizTopics;
//    }
}