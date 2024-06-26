package com.example.personalizedlearningexperience.API;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.example.personalizedlearningexperience.API.models.ResponsePost;

public interface API {
    // Login user
    @FormUrlEncoded
    @POST("users/login")
    Call<ResponsePost> loginUser(
            @Field("username") String username,
            @Field("password") String password
    );

    // Create new user
    @FormUrlEncoded
    @POST("users/register")
    Call<ResponsePost> createUser(
            @Field("username") String username,
            @Field("email") String email,
            @Field("confirmEmail") String confirmEmail,
            @Field("password") String password,
            @Field("confirmPassword") String confirmPassword,
            @Field("mobile") String mobile
    );

    // Retrieve all quizzes for user
    @GET("quiz")
    Call<ResponsePost> getUsersQuizzes(@Header("Authorization") String token);

    // Generate new quiz
    @GET("quiz/create")
    Call<ResponsePost> createNewQuiz(@Header("Authorization") String token, @Query("query") String topic);

    // Retrieve feedback for specific question/answer
    @FormUrlEncoded
    @POST("quiz/feedback")
    Call<ResponsePost> getQuizFeedback(
            @Header("Authorization") String token,
            @Field("question") String question,
            @Field("options") String options,
            @Field("correctAnswer") String correctAnswer,
            @Field("usersGuess") String usersGuess
    );
}
