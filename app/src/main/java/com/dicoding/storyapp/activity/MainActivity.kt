package com.dicoding.storyapp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.ui.StoryAppTheme
import com.dicoding.storyapp.ui.screens.AddStoryScreen
import com.dicoding.storyapp.ui.screens.LoginScreen
import com.dicoding.storyapp.ui.screens.StoryListScreen
import com.dicoding.storyapp.viewmodel.AuthViewModel
import com.dicoding.storyapp.viewmodel.AuthViewModelFactory
import com.dicoding.storyapp.viewmodel.StoryViewModel
import com.dicoding.storyapp.viewmodel.StoryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreManager = DataStoreManager(applicationContext) // Inisialisasi DataStoreManager

        setContent {
            val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(dataStoreManager))
            val storyViewModelFactory = StoryViewModelFactory(dataStoreManager) // Gunakan DataStoreManager di sini
            StoryApp(authViewModel, storyViewModelFactory)
        }
    }
}

@Composable
fun StoryApp(authViewModel: AuthViewModel, storyViewModelFactory: StoryViewModelFactory) {
    StoryAppTheme {
        val navController = rememberNavController()

        // Gunakan `value` secara eksplisit untuk mendapatkan nilai State
        val isLoggedInState = authViewModel.isLoggedIn.collectAsState(initial = false)
        val isLoggedIn = isLoggedInState.value

        NavHost(navController = navController, startDestination = if (isLoggedIn) "story_list" else "login") {
            composable("login") {
                LoginScreen(navController, authViewModel)
            }
            composable("story_list") {
                val storyViewModel: StoryViewModel = viewModel(factory = storyViewModelFactory)
                StoryListScreen(navController, storyViewModel)
            }
            composable("add_story") {
                AddStoryScreen(navController)
            }
        }
    }
}


