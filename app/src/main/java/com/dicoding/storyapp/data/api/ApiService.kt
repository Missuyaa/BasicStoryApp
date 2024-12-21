package com.dicoding.storyapp.data.api

import com.dicoding.storyapp.data.model.LoginResponse
import com.dicoding.storyapp.data.model.RegisterResponse
import com.dicoding.storyapp.data.model.StoryResponse
import com.dicoding.storyapp.data.model.StoryResponseWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body credentials: Map<String, String>): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body credentials: Map<String, String>): Response<RegisterResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<StoryResponse>


    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") storyId: String
    ): Response<StoryResponseWrapper>


    @Multipart
    @POST("stories")
    suspend fun addStoryWithImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): Response<Unit>

    @Headers("Cache-Control: no-cache")
    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") token: String,
        @Query("location") location: String

    ): StoryResponse


}

