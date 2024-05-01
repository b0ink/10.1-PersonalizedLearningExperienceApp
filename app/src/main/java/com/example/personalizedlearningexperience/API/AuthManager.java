package com.example.personalizedlearningexperience.API;

import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.example.personalizedlearningexperience.Quiz;
import com.example.personalizedlearningexperience.QuizQuestion;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "auth_pref";
    private static final String JWT_TOKEN_KEY = "jwt_token";

    public AuthManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public String getUsersGuess(Quiz quiz, QuizQuestion question) {
        if (quiz == null || question == null) {
            return "";
        }
        String quizId = String.valueOf(quiz.id);
        String quizQuestion = question.question;

        String key = quizId + quizQuestion;

        return sharedPreferences.getString(key, "");
    }


    public void saveUsersGuess(Quiz quiz, QuizQuestion question) {
        if (quiz == null || question == null || question.usersGuess.isEmpty()) {
            return;
        }
        String quizId = String.valueOf(quiz.id);
        String quizQuestion = question.question;
        String usersGuess = question.usersGuess;

        String key = quizId + quizQuestion;
        String value = usersGuess;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        System.out.println("Saved users guess " + key + value);
    }

    public ArrayList<String> getInterests() {
        ArrayList<String> topics = new ArrayList<>();
        String jsonString = sharedPreferences.getString("interests", null);

        if(jsonString == null){
            return topics;
        }

        System.out.println(jsonString);
        String result = jsonString.substring(1, jsonString.length() - 1);

        String[] parts = result.split(",");
        for (int i = 0; i < parts.length; i++) {
            topics.add(parts[i]);
        }

        return topics;
    }

    public void saveInterests(ArrayList<String> topics) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("interests", topics.toString());
        editor.apply();
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(JWT_TOKEN_KEY, token);
        editor.apply();
        System.out.println("Saved token");
    }

    public void resetToken() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(JWT_TOKEN_KEY);
        editor.apply();
    }

    public String getToken() {
        return sharedPreferences.getString(JWT_TOKEN_KEY, null);
    }

    public String getJwtProperty(String key) {
        JWT parsedJWT = new JWT(this.getToken());
        Claim subscriptionMetaData = parsedJWT.getClaim(key);
        String parsedValue = subscriptionMetaData.asString();
        return parsedValue;
    }

    public void logout() {
        resetToken();
        // TEMP DEBUG: clears any saved interests, forcing new ones to be selected on next login
        saveInterests(new ArrayList<>());
    }

    public Boolean isTokenValid() {
        if (this.getToken() == null) {
            return false;
        }

        try {
            JWT parsedJWT = new JWT(this.getToken());
            boolean isExpired = parsedJWT.isExpired(10);
            if (isExpired) {
                resetToken();
                return false;
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}