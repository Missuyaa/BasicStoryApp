package com.dicoding.storyapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

class LoginActivity : AppCompatActivity() {

    // Fungsi login untuk memproses autentikasi pengguna
    private fun login(email: String, password: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val credentials = mapOf(
            "email" to email,
            "password" to password
        )

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Panggil API menggunakan Retrofit
                val response = apiService.login(credentials)

                if (response.isSuccessful) { // Periksa status HTTP respons
                    val loginResponse = response.body() // Ambil body respons
                    withContext(Dispatchers.Main) {
                        if (loginResponse != null && !loginResponse.error!!) {
                            saveSession(loginResponse.loginResult?.token) // Simpan token ke SharedPreferences
                            Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                            Log.d("LoginActivity", "Navigasi ke MainActivity dimulai")
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                loginResponse?.message ?: "Login gagal.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API_ERROR", e.message ?: "Unknown error")
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Fungsi untuk menyimpan token login ke SharedPreferences
private fun saveSession(token: String?) {
    val sharedPreferences = getSharedPreferences("StoryAppPrefs", MODE_PRIVATE)
    sharedPreferences.edit().putString("token", token).apply()
    // Tambahkan log dan pesan toast untuk memastikan token disimpan
    Log.d("LoginActivity", "Token disimpan: $token")
    Toast.makeText(this, "Token disimpan: $token", Toast.LENGTH_SHORT).show()
}

    // Fungsi untuk menginisialisasi elemen UI dan mendefinisikan interaksi pengguna
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.ed_login_email)
        val passwordEditText = findViewById<EditText>(R.id.ed_login_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val registerTextView = findViewById<TextView>(R.id.tv_register)

        // Klik tombol login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            login(email, password) // Panggil fungsi login
        }

        // Klik teks untuk navigasi ke halaman register
        registerTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
