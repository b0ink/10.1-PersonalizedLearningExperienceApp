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

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity {

    interface RequestUser {
        @FormUrlEncoded
        @POST("api/register")
//        Call<ResponsePost> postUser(@Body RequestPost requestPost);
        Call<ResponsePost> postUser(
                @Field("username") String username,
                @Field("fullname") String fullname,
                @Field("email") String email,
                @Field("confirmEmail") String confirmEmail,
                @Field("password") String password,
                @Field("confirmPassword") String confirmPassword,
                @Field("mobileNumber") String mobileNumber
        );

        @POST("api/login")
//        Call<ResponsePost> postUser(@Body RequestPost requestPost);
        Call<ResponsePost> loginUser(
                @Field("username") String username,
                @Field("password") String password
        );

        @GET("/protected")
        Call<ResponsePost> getUser(

        );

    }

    private Button btnRegister;
    private Button btnApiCall;
    private TextView tvText;


    public String sessionID = "";

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

        Intent intent = new Intent(this, AccountRegisterActivity.class);
        startActivity(intent);
        finish();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();



        RequestUser requestUser = retrofit.create(RequestUser.class);
        authManager = new AuthManager(this);

        btnRegister.setOnClickListener(view -> {
//            AuthService authService = retrofit.create(AuthService.class);

            // Example usage:
//            authManager.loginUser("username", "password", new AuthManager.AuthCallback() {
//                @Override
//                public void onSuccess() {
//                    // Authentication successful
//                    String token = authManager.getToken();
//                    // Proceed to next screen or make further API calls
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    // Authentication failed
//                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//                }
//            });
            //TODO: make a retrofitClient class

//            requestUser.loginUser("username", "password").enqueue(new Callback<ResponsePost>() {
//                @Override
//                public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
//                    if(response != null && response.body() != null){
////                        sessionID = response.body().sessionID;
//                        String cookie = response.headers().get("Set-Cookie");
//                        if (cookie != null && cookie.startsWith("sessionID=")) {
//                            sessionID = cookie.split(";")[0].substring("sessionID=".length());
//                            tvText.setText(sessionID);
//                        }else{
//                            tvText.setText("no session!");
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponsePost> call, Throwable throwable) {
//                    tvText.setText("wtf?");
//                }
//            });
        });

        btnApiCall.setOnClickListener(view -> {
//            authManager.getProtectedResource(authManager.getToken(), new AuthManager.AuthCallback() {
//                @Override
//                public void onSuccess() {
//
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    // Authentication failed
//                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
//                }
//            });
        });
    }
}