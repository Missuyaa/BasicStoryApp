package com.dicoding.storyapp.data.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.api.ApiClient
import com.dicoding.storyapp.data.datastore.DataStoreManager
import com.dicoding.storyapp.data.datastore.StoryPagingSource
import com.dicoding.storyapp.data.model.Story
import com.dicoding.storyapp.data.model.StoryResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

open class StoryViewModel(
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


    val storyPagingData = Pager(
        config = PagingConfig(
            pageSize = 10,
            prefetchDistance = 1,
            enablePlaceholders = false,
            initialLoadSize = 10
        ),
        pagingSourceFactory = {
            StoryPagingSource(apiService, token = runBlocking { getToken() ?: "" })
        }
    ).flow.cachedIn(viewModelScope)

    fun getStoriesWithLocation(): LiveData<List<Story>> = liveData {
        try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("StoryViewModel", "Token tidak ditemukan.")
                emit(emptyList())
                return@liveData
            }

            Log.d("StoryViewModel", "Token dikirim: Bearer $token")

            val response = apiService.getStoriesWithLocation(
                token = "Bearer $token",
                location = "1"
            )

            if (response.listStory.isNotEmpty()) {
                Log.d("StoryViewModel", "Jumlah cerita dengan lokasi: ${response.listStory.size}")
                emit(response.listStory)
            } else {
                Log.d("StoryViewModel", "Tidak ada cerita dengan lokasi yang diterima.")
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("StoryViewModel", "Terjadi kesalahan: ${e.message}")
            emit(emptyList())
        }
    }

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

    fun fetchStories(page: Int = 1, size: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            val token = getToken()

            if (token.isNullOrEmpty()) {
                _errorMessage.value = "Token tidak ditemukan. Harap login ulang."
                _isLoading.value = false
                return@launch
            }

            try {
                Log.d("StoryViewModel", "Fetching stories with token: $token")
                val response = apiService.getStories("Bearer $token", page, size)
                if (response.isSuccessful) {
                    val storyResponse = response.body()
                    if (storyResponse != null && !storyResponse.error) {
                        _stories.value = storyResponse.listStory
                        Log.d("StoryViewModel", "Fetched stories: ${storyResponse.listStory.size}")
                        _errorMessage.value = null
                    } else {
                        _errorMessage.value = storyResponse?.message ?: "Gagal memuat cerita."
                        Log.e("StoryViewModel", "Error: ${_errorMessage.value}")
                    }
                } else {
                    _errorMessage.value = "Gagal memuat cerita: ${response.message()}"
                    Log.e("StoryViewModel", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
                Log.e("StoryViewModel", "Exception: ${e.message}")
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

    fun uploadStoryWithImage(
        description: String,
        imageUri: Uri,
        lat: Double?,
        lon: Double?,
        onComplete: (Boolean) -> Unit
    ) {
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
                val latRequestBody = lat?.toString()?.toRequestBody("text/plain".toMediaType())
                val lonRequestBody = lon?.toString()?.toRequestBody("text/plain".toMediaType())

                val response = apiService.addStoryWithImage(
                    token = "Bearer $token",
                    file = imageMultipart,
                    description = descriptionRequestBody,
                    lat = latRequestBody,
                    lon = lonRequestBody
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

