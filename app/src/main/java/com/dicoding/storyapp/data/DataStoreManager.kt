package com.dicoding.storyapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
    }

    fun getToken(): Flow<String?> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    println("Error reading token from DataStore: ${exception.message}")
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val token = preferences[TOKEN_KEY]
                println("Token diambil dari DataStore: $token")
                token
            }
    }

    suspend fun saveToken(token: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[TOKEN_KEY] = token
            }
            println("Token berhasil disimpan: $token")
        } catch (e: Exception) {
            println("Error saving token: ${e.message}")
        }
    }


    suspend fun clearToken() {
        try {
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
            println("Token berhasil dihapus.")
        } catch (e: Exception) {
            println("Error clearing token: ${e.message}")
        }
    }
}
