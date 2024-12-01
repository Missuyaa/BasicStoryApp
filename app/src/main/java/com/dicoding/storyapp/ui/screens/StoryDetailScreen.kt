package com.dicoding.storyapp.ui.screens

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.viewmodel.StoryViewModel

@Composable
fun StoryDetailScreen(
    storyId: String,
    storyViewModel: StoryViewModel,
    modifier: Modifier = Modifier
) {
    val story by storyViewModel.storyDetail.collectAsState()
    val isLoading by storyViewModel.isLoading.collectAsState()
    val errorMessage by storyViewModel.errorMessage.collectAsState()

    // Fetch story detail when the screen is loaded
    androidx.compose.runtime.LaunchedEffect(storyId) {
        storyViewModel.fetchStoryDetail(storyId)
    }

    if (isLoading) {
        // Show a loading indicator
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.CircularProgressIndicator()
        }
    } else if (errorMessage != null) {
        // Show an error message
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = errorMessage ?: "Terjadi kesalahan.",
                color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )
        }
    } else if (story != null) {
        // Display XML layout using AndroidView
        AndroidView(
            factory = { context ->
                // Inflate the XML layout
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.activity_story_detail, null, false)

                // Bind data to the XML layout
                val imageView = view.findViewById<ImageView>(R.id.iv_detail_photo)
                val nameTextView = view.findViewById<TextView>(R.id.tv_detail_name)
                val descriptionTextView = view.findViewById<TextView>(R.id.tv_detail_description)

                // Use Glide to load the image
                Glide.with(context)
                    .load(story?.photoUrl)
                    .placeholder(R.drawable.ic_placeholder) // Placeholder while loading
                    .error(R.drawable.ic_error) // Error image
                    .into(imageView)

                // Set data to TextViews
                nameTextView.text = story?.name ?: "Nama tidak diketahui"
                descriptionTextView.text = story?.description ?: "Deskripsi tidak tersedia"

                view
            },
            modifier = modifier.fillMaxSize()
        )
    }
}
