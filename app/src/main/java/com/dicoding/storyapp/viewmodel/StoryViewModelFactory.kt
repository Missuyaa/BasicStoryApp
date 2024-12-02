package com.dicoding.storyapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.DataStoreManager

class StoryViewModelFactory(
    private val dataStoreManager: DataStoreManager,
    private val context: Context

) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(dataStoreManager, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
