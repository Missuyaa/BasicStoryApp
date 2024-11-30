package com.dicoding.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.api.ApiService
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class StoryViewModel(private val dataStoreManager: DataStoreManager) : ViewModel() {

    private val _stories = MutableStateFlow<List<Story>>(emptyList())
    val stories: StateFlow<List<Story>> = _stories

    private val _storyDetail = MutableStateFlow<Story?>(null)
    val storyDetail: StateFlow<Story?> = _storyDetail

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://story-api.dicoding.dev/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    init {
        viewModelScope.launch {
            _token.value = dataStoreManager.getToken().first()
        }
    }

    private fun handleError(message: String) {
        _errorMessage.value = message
        _isLoading.value = false
    }

    /**
     * Fetch the list of stories
     */
    fun fetchStories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = _token.value
                if (token.isNullOrEmpty()) {
                    handleError("Token tidak ditemukan. Harap login ulang.")
                    return@launch
                }
                val response = apiService.getStories("Bearer $token")
                if (response.isSuccessful) {
                    val storyResponse = response.body()
                    if (storyResponse != null && !storyResponse.error) {
                        _stories.value = storyResponse.listStory
                    } else {
                        handleError(storyResponse?.message ?: "Gagal memuat cerita.")
                    }
                } else {
                    handleError("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                handleError("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Fetch details of a specific story
     */
    fun fetchStoryDetail(storyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val token = _token.value
                if (token.isNullOrEmpty()) {
                    handleError("Token tidak ditemukan. Harap login ulang.")
                    return@launch
                }
                val response = apiService.getStoryDetail("Bearer $token", storyId)
                if (response.isSuccessful) {
                    _storyDetail.value = response.body()
                } else {
                    handleError("Gagal mengambil detail cerita.")
                }
            } catch (e: Exception) {
                handleError("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Upload a new story with an image
     */
    fun uploadStoryWithImage(description: String, imageFile: File, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = _token.value
                if (token.isNullOrEmpty()) {
                    handleError("Token tidak ditemukan. Harap login ulang.")
                    onComplete(false)
                    return@launch
                }

                // Create RequestBody and MultipartBody.Part
                val requestImageFile = imageFile.asRequestBody(contentType = "image/jpeg".toMediaType())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo", imageFile.name, requestImageFile
                )
                val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())

                // Call API
                val response = apiService.addStoryWithImage(
                    token = "Bearer $token",
                    file = imageMultipart,
                    description = descriptionRequestBody
                )

                if (response.isSuccessful) {
                    _errorMessage.value = null
                    onComplete(true)
                } else {
                    handleError("Gagal mengunggah cerita: ${response.message()}")
                    onComplete(false)
                }
            } catch (e: Exception) {
                handleError("Terjadi kesalahan: ${e.message}")
                onComplete(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear the error message
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Logout the user
     */
    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearToken()
            _token.value = null
        }
    }
}
