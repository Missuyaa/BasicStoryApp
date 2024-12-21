package com.dicoding.storyapp.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.data.api.ApiClient
import com.dicoding.storyapp.data.datastore.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    val isLoggedIn = dataStoreManager.isLoggedIn()

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val credentials = mapOf("email" to email, "password" to password)
                val response = ApiClient.apiService.login(credentials)

                if (response.isSuccessful) {
                    val token = response.body()?.loginResult?.token
                    if (!token.isNullOrEmpty()) {
                        dataStoreManager.saveToken(token)
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
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
        }
    }
}