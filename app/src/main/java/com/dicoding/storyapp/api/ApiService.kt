package com.dicoding.storyapp.api

import com.dicoding.storyapp.model.LoginResponse
import com.dicoding.storyapp.model.RegisterResponse
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

    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): LoginResponse

    @POST("register")
    suspend fun register(@Body user: Map<String, String>): RegisterResponse

    @POST("stories")
    suspend fun addStory(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Response<Unit>

    @POST("stories")
    @Multipart
    suspend fun addStoryWithImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Response<Unit>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String
    ): Response<StoryResponse> // Mengembalikan StoryResponse
}
