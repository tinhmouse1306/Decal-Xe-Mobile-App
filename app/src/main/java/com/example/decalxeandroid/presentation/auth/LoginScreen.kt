package com.example.decalxeandroid.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// Removed problematic imports for now

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit, onNavigateToHome: () -> Unit) {
    val viewModel: LoginViewModel =
            androidx.lifecycle.viewmodel.compose.viewModel(factory = LoginViewModelFactory())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {
                onNavigateToHome()
            }
            else -> {}
        }
    }

    Box(
            modifier =
                    Modifier.fillMaxSize()
                            .background(
                                    brush =
                                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                                    colors =
                                                            listOf(
                                                                    MaterialTheme.colorScheme
                                                                            .primary.copy(
                                                                            alpha = 0.1f
                                                                    ),
                                                                    MaterialTheme.colorScheme
                                                                            .background
                                                            )
                                            )
                            )
    ) {

        // Main content
        Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            // Logo and title section
            Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor =
                                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo placeholder
                    Surface(
                            modifier = Modifier.size(80.dp),
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            androidx.compose.material.icons.Icons.Default.DirectionsCar
                            androidx.compose.material3.Icon(
                                    imageVector =
                                            androidx.compose.material.icons.Icons.Default
                                                    .DirectionsCar,
                                    contentDescription = "DecalXe Logo",
                                    modifier = Modifier.size(40.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                            text = "DecalXe",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                            text = "Hệ thống quản lý decal xe chuyên nghiệp",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Login form
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                            ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                            text = "Đăng nhập vào hệ thống",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Tên đăng nhập") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions =
                                    KeyboardOptions(
                                            keyboardType = KeyboardType.Text,
                                            imeAction = ImeAction.Next
                                    ),
                            singleLine = true,
                            leadingIcon = {
                                androidx.compose.material3.Icon(
                                        imageVector =
                                                androidx.compose.material.icons.Icons.Default
                                                        .Person,
                                        contentDescription = "Tên đăng nhập"
                                )
                            },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Mật khẩu") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions =
                                    KeyboardOptions(
                                            keyboardType = KeyboardType.Password,
                                            imeAction = ImeAction.Done
                                    ),
                            singleLine = true,
                            leadingIcon = {
                                androidx.compose.material3.Icon(
                                        imageVector =
                                                androidx.compose.material.icons.Icons.Default.Lock,
                                        contentDescription = "Mật khẩu"
                                )
                            },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    )

                    Button(
                            onClick = { viewModel.login(username, password) },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            enabled =
                                    username.isNotBlank() &&
                                            password.isNotBlank() &&
                                            uiState !is LoginUiState.Loading,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        if (uiState is LoginUiState.Loading) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Đăng nhập", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    if (uiState is LoginUiState.Error) {
                        val errorState = uiState as LoginUiState.Error
                        Card(
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor =
                                                        MaterialTheme.colorScheme.errorContainer
                                        ),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.material3.Icon(
                                        imageVector =
                                                androidx.compose.material.icons.Icons.Default.Error,
                                        contentDescription = "Lỗi",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = errorState.message,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    TextButton(onClick = onNavigateToRegister, modifier = Modifier.fillMaxWidth()) {
                        Text(
                                "Chưa có tài khoản? Đăng ký ngay",
                                style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

// UI State classes moved to LoginViewModel.kt to avoid duplication
