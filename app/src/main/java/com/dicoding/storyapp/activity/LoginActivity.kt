package com.dicoding.storyapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.api.ApiService
import com.dicoding.storyapp.model.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private fun login(email: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/") // Base URL yang diperbarui
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        val credentials = mapOf(
            "email" to email,
            "password" to password
        )

        // Gunakan coroutine untuk memanggil suspend function
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response: LoginResponse = apiService.login(credentials)
                withContext(Dispatchers.Main) {
                    if (!response.error) {
                        saveSession(response.token)
                        Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, StoryListActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveSession(token: String?) {
        val sharedPreferences = getSharedPreferences("StoryAppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().putString("token", token).apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.ed_login_email)
        val passwordEditText = findViewById<EditText>(R.id.ed_login_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val registerTextView = findViewById<TextView>(R.id.tv_register)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(email, password)
        }

        // Navigasi ke halaman registrasi
        registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
