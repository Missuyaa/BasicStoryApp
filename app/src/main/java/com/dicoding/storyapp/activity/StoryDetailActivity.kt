package com.dicoding.storyapp.activity

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R

class StoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)

        val name = intent.getStringExtra("name")
        val photoUrl = intent.getStringExtra("photoUrl")
        val description = intent.getStringExtra("description")

        findViewById<TextView>(R.id.tv_detail_name).text = name
        findViewById<TextView>(R.id.tv_detail_description).text = description
        val imageView = findViewById<ImageView>(R.id.iv_detail_photo)
        Glide.with(this).load(photoUrl).into(imageView)
    }
}
