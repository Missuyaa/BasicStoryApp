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

        setContent {

            val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(dataStoreManager))

            StoryAppTheme {
                LoginScreen(
                    onLoginSuccess = {
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
