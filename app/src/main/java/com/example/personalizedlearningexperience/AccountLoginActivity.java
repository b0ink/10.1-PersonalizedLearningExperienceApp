package com.example.personalizedlearningexperience;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.personalizedlearningexperience.API.AuthManager;
import com.example.personalizedlearningexperience.API.RetrofitClient;
import com.example.personalizedlearningexperience.API.models.ResponsePost;

import java.util.jar.JarEntry;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountLoginActivity extends AppCompatActivity {


    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnCreateAccount;

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(AccountLoginActivity.this, AccountRegisterActivity.class);
            startActivity(intent);
            finish();
            return;
        });

        authManager = new AuthManager(this);

        btnLogin.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();

            Call<ResponsePost> call = RetrofitClient.getInstance()
                    .getAPI()
                    .loginUser(username, password);

            call.enqueue(new Callback<ResponsePost>() {
                @Override
                public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                    try {
                        if (!response.isSuccessful()) {
                            // response was not 2xx
                            // TODO: respond accordingly
                            // TODO: custom 4xx codes for each error
                            // TODO: 409: conflict (existing user/email?)
                            return;
                        }

                        String token = response.headers().get("Authorization");
                        authManager.saveToken(token);
                        Toast.makeText(AccountLoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AccountLoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponsePost> call, Throwable throwable) {

                }
            });

        });
    }
}