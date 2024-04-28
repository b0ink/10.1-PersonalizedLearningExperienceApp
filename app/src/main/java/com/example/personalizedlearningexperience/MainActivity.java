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

        btnRegister = findViewById(R.id.register);
        btnApiCall = findViewById(R.id.apicall);
        tvText = findViewById(R.id.textView);

        authManager = new AuthManager(this);

        Intent intent = new Intent(this, InterestsActivity.class);
        startActivity(intent);
        finish();

//        // Check if user is logged in
//        if (authManager.getToken() == null || !authManager.isTokenValid()) {
//            Intent intent = new Intent(this, AccountLoginActivity.class);
//            startActivity(intent);
//            finish();
//        }




        tvText.setText("Welcome back, " + authManager.getJwtProperty("username") + "!");


        btnRegister.setOnClickListener(view -> {

        });

        btnApiCall.setOnClickListener(view -> {


        });
    }
}