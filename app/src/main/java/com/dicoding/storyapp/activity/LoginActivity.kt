package com.dicoding.storyapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.ui.StoryAppTheme
import com.dicoding.storyapp.ui.screens.LoginScreen
import com.dicoding.storyapp.viewmodel.AuthViewModel
import com.dicoding.storyapp.viewmodel.AuthViewModelFactory

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi DataStoreManager
        val dataStoreManager = DataStoreManager(applicationContext)

        setContent {
            // Inisialisasi ViewModel menggunakan Factory
            val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(dataStoreManager))

            // Menampilkan UI Login
            StoryAppTheme {
                LoginScreen(
                    onLoginSuccess = {
                        // Navigasi ke MainActivity jika login berhasil
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onRegisterClick = {
                        // Navigasi ke RegisterActivity jika pengguna memilih register
                        startActivity(Intent(this, RegisterActivity::class.java))
                    },
                    authViewModel = authViewModel
                )
            }
        }
    }
}
