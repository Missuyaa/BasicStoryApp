package com.dicoding.storyapp.model

data class Story(
    val id: String,
    val name: String,
    val description: String,
    val photoUrl: String,
    val createdAt: String,
    val lat: Double? = null,
    val lon: Double? = null
)
