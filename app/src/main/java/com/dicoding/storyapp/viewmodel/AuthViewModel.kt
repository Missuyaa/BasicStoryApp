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

    private fun checkLoginStatus() {
        viewModelScope.launch {
            dataStoreManager.getToken().collect { token ->
                _isLoggedIn.value = !token.isNullOrEmpty()
            }
        }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val credentials = mapOf("email" to email, "password" to password)
                val response = ApiClient.apiService.login(credentials)
                if (!response.error) {
                    dataStoreManager.saveToken(response.token)
                    onResult(true)
                } else {
                    _errorMessage.value = response.message
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
