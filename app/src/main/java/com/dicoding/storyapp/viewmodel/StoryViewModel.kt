package com.dicoding.storyapp.viewmodel

import android.util.Log
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

    // Tambahkan StateFlow untuk token
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://story-api.dicoding.dev/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    init {
        // Inisialisasi token dari DataStore saat ViewModel dibuat
        viewModelScope.launch {
            _token.value = dataStoreManager.getToken().first()
            Log.d("StoryViewModel", "Token diinisialisasi: ${_token.value}")
        }
    }

    private fun handleError(message: String) {
        Log.e("StoryViewModel", message)
        _errorMessage.value = message
        _isLoading.value = false
    }

    fun fetchStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = dataStoreManager.getToken().first()
                Log.d("StoryViewModel", "Token yang digunakan: Bearer $token")

                if (token.isNullOrEmpty()) {
                    handleError("Token tidak ditemukan. Harap login ulang.")
                    return@launch
                }

                val response = apiService.getStories("Bearer $token")
                if (response.isSuccessful) {
                    val storyResponse = response.body()
                    if (storyResponse != null && !storyResponse.error) {
                        _stories.value = storyResponse.listStory
                        _errorMessage.value = null
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


    fun fetchStoryDetail(storyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = _token.value
                if (token.isNullOrEmpty()) {
                    handleError("Token tidak ditemukan. Harap login ulang.")
                    return@launch
                }
                Log.d("StoryViewModel", "Token digunakan untuk detail: Bearer $token")
                val response = apiService.getStoryDetail("Bearer $token", storyId)

                if (response.isSuccessful) {
                    val story = response.body()
                    if (story != null) {
                        _storyDetail.value = story
                        Log.d("StoryViewModel", "Detail cerita berhasil dimuat.")
                    } else {
                        handleError("Detail cerita tidak ditemukan.")
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

                val requestImageFile = imageFile.asRequestBody(contentType = "image/jpeg".toMediaType())
                val imageMultipart = MultipartBody.Part.createFormData(
                    "photo", imageFile.name, requestImageFile
                )
                val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())

                val response = apiService.addStoryWithImage(
                    token = "Bearer $token",
                    file = imageMultipart,
                    description = descriptionRequestBody
                )

                if (response.isSuccessful) {
                    _errorMessage.value = null
                    onComplete(true)
                    Log.d("StoryViewModel", "Cerita berhasil diunggah.")
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

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearToken()
            _token.value = null
            Log.d("StoryViewModel", "Token berhasil dihapus.")
        }
    }
}
