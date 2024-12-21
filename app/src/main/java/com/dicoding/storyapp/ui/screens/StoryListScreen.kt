package com.dicoding.storyapp.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.dicoding.storyapp.activity.MapsActivity
import com.dicoding.storyapp.animations.AnimatedButton
import com.dicoding.storyapp.ui.components.StoryCard
import com.dicoding.storyapp.data.viewmodel.AuthViewModel
import com.dicoding.storyapp.data.viewmodel.StoryViewModel
import com.dicoding.storyapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryListScreen(
    navController: NavHostController,
    storyViewModel: StoryViewModel,
    authViewModel: AuthViewModel
) {
    val storyPagingItems = storyViewModel.storyPagingData.collectAsLazyPagingItems()
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daftar Cerita") },
                actions = {
                    IconButton(onClick = {
                        navController.context.startActivity(
                            Intent(navController.context, MapsActivity::class.java)
                        )
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_map),
                            contentDescription = "Lihat Peta"
                        )
                    }

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
                onClick = { navController.navigate("add_story") }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Cerita")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(storyPagingItems.itemCount) { index ->
                    val story = storyPagingItems[index]
                    if (story != null) {
                        StoryCard(
                            story = story,
                            onClick = {
                                navController.navigate("story_detail/${story.id}")
                            }
                        )
                    }
                }

                when (val appendState = storyPagingItems.loadState.append) {
                    is androidx.paging.LoadState.Loading -> {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                    is androidx.paging.LoadState.Error -> {
                        item {
                            Text(
                                text = "Error memuat data berikutnya: ${(appendState as androidx.paging.LoadState.Error).error.message}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                    else -> {}
                }
            }

            // Tambahkan indikator loading di tengah layar saat refresh
            if (storyPagingItems.loadState.refresh is androidx.paging.LoadState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (storyPagingItems.itemCount == 0 &&
                storyPagingItems.loadState.refresh !is androidx.paging.LoadState.Loading &&
                storyPagingItems.loadState.append !is androidx.paging.LoadState.Loading
            ) {
                Text(
                    text = "Tidak ada cerita ditemukan.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex }
                .collect { firstVisibleItemIndex ->
                    if (firstVisibleItemIndex == storyPagingItems.itemCount - 1) {
                        // Jika sudah mencapai akhir daftar, tunggu pengguna scroll ulang untuk memuat data
                        println("Pengguna di akhir daftar, tunggu scroll berikutnya untuk memuat data")
                    }
                }
        }
    }
}




