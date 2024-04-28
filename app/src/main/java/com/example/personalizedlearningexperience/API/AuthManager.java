package com.example.personalizedlearningexperience.API;

import android.content.Context;
import android.content.SharedPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class AuthManager {


//    private AuthService authService;x

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "auth_pref";
    private static final String JWT_TOKEN_KEY = "jwt_token";

    public AuthManager(Context context) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:3000/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();

//        this.authService = retrofit.create(AuthService.class);;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
//
//    public void loginUser(String username, String password, final AuthCallback callback) {
//        authService.loginUser(username, password).enqueue(new Callback<ResponsePost>() {
//            @Override
//            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
//                if (response.isSuccessful()) {
//                    String token = response.headers().get("Authorization");
//                    saveToken(token);
//                    callback.onSuccess();
//                } else {
//                    callback.onFailure("Login failed");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponsePost> call, Throwable t) {
//                callback.onFailure("Network error");
//            }
//        });
//    }
//
//    public void getProtectedResource(String token, final AuthCallback callback) {
//        authService.getProtectedResource("Bearer " + token).enqueue(new Callback<ResponsePost>() {
//            @Override
//            public void onResponse(Call<ResponsePost> call, Response<ResponsePost> response) {
//                if (response.isSuccessful()) {
//                    callback.onSuccess();
//                } else {
//                    callback.onFailure("Failed to fetch protected resource");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponsePost> call, Throwable t) {
//                callback.onFailure("Network error");
//            }
//        });
//    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JWT_TOKEN_KEY, token);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(JWT_TOKEN_KEY, null);
    }

//    public interface AuthCallback {
//        void onSuccess();
//        void onFailure(String message);
//    }
}