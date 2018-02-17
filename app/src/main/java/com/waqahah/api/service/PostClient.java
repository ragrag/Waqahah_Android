package com.waqahah.api.service;

import com.waqahah.api.model.Login;
import com.waqahah.api.model.Post;
import com.waqahah.api.model.Profile;
import com.waqahah.api.model.User;

import java.math.BigInteger;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Raggi on 8/22/2017.
 */


    public interface PostClient{
        // Request method and URL specified in the annotation
        // Callback for the parsed response is the last parameter



    @POST("api/auth/token/")
    Call<User> login(@Body Login login);


    @GET("api/recpostlist")
    Call<List<Post>> getPostList(@Header("Authorization") String token);

    @GET("api/profile")
    Call<Profile> getProfile(@Header("Authorization") String token);

    @GET("api/user/search/{username}")
    Call<Profile> searchProfile(@Path("username") String username);

    @GET("api/post/delete/{id}")
    Call<ResponseBody> deletePost(@Header("Authorization") String token,@Path("id") BigInteger id);


    @POST("api/post/create/{username}")
    Call<ResponseBody> makePost(@Header("Authorization") String token,@Path("username") String username,@Body Post obj);



    @FormUrlEncoded
    @POST("api/user/create/")
    Call<ResponseBody> register(@Field("email") String email,@Field("username") String username,@Field("password") String password,@Field("first_name") String first_name,@Field("last_name") String last_name);

    }

