package com.dicoding.storyapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.StoryAdapter
import com.dicoding.storyapp.data.datastore.DataStoreManager
import com.dicoding.storyapp.data.viewmodel.StoryViewModel
import com.dicoding.storyapp.data.viewmodel.StoryViewModelFactory
import kotlinx.coroutines.launch

class StoryListActivity : AppCompatActivity() {

    private lateinit var storyViewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_list)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_stories)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val dataStoreManager = DataStoreManager(applicationContext)
        storyViewModel = ViewModelProvider(
            this,
            StoryViewModelFactory(dataStoreManager, this)
        )[StoryViewModel::class.java]

        lifecycleScope.launchWhenStarted {
            launch {
                storyViewModel.stories.collect { stories ->
                    recyclerView.adapter = StoryAdapter(stories, this@StoryListActivity)
                }
            }

            launch {
                storyViewModel.isLoading.collect { isLoading ->
                    if (isLoading) {
                        Toast.makeText(this@StoryListActivity, "Loading...", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            launch {
                storyViewModel.errorMessage.collect { errorMessage ->
                    errorMessage?.let {
                        Toast.makeText(this@StoryListActivity, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        storyViewModel.fetchStories()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_story_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Handle Logout
                Toast.makeText(this, "Logout berhasil!", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_open_maps -> {
                // Navigasi ke MapsActivity
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

