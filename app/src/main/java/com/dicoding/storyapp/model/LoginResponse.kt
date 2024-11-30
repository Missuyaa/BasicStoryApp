package com.dicoding.storyapp.model

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val token: String?
)

