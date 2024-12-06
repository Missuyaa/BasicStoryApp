package com.dicoding.storyapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.ui.StoryAppTheme
import com.dicoding.storyapp.ui.screens.LoginScreen
import com.dicoding.storyapp.viewmodel.AuthViewModel
import com.dicoding.storyapp.viewmodel.AuthViewModelFactory

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStoreManager = DataStoreManager(applicationContext)

        // Cek token login dari DataStore atau SharedPreferences
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", null)

        if (token != null) {
            // Jika token ditemukan, langsung masuk ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // Jika belum login, tampilkan LoginScreen
            setContent {
                val authViewModel: AuthViewModel =
                    viewModel(factory = AuthViewModelFactory(dataStoreManager))

                StoryAppTheme {
                    LoginScreen(
                        onLoginSuccess = {
                            // Simpan token saat login berhasil
                            sharedPref.edit().putString("TOKEN", token).apply()

                            // Pindah ke MainActivity
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        },
                        onRegisterClick = {
                            startActivity(Intent(this, RegisterActivity::class.java))
                        },
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

