package com.dicoding.storyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    val tokenState = storyViewModel.token.collectAsState()
    val token = tokenState.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Cerita") },
                actions = {
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
                Text("Token tidak ditemukan. Harap login ulang.", color = MaterialTheme.colorScheme.error)
            } else {
                Text("Token ditemukan: $token")
                // Tambahkan UI untuk menampilkan daftar cerita
            }
        }
    }
}
