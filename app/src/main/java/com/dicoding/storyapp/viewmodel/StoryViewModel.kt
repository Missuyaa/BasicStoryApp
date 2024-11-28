package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.api.ApiService
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StoryViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://story-api.dicoding.dev/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    fun fetchStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                dataStoreManager.getToken().collect { token ->
                    if (token.isNullOrEmpty()) {
                        _errorMessage.value = "Token tidak ditemukan. Harap login ulang."
                    } else {
                        val response = apiService.getStories("Bearer $token")
                        if (response.isSuccessful) {
                            val storyResponse = response.body()
                            if (storyResponse != null && !storyResponse.error) {
                                _stories.value = storyResponse.listStory
                                _errorMessage.value = null
                            } else {
                                _errorMessage.value = storyResponse?.message ?: "Gagal memuat cerita."
                            }
                        } else {
                            _errorMessage.value = "Error: ${response.message()}"
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearToken()
        }
    }
}
