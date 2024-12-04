package com.dicoding.storyapp.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.ui.StoryAppTheme
import com.dicoding.storyapp.ui.screens.*
import com.dicoding.storyapp.viewmodel.AuthViewModel
import com.dicoding.storyapp.viewmodel.AuthViewModelFactory
import com.dicoding.storyapp.viewmodel.StoryViewModel
import com.dicoding.storyapp.viewmodel.StoryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStoreManager = DataStoreManager(applicationContext)

        setContent {
            val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(dataStoreManager))
            val storyViewModel: StoryViewModel = viewModel(factory = StoryViewModelFactory(dataStoreManager, applicationContext))
            StoryApp(authViewModel, storyViewModel)
        }
    }
}

@Composable
fun StoryApp(authViewModel: AuthViewModel, storyViewModel: StoryViewModel) {
    StoryAppTheme {
        val navController = rememberNavController()
        val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "story_list" else "login"
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("story_list") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onRegisterClick = {
                        navController.navigate("register")
                    },
                    authViewModel = authViewModel
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    authViewModel = authViewModel
                )
            }

            composable("story_list") {
                StoryListScreen(
                    navController = navController,
                    storyViewModel = storyViewModel,
                    authViewModel = authViewModel
                )
            }

            composable("add_story") {
                AddStoryScreen(
                    navController = navController,
                    storyViewModel = storyViewModel
                )
            }

            composable("story_detail/{storyId}") { backStackEntry ->
                val storyId = backStackEntry.arguments?.getString("storyId") ?: ""
                StoryDetailScreen(
                    storyId = storyId,
                    storyViewModel = storyViewModel
                )
            }
        }
    }
}
