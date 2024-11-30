package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.api.ApiClient
import com.dicoding.storyapp.data.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    init {
        viewModelScope.launch {
            dataStoreManager.getToken().collect { token ->
                _isLoggedIn.value = !token.isNullOrEmpty()
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val credentials = mapOf("email" to email, "password" to password)
                val response = ApiClient.apiService.login(credentials)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.error) {
                        val token = body.token
                        if (!token.isNullOrEmpty()) {
                            dataStoreManager.saveToken(token)
                            _isLoggedIn.value = true
                            onResult(true)
                        } else {
                            _errorMessage.value = "Token tidak valid."
                            onResult(false)
                        }
                    } else {
                        _errorMessage.value = body?.message ?: "Gagal login."
                        onResult(false)
                    }
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                    onResult(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(name: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val credentials = mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password
                )
                val response = ApiClient.apiService.register(credentials)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.error) {
                        onResult(true)
                    } else {
                        _errorMessage.value = body?.message ?: "Gagal registrasi."
                        onResult(false)
                    }
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                    onResult(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearToken()
            _isLoggedIn.value = false
        }
    }
}

