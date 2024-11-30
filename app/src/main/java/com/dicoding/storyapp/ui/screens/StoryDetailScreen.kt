package com.dicoding.storyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.dicoding.storyapp.viewmodel.StoryViewModel
import com.dicoding.storyapp.viewmodel.StoryViewModelFactory

@Composable
fun StoryDetailScreen(
    navController: NavHostController,
    storyId: String,
    storyViewModel: StoryViewModel
) {
    val story by storyViewModel.storyDetail.collectAsState()
    val isLoading by storyViewModel.isLoading.collectAsState()
    val errorMessage by storyViewModel.errorMessage.collectAsState()

    LaunchedEffect(storyId) {
        storyViewModel.fetchStoryDetail(storyId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Terjadi kesalahan.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            story != null -> {
                AsyncImage(
                    model = story?.photoUrl,
                    contentDescription = "Story Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = story?.name.orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = story?.description.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


