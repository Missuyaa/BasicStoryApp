package com.dicoding.storyapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.StoryAdapter
import com.dicoding.storyapp.api.ApiService
import com.dicoding.storyapp.model.Story
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StoryListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_list)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_stories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Ambil token dari SharedPreferences
        val sharedPreferences = getSharedPreferences("StoryAppPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            fetchStories(token) { stories ->
                val adapter = StoryAdapter(stories, this)
                recyclerView.adapter = adapter
            }
        } else {
            Toast.makeText(this, "Token tidak ditemukan. Harap login ulang.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_story_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val sharedPreferences = getSharedPreferences("StoryAppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        Toast.makeText(this, "Logout berhasil!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun fetchStories(token: String, callback: (List<Story>) -> Unit) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://story-api.dicoding.dev/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(ApiService::class.java)

        lifecycleScope.launch {
            try {
                val response = apiService.getStories("Bearer $token")
                if (response.isSuccessful) {
                    val storyResponse = response.body()
                    if (storyResponse != null && !storyResponse.error) {
                        callback(storyResponse.listStory)
                    } else {
                        Log.e("StoryListActivity", "Gagal memuat cerita: ${storyResponse?.message}")
                        Toast.makeText(
                            this@StoryListActivity,
                            storyResponse?.message ?: "Gagal memuat cerita",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("StoryListActivity", "Error: ${response.message()}")
                    Toast.makeText(
                        this@StoryListActivity,
                        "Error: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("StoryListActivity", "Error: ${e.message}")
                Toast.makeText(this@StoryListActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
