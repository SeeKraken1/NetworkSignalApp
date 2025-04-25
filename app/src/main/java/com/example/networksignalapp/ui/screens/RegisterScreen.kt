package com.example.networksignalapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.networksignalapp.viewmodel.AuthState
import com.example.networksignalapp.viewmodel.UserViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: UserViewModel
) {
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var passwordVisible by remember { mutableStateOf(false) }

    // Effect to navigate on successful registration
    LaunchedEffect(authState) {
        if (authState == AuthState.SUCCESS) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { viewModel.updateUsername(it) },
            label = { Text("Username") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "Username")
            },
            modifier = Modifier.fillMaxWidth(),
            isError = authState == AuthState.ERROR
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Password")
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    // Simple text instead of icon to avoid import issues
                    Text(if (passwordVisible) "Hide" else "Show")
                }
            },
            visualTransformation = if (passwordVisible)
                VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            isError = authState == AuthState.ERROR
        )

        // Error message
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.register() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = authState != AuthState.LOADING,
            shape = RoundedCornerShape(8.dp)
        ) {
            if (authState == AuthState.LOADING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onLoginClick) {
            Text("Already have an account? Login")
        }
    }
}