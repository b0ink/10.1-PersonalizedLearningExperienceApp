package com.example.personalizedlearningexperience.API;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import com.example.personalizedlearningexperience.API.models.ResponsePost;

public interface API {
    @FormUrlEncoded
    @POST("users/login")
    Call<ResponsePost> loginUser(
            @Field("username") String username,
            @Field("password") String password
    );

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

    @GET("/protected")
    Call<ResponsePost> getProtectedResource(@Header("Authorization") String token);
}
