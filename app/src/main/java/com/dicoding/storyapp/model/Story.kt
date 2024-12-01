package com.dicoding.storyapp.model

data class StoryResponseWrapper(
    val error: Boolean,
    val message: String,
    val story: Story
)

data class Story(
    val id: String?,
    val name: String?,
    val description: String?,
    val photoUrl: String?,
    val createdAt: String?,
    val lat: Double?,
    val lon: Double?
)

