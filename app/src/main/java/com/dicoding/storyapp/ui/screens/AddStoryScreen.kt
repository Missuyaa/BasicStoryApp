package com.dicoding.storyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dicoding.storyapp.viewmodel.StoryViewModel

@Composable
fun AddStoryScreen(
    navController: NavHostController,
    storyViewModel: StoryViewModel = viewModel()
) {
    // State untuk deskripsi cerita dan error
    var description by remember { mutableStateOf("") }
    val isLoading by storyViewModel.isLoading.collectAsState()
    val errorMessage by storyViewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Input untuk deskripsi cerita
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Deskripsi Cerita") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol upload cerita
        Button(
            onClick = {
                storyViewModel.uploadStory(description) { success ->
                    if (success) {
                        navController.navigate("story_list")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Upload Cerita")
        }

        // Loading indicator
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        // Pesan error
        errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
        }
    }
}
