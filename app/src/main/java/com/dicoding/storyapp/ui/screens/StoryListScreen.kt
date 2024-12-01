package com.dicoding.storyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dicoding.storyapp.viewmodel.AuthViewModel
import com.dicoding.storyapp.viewmodel.StoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryListScreen(
    navController: NavHostController,
    storyViewModel: StoryViewModel,
    authViewModel: AuthViewModel
) {
    // Observasi token menggunakan collectAsState
    val token by storyViewModel.token.collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Cerita") },
                actions = {
                    // Tombol logout
                    Button(
                        onClick = {
                            authViewModel.logout()
                            navController.navigate("login") {
                                popUpTo("story_list") { inclusive = true }
                            }
                        }
                    ) {
                        Text("Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (token.isNullOrEmpty()) {
                // Tampilkan pesan jika token tidak ditemukan
                Text(
                    text = "Token tidak ditemukan. Harap login ulang.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // Tampilkan UI daftar cerita di sini
                Text(text = "Token ditemukan: $token")
            }
        }
    }
}
