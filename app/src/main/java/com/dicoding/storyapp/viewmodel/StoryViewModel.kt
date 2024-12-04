package com.dicoding.storyapp.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.api.ApiService
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class StoryViewModel(
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModel() {
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
            val token = dataStoreManager.getToken().firstOrNull()
            if (!token.isNullOrEmpty()) {
                _token.value = token
                Log.d("StoryViewModel", "Token berhasil diambil: $token")
            } else {
                Log.e("StoryViewModel", "Token tidak ditemukan. Harap login ulang.")
            }
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
                    if (storyResponse != null && !storyResponse.error) {
                        _stories.value = storyResponse.listStory
                        _errorMessage.value = null
                    } else {
                        handleError(storyResponse?.message ?: "Gagal memuat cerita.")
                    }
                } else {
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
                    handleError("Token tidak ditemukan. Harap login ulang.")
                    return@launch
                }

                val response = apiService.getStoryDetail("Bearer $token", storyId)
                if (response.isSuccessful) {
                    val storyResponseWrapper = response.body()
                    if (storyResponseWrapper != null && !storyResponseWrapper.error) {
                        _storyDetail.value = storyResponseWrapper.story
                        _errorMessage.value = null
                    } else {
                        handleError(storyResponseWrapper?.message ?: "Detail cerita tidak ditemukan.")
                    }
                } else {
                    handleError("Gagal memuat detail cerita: ${response.message()}")
                }
            } catch (e: Exception) {
                handleError("Terjadi kesalahan: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun uriToFile(uri: Uri): File {
        val contentResolver: ContentResolver = context.contentResolver
        val tempFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(tempFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input?.copyTo(output)
            }
        }
        return tempFile
    }

    fun uploadStoryWithImage(description: String, imageUri: Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = _token.value
                if (token.isNullOrEmpty()) {
                    handleError("Token tidak ditemukan. Harap login ulang.")
                    onComplete(false)
                    return@launch
                }

                val imageFile = uriToFile(imageUri)

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
