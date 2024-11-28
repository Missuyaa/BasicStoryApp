package com.dicoding.storyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dicoding.storyapp.model.Story
import com.dicoding.storyapp.viewmodel.AuthViewModel
import com.dicoding.storyapp.viewmodel.AuthViewModelFactory
import com.dicoding.storyapp.viewmodel.StoryViewModel
import com.dicoding.storyapp.viewmodel.StoryViewModelFactory


@Composable
fun StoryListScreen(
    navController: NavHostController,
    storyViewModel: StoryViewModel = viewModel(factory = StoryViewModelFactory(LocalContext.current)),
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current))
) {
    // Collect state dari ViewModel
    val stories by storyViewModel.stories.collectAsState()
    val isLoading by storyViewModel.isLoading.collectAsState()
    val errorMessage by storyViewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Header dan tombol logout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Daftar Cerita",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(onClick = {
                authViewModel.logout()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true } // Bersihkan backstack
                }
            }) {
                Text("Logout")
            }
        }

        // Tampilkan data berdasarkan state
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            !errorMessage.isNullOrEmpty() -> {
                Text(
                    text = errorMessage ?: "Terjadi kesalahan.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(stories) { story ->
                        StoryCard(story)
                    }
                }
            }
        }
    }
}

@Composable
fun StoryCard(story: Story) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = story.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = story.description, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            story.photoUrl?.let { photoUrl ->
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Story Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}



