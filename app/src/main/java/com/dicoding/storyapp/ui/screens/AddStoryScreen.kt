package com.dicoding.storyapp.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.dicoding.storyapp.viewmodel.StoryViewModel
import java.io.File

@Composable
fun AddStoryScreen(
    navController: NavHostController,
    storyViewModel: StoryViewModel = viewModel()
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val isLoading by storyViewModel.isLoading.collectAsState()
    val errorMessage by storyViewModel.errorMessage.collectAsState()

    // Launcher untuk memilih file dari galeri
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Input deskripsi cerita
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Deskripsi Cerita") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Tombol untuk memilih gambar
        Button(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Pilih Gambar")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Tampilkan nama file jika gambar sudah dipilih
        imageUri?.let {
            Text(text = "Gambar dipilih: ${it.lastPathSegment}")
        } ?: run {
            Text(text = "Belum ada gambar dipilih.")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol upload cerita
        Button(
            onClick = {
                if (imageUri != null) {
                    val file = uriToFile(imageUri!!, context) // Konversi URI ke File
                    storyViewModel.uploadStoryWithImage(description, file) { success ->
                        if (success) {
                            navController.navigate("story_list") {
                                popUpTo("add_story") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Gagal mengunggah cerita.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Harap pilih gambar.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = description.isNotBlank() && imageUri != null && !isLoading
        ) {
            Text(text = "Upload Cerita")
        }

        // Loading indicator
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        // Pesan error
        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
        }
    }
}

/**
 * Konversi URI ke File
 */
fun uriToFile(uri: Uri, context: Context): File {
    val contentResolver = context.contentResolver
    val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
    contentResolver.openInputStream(uri)?.use { inputStream ->
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return tempFile
}
