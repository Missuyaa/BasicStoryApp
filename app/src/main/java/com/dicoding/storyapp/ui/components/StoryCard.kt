package com.dicoding.storyapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.dicoding.storyapp.model.Story

@Composable
fun StoryCard(story: Story, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nama pengguna
            Text(
                text = story.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Deskripsi cerita
            Text(
                text = story.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2 // Batasi hingga 2 baris
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Gambar cerita
            AsyncImage(
                model = story.photoUrl,
                contentDescription = "Story Photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}
