package com.example.personalizedlearningexperience;

import android.accounts.Account;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.example.personalizedlearningexperience.API.models.ResponsePost;
import com.example.personalizedlearningexperience.API.AuthManager;

import java.util.ArrayList;

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

        // Check if user is logged in
        if (authManager.getToken() == null || !authManager.isTokenValid()) {
            Intent intent = new Intent(this, AccountLoginActivity.class);
            startActivity(intent);
            finish();
        }

        //TODO: if no quizzes -> call API with one of user's interest to generate new quiz
        //TODO: API creates empty row first, calls Llama, populates row with response
        //TODO: this way no additional quizzes are made until the initial call is complete

        System.out.println(authManager.getToken());

        tvText.setText("Welcome back, " + authManager.getJwtProperty("username") + "!");


        ArrayList<Quiz> quizzes = new ArrayList<>();
        quizzes.add(new Quiz(1, "iPhone", new ArrayList<>()));
        RecyclerView recycler = findViewById(R.id.tasksRecyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        TasksAdapter adapter = new TasksAdapter(this, quizzes);
        recycler.setAdapter(adapter);



    }
}