package com.dicoding.storyapp.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dicoding.storyapp.animations.AnimatedButton
import com.dicoding.storyapp.viewmodel.StoryViewModel

@Composable
fun AddStoryScreen(
    navController: NavController,
    storyViewModel: StoryViewModel
) {
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedImageUri = uri
            errorMessage = if (uri == null) "Gagal memilih gambar" else null
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        selectedImageUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
            )
        } ?: Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text("Pilih Foto", color = Color.White, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Pilih Foto")
        }

        Spacer(modifier = Modifier.height(16.dp))
        BasicTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.LightGray)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (description.isEmpty()) {
                        Text("Deskripsi cerita", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        AnimatedButton(
            onClick = {
                if (selectedImageUri == null) {
                    errorMessage = "Silakan pilih gambar terlebih dahulu."
                    return@AnimatedButton
                }

                if (description.isEmpty()) {
                    errorMessage = "Deskripsi tidak boleh kosong."
                    return@AnimatedButton
                }

                selectedImageUri?.let { uri ->
                    storyViewModel.uploadStoryWithImage(
                        description = description,
                        imageUri = uri,
                        onComplete = { success ->
                            if (success) {
                                navController.navigate("story_list") {
                                    popUpTo("add_story") { inclusive = true }
                                }
                            } else {
                                errorMessage = "Gagal mengunggah cerita."
                            }
                        }
                    )
                }
            },
            text = "Unggah Cerita"
        )

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = Color.Red)
        }
    }
}

