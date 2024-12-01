package com.dicoding.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.api.ApiClient
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

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess: StateFlow<Boolean> = _isSuccess

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
            Log.d("StoryViewModel", "Token diinisialisasi: ${_token.value}")
        }
    }

    private fun handleError(message: String) {
        Log.e("StoryViewModel", message)
        _errorMessage.value = message
        _isLoading.value = false
        _isSuccess.value = false
    }
    fun fetchStories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = _token.value
                if (token.isNullOrEmpty()) {
                    handleError("Token tidak ditemukan. Harap login ulang.")
                    return@launch
                }

                Log.d("StoryViewModel", "Mengambil cerita dengan token: Bearer $token")
                val response = apiService.getStories("Bearer $token")
                if (response.isSuccessful) {
                    val storyResponse = response.body()
                    Log.d("StoryViewModel", "Response API: $storyResponse")
                    if (storyResponse != null && !storyResponse.error) {
                        _stories.value = storyResponse.listStory
                        _errorMessage.value = null
                    } else {
                        handleError(storyResponse?.message ?: "Gagal memuat cerita.")
                    }
                } else {
                    Log.e("StoryViewModel", "Error Body: ${response.errorBody()?.string()}")
                    handleError("Gagal memuat cerita: ${response.message()}")
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
                    _errorMessage.value = "Token tidak ditemukan. Harap login ulang."
                    return@launch
                }

                val response = apiService.getStoryDetail("Bearer $token", storyId)
                if (response.isSuccessful) {
                    val storyResponseWrapper = response.body()
                    if (storyResponseWrapper != null && !storyResponseWrapper.error) {
                        _storyDetail.value = storyResponseWrapper.story
                    } else {
                        _errorMessage.value = storyResponseWrapper?.message ?: "Detail cerita tidak ditemukan."
                    }
                } else {
                    _errorMessage.value = "Gagal memuat detail cerita: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
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

                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
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
                    _isSuccess.value = true
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

    fun logout() {
        viewModelScope.launch {
            dataStoreManager.clearToken()
            _token.value = null
            _stories.value = emptyList()
            _storyDetail.value = null
            _errorMessage.value = null
            _isSuccess.value = false
            Log.d("StoryViewModel", "Logout berhasil.")
        }
    }
}
