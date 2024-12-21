package com.dicoding.storyapp

import com.dicoding.storyapp.data.api.ApiService
import com.dicoding.storyapp.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class FakeApiService : ApiService {

    override suspend fun login(credentials: Map<String, String>): Response<LoginResponse> {
        val success = credentials["username"] == "user" && credentials["password"] == "password"
        return Response.success(
            LoginResponse(
                error = !success,
                message = if (success) "Login successful" else "Invalid credentials",
                loginResult = if (success) LoginResult("fakeToken123") else null
            )
        )
    }

    override suspend fun register(credentials: Map<String, String>): Response<RegisterResponse> {
        return Response.success(
            RegisterResponse(
                error = false,
                message = "Registration successful"
            )
        )
    }

    override suspend fun getStories(
        token: String,
        page: Int,
        size: Int
    ): Response<StoryResponse> {
        val fakeStories = List(size) { index ->
            Story(
                id = "id-$index",
                name = "Story $index",
                description = "Description for Story $index",
                photoUrl = "https://picsum.photos/200",
                createdAt = "2024-12-21",
                lat = null,
                lon = null
            )
        }
        return Response.success(
            StoryResponse(
                error = false,
                message = "Stories fetched successfully",
                listStory = fakeStories
            )
        )
    }

    override suspend fun getStoryDetail(token: String, storyId: String): Response<StoryResponseWrapper> {
        return Response.success(
            StoryResponseWrapper(
                error = false,
                message = "Story details fetched successfully",
                story = Story(
                    id = storyId,
                    name = "Story $storyId",
                    description = "Description for Story $storyId",
                    photoUrl = "https://picsum.photos/200",
                    createdAt = "2024-12-21",
                    lat = 12.34,
                    lon = 56.78
                )
            )
        )
    }

    override suspend fun addStoryWithImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): Response<Unit> {
        return Response.success(Unit)
    }

    override suspend fun getStoriesWithLocation(
        token: String,
        location: String
    ): StoryResponse {
        val fakeStories = List(5) { index ->
            Story(
                id = "id-$index",
                name = "Story with Location $index",
                description = "Description for Story with Location $index",
                photoUrl = "https://picsum.photos/200",
                createdAt = "2024-12-21",
                lat = 12.34 + index,
                lon = 56.78 + index
            )
        }
        return StoryResponse(
            error = false,
            message = "Stories with location fetched successfully",
            listStory = fakeStories
        )
    }
}
