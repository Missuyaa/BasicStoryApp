package com.dicoding.storyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.dicoding.storyapp.animations.AnimatedButton
import com.dicoding.storyapp.ui.components.StoryCard
import com.dicoding.storyapp.viewmodel.AuthViewModel
import com.dicoding.storyapp.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryListScreen(
    navController: NavHostController,
    storyViewModel: StoryViewModel,
    authViewModel: AuthViewModel
) {
    val stories by storyViewModel.stories.collectAsState()
    val isLoading by storyViewModel.isLoading.collectAsState()
    val errorMessage by storyViewModel.errorMessage.collectAsState()

    // Memanggil fetchStories saat layar dimuat
    LaunchedEffect(Unit) {
        storyViewModel.fetchStories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Cerita") },
                actions = {
                    AnimatedButton(
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("story_list") { inclusive = true }
                            }
                        },
                        text = "Logout"
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Navigasi ke halaman tambah cerita
                    navController.navigate("add_story")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Cerita"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                errorMessage != null -> Text(
                    text = errorMessage ?: "Terjadi kesalahan.",
                    color = MaterialTheme.colorScheme.error
                )
                stories.isNotEmpty() -> {
                    LazyColumn {
                        items(stories) { story ->
                            StoryCard(
                                story = story,
                                onClick = {
                                    // Navigasi ke detail cerita
                                    navController.navigate("story_detail/${story.id}")
                                }
                            )
                        }
                    }
                }
                else -> Text(text = "Tidak ada cerita yang ditemukan.")
            }
        }
    }
}
