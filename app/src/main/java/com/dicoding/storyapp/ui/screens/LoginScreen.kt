package com.dicoding.storyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.dicoding.storyapp.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // Callback jika login berhasil
    onRegisterClick: () -> Unit, // Callback untuk navigasi ke register
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = email.isBlank() && !isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input password
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = password.isBlank() && !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol login
        Button(
            onClick = {
                authViewModel.login(email, password) { isSuccess ->
                    if (isSuccess) {
                        onLoginSuccess()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }

        // Tampilkan pesan error jika ada
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigasi ke register
        TextButton(onClick = { onRegisterClick() }) {
            Text("Don't have an account? Register")
        }
    }
}
