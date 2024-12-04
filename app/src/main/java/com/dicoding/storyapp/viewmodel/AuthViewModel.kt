package com.dicoding.storyapp.viewmodel

import android.util.Log
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
                    val token = response.body()?.loginResult?.token
                    if (!token.isNullOrEmpty()) {
                        dataStoreManager.saveToken(token) // Simpan token
                        _isLoggedIn.value = true
                        _errorMessage.value = null
                        Log.d("AuthViewModel", "Login Berhasil. Token: $token")
                        onResult(true)
                    } else {
                        _errorMessage.value = "Login gagal: Token tidak valid."
                        Log.e("AuthViewModel", "Token tidak valid.")
                        onResult(false)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = "Login gagal: ${errorBody ?: response.message()}"
                    Log.e("AuthViewModel", "Login gagal. Error Body: $errorBody")
                    onResult(false)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
                Log.e("AuthViewModel", "Terjadi kesalahan: ${e.message}")
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