package com.example.personalizedlearningexperience;

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

        btnRegister.setOnClickListener(view -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String confirmEmail = etConfirmEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();

            //TODO: validation
            //TODO: check email == confirmEmail && password == confirmPassword

            Call<ResponsePost> call = RetrofitClient.getInstance()
                    .getAPI()
                    .createUser(username, email, confirmEmail, password,confirmPassword, mobile);

            call.enqueue(new Callback<ResponsePost>() {
                @Override
                public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
                    try {
                        String message  = response.body().message;
                        System.out.println(message);
                        Toast.makeText(AccountRegisterActivity.this, message, Toast.LENGTH_LONG).show();
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