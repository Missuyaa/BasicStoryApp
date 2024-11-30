package com.dicoding.storyapp.data


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Ekstensi untuk membuat DataStore
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
    }

    /**
     * Fungsi untuk mendapatkan token dari DataStore
     */
    fun getToken(): Flow<String?> {
        return context.dataStore.data
            .map { preferences ->
                val token = preferences[TOKEN_KEY]
                println("Token diambil dari DataStore: $token") // Tambahkan log
                token
            }
    }

    /**
     * Fungsi untuk menyimpan token ke DataStore
     */
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            println("Token berhasil disimpan: $token") // Tambahkan log ini
        }
    }


    suspend fun clearToken() {
        try {
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
        } catch (e: Exception) {
            println("Error clearing token: ${e.message}")
        }
    }

}

