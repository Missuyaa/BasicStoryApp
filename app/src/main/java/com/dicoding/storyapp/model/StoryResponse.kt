package com.dicoding.storyapp.model

import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @SerializedName("listStory")
    val listStory: List<Story>, // Daftar cerita

    @SerializedName("error")
    val error: Boolean, // Apakah ada error?

    @SerializedName("message")
    val message: String // Pesan dari server
)

data class Story(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("photoUrl")
    val photoUrl: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("lat")
    val lat: Double?,

    @SerializedName("lon")
    val lon: Double?
)
