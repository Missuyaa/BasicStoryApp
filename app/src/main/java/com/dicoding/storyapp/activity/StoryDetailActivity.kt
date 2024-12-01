package com.dicoding.storyapp.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.viewmodel.StoryViewModel
import com.dicoding.storyapp.viewmodel.StoryViewModelFactory
import com.dicoding.storyapp.data.DataStoreManager
import kotlinx.coroutines.launch

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var storyViewModel: StoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)

        val storyId = intent.getStringExtra("STORY_ID") ?: ""
        val dataStoreManager = DataStoreManager(applicationContext)
        val factory = StoryViewModelFactory(dataStoreManager)
        storyViewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]

        storyViewModel.fetchStoryDetail(storyId)

        lifecycleScope.launch {
            storyViewModel.storyDetail.collect { story ->
                if (story != null) {
                    findViewById<ImageView>(R.id.iv_detail_photo).let {
                        Glide.with(this@StoryDetailActivity)
                            .load(story.photoUrl)
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_error)
                            .into(it)
                    }
                    findViewById<TextView>(R.id.tv_detail_name).text = story.name ?: "Tidak diketahui"
                    findViewById<TextView>(R.id.tv_detail_description).text = story.description ?: "Deskripsi tidak tersedia"
                }
            }
        }
    }
}
