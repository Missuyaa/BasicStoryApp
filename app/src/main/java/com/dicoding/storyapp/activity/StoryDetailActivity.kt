package com.dicoding.storyapp.activity

import android.os.Bundle
import android.transition.TransitionInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.viewmodel.StoryViewModel
import com.dicoding.storyapp.viewmodel.StoryViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var storyViewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup shared element transition
        window.sharedElementEnterTransition = TransitionInflater.from(this)
            .inflateTransition(android.R.transition.move)
        window.sharedElementExitTransition = TransitionInflater.from(this)
            .inflateTransition(android.R.transition.move)

        setContentView(R.layout.activity_story_detail)

        val storyId = intent.getStringExtra("STORY_ID") ?: ""
        setupViewModel()
        observeStateFlows()
        storyViewModel.fetchStoryDetail(storyId)
    }

    private fun setupViewModel() {
        val dataStoreManager = DataStoreManager(applicationContext)
        val factory = StoryViewModelFactory(dataStoreManager, applicationContext)
        storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
    }

    private fun observeStateFlows() {
        lifecycleScope.launchWhenStarted {
            launch {
                storyViewModel.storyDetail.collect { story ->
                    if (story != null) {
                        displayStoryDetail(story.photoUrl, story.name, story.description)
                    } else {
                        showError("Detail cerita tidak ditemukan.")
                    }
                }
            }

            launch {
                storyViewModel.isLoading.collect { isLoading ->
                    if (isLoading) {
                        showLoading(true)
                    } else {
                        showLoading(false)
                    }
                }
            }

            launch {
                storyViewModel.errorMessage.collect { errorMessage ->
                    errorMessage?.let { showError(it) }
                }
            }
        }
    }

    private fun displayStoryDetail(photoUrl: String?, name: String?, description: String?) {
        findViewById<ImageView>(R.id.iv_detail_photo).let { imageView ->
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(imageView)
        }

        findViewById<TextView>(R.id.tv_detail_name).text =
            name ?: getString(R.string.name_not_available)

        findViewById<TextView>(R.id.tv_detail_description).text =
            description ?: getString(R.string.description_not_available)
    }

    private fun showLoading(isLoading: Boolean) {
        // Anda dapat menambahkan progress bar di layout dan mengelola visibilitasnya
        if (isLoading) {
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Selesai.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
