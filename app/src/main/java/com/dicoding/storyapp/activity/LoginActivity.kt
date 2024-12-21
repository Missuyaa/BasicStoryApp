package com.dicoding.storyapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dicoding.storyapp.data.datastore.DataStoreManager
import com.dicoding.storyapp.ui.StoryAppTheme
import com.dicoding.storyapp.ui.screens.LoginScreen
import com.dicoding.storyapp.data.viewmodel.AuthViewModel
import com.dicoding.storyapp.data.viewmodel.AuthViewModelFactory

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStoreManager = DataStoreManager(applicationContext)

        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val token = sharedPref.getString("TOKEN", null)



        if (token != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            setContent {
                val authViewModel: AuthViewModel =
                    viewModel(factory = AuthViewModelFactory(dataStoreManager))

                StoryAppTheme {
                    LoginScreen(
                        onLoginSuccess = {
                            sharedPref.edit().putString("TOKEN", token).apply()

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

