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

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.personalizedlearningexperience.API.models.ResponsePost;
import com.example.personalizedlearningexperience.API.RetrofitClient;

public class AccountRegisterActivity extends AppCompatActivity {


    private EditText etUsername;
    private EditText etEmail;
    private EditText etConfirmEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etMobile;

    private Button btnRegister;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etConfirmEmail = findViewById(R.id.etConfirmEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etMobile = findViewById(R.id.etMobile);

        btnRegister = findViewById(R.id.btnRegister);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(AccountRegisterActivity.this, AccountLoginActivity.class);
            startActivity(intent);
            finish();
            return;
        });

        btnRegister.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String confirmEmail = etConfirmEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();

            if(username.isEmpty() || email.isEmpty() || confirmEmail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || mobile.isEmpty()){
                Toast.makeText(AccountRegisterActivity.this, "Please fill out all fields.", Toast.LENGTH_LONG).show();
                return;
            }

            if(!email.equals(confirmEmail)){
                Toast.makeText(AccountRegisterActivity.this, "Email addresses do not match. Please ensure both email fields are identical.", Toast.LENGTH_LONG).show();
                return;
            }

            if(!password.equals(confirmPassword)){
                Toast.makeText(AccountRegisterActivity.this, "Passwords do not match. Please ensure both password fields are identical.", Toast.LENGTH_LONG).show();
                return;
            }

            Call<ResponsePost> call = RetrofitClient.getInstance()
                    .getAPI()
                    .createUser(username, email, confirmEmail, password, confirmPassword, mobile);

            call.enqueue(new Callback<ResponsePost>() {
                @Override
                public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {

                    if (!response.isSuccessful() || (response.body() != null && response.body().status.equals("400"))) {
                        if(response.body() != null){
                            Toast.makeText(AccountRegisterActivity.this, response.body().message, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(AccountRegisterActivity.this, "Invalid fields", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }

                    try {
                        String message = response.body().message;
                        System.out.println(message);
                        Toast.makeText(AccountRegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AccountRegisterActivity.this, AccountLoginActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponsePost> call, Throwable throwable) {
                    Toast.makeText(AccountRegisterActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });


    }
}