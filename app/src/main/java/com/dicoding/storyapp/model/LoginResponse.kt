package com.dicoding.storyapp.model

data class LoginResponse(
    val token: String,
    val error: Boolean,
    val message: String
)
