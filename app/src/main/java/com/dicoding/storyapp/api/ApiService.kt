package com.dicoding.storyapp.api

import com.dicoding.storyapp.model.LoginResponse
import com.dicoding.storyapp.model.RegisterResponse
import com.dicoding.storyapp.model.Story
import com.dicoding.storyapp.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    // Login endpoint
    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): Response<LoginResponse>

    // Register endpoint
    @POST("register")
    suspend fun register(@Body credentials: Map<String, String>): Response<RegisterResponse>

    // Get list of stories
    @GET("stories")
    suspend fun getStories(@Header("Authorization") token: String): Response<StoryResponse>

    // Get story details by ID
    @GET("stories/{id}")
    suspend fun getStoryDetail(
        @Header("Authorization") token: String,
        @retrofit2.http.Path("id") storyId: String
    ): Response<Story>

    // Add story with optional image
    @Multipart
    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part? = null,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Response<Unit>

    // Add story with image only
    @Multipart
    @POST("stories")
    suspend fun addStoryWithImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Response<Unit>
}

