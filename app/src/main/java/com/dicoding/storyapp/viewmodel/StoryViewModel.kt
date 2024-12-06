package com.dicoding.storyapp.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storyapp.api.ApiClient
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.model.Story
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class StoryViewModel(
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : ViewModel() {

    private val apiService = ApiClient.apiService

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

    init {
        viewModelScope.launch {
            val token = dataStoreManager.getToken().firstOrNull()
            if (token.isNullOrEmpty()) {
                Log.e("StoryViewModel", "Token tidak ditemukan. Harap login ulang.")
            } else {
                Log.d("StoryViewModel", "Token berhasil diambil: $token")
            }
        }
    }

    private fun handleError(message: String) {
        Log.e("StoryViewModel", message)
        _errorMessage.value = message
        _isLoading.value = false
        _isSuccess.value = false
    }

    private suspend fun getToken(): String? {
        return dataStoreManager.getToken().firstOrNull()
    }

    fun fetchStories() {
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()

            if (token.isNullOrEmpty()) {
                _errorMessage.value = "Token tidak ditemukan. Harap login ulang."
                _isLoading.value = false
                return@launch
            }

            try {
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
                    _errorMessage.value = "Gagal memuat cerita: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchStoryDetail(storyId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()

            if (token.isNullOrEmpty()) {
                _errorMessage.value = "Token tidak ditemukan. Harap login ulang."
                _isLoading.value = false
                return@launch
            }

            try {
                val response = apiService.getStoryDetail("Bearer $token", storyId)
                if (response.isSuccessful) {
                    val storyResponseWrapper = response.body()
                    if (storyResponseWrapper != null && !storyResponseWrapper.error) {
                        _storyDetail.value = storyResponseWrapper.story
                        _errorMessage.value = null
                    } else {
                        _errorMessage.value =
                            storyResponseWrapper?.message ?: "Detail cerita tidak ditemukan."
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
            val token = getToken()

            if (token.isNullOrEmpty()) {
                handleError("Token tidak ditemukan. Harap login ulang.")
                onComplete(false)
                return@launch
            }

            try {
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

}
