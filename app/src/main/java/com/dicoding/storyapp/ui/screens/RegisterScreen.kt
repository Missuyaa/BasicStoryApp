package com.dicoding.storyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dicoding.storyapp.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    // State untuk input nama, email, dan password
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observasi state dari ViewModel
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage: String? by authViewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Judul Register
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input Nama
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama") },
            modifier = Modifier.fillMaxWidth(),
            isError = name.isBlank() && !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Input Email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = email.isBlank() && !isLoading
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Input Password
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = password.isBlank() && !isLoading
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Register
        Button(
            onClick = {
                authViewModel.register(name, email, password) { isSuccess ->
                    if (isSuccess) {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Pesan Error
        errorMessage?.let {
            Text(
                text = it, // Tampilkan error message jika tidak null
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigasi kembali ke Login
        TextButton(onClick = {
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }) {
            Text("Already have an account? Login")
        }
    }
}
