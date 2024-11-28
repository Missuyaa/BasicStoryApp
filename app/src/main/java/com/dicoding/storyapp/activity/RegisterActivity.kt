package com.dicoding.storyapp.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.storyapp.R
import com.dicoding.storyapp.api.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameEditText = findViewById<EditText>(R.id.ed_register_name)
        val emailEditText = findViewById<EditText>(R.id.ed_register_email)
        val passwordEditText = findViewById<EditText>(R.id.ed_register_password)
        val registerButton = findViewById<Button>(R.id.btn_register)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, "Password minimal 8 karakter!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            register(name, email, password)
        }
    }

    private fun register(name: String, email: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/") // Ganti dengan URL API Anda
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        // Gunakan coroutine untuk memanggil suspend function
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val user = mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password
                )

                val response = apiService.register(user)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registrasi berhasil!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Kembali ke halaman login
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registrasi gagal: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
