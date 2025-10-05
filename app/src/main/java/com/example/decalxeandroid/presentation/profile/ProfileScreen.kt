package com.example.decalxeandroid.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onNavigateToLogin: () -> Unit, viewModel: ProfileViewModel = viewModel()) {
        // Initialize GlobalAuthManager if not already initialized
        LaunchedEffect(Unit) {
                com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.initialize(
                        com.example.decalxeandroid.di.AppContainer.authRepository
                )
        }

        val currentUser by
                com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.currentUser
                        .collectAsState()
        val isLoggedIn by
                com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.isLoggedIn
                        .collectAsState()
        val profileUiState by viewModel.uiState.collectAsState()

        // Load employee data when user is available
        LaunchedEffect(currentUser?.accountId) {
                currentUser?.accountId?.let { accountId ->
                        viewModel.loadEmployeeByAccountId(accountId)
                }
        }

        Scaffold(topBar = { TopAppBar(title = { Text("Hồ sơ") }) }) { paddingValues ->
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(paddingValues)
                                        .verticalScroll(rememberScrollState())
                ) {
                        when {
                                currentUser == null -> {
                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Column(
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally,
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(16.dp)
                                                ) {
                                                        Icon(
                                                                imageVector = Icons.Default.Person,
                                                                contentDescription = "No User",
                                                                modifier = Modifier.size(64.dp),
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                        Text(
                                                                text =
                                                                        "Không có thông tin người dùng",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .headlineSmall,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                        Text(
                                                                text = "Vui lòng đăng nhập lại",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodyMedium,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        )
                                                        Button(onClick = onNavigateToLogin) {
                                                                Text("Đăng nhập")
                                                        }
                                                }
                                        }
                                }
                                else -> {
                                        val user = currentUser

                                        // Profile Header
                                        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                                Column(
                                                        modifier = Modifier.padding(24.dp),
                                                        horizontalAlignment =
                                                                Alignment.CenterHorizontally
                                                ) {
                                                        // Avatar
                                                        Surface(
                                                                modifier = Modifier.size(80.dp),
                                                                shape = MaterialTheme.shapes.large,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .primaryContainer
                                                        ) {
                                                                Box(
                                                                        modifier =
                                                                                Modifier.fillMaxSize(),
                                                                        contentAlignment =
                                                                                Alignment.Center
                                                                ) {
                                                                        Text(
                                                                                text =
                                                                                        user?.fullName
                                                                                                ?.firstOrNull()
                                                                                                ?.uppercase()
                                                                                                ?: "U",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .headlineLarge,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onPrimaryContainer
                                                                        )
                                                                }
                                                        }

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        // Name
                                                        Text(
                                                                text = user?.fullName
                                                                                ?: "Unknown User",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .headlineSmall,
                                                                fontWeight = FontWeight.Bold
                                                        )

                                                        // Role with better display
                                                        user?.role?.let { role ->
                                                                Surface(
                                                                        shape =
                                                                                androidx.compose
                                                                                        .foundation
                                                                                        .shape
                                                                                        .RoundedCornerShape(
                                                                                                16.dp
                                                                                        ),
                                                                        color =
                                                                                when (role) {
                                                                                        com.example
                                                                                                .decalxeandroid
                                                                                                .domain
                                                                                                .model
                                                                                                .UserRole
                                                                                                .SALES ->
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .primaryContainer
                                                                                        com.example
                                                                                                .decalxeandroid
                                                                                                .domain
                                                                                                .model
                                                                                                .UserRole
                                                                                                .TECHNICIAN ->
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .secondaryContainer
                                                                                        com.example
                                                                                                .decalxeandroid
                                                                                                .domain
                                                                                                .model
                                                                                                .UserRole
                                                                                                .CUSTOMER ->
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .tertiaryContainer
                                                                                        else ->
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .surfaceVariant
                                                                                },
                                                                        modifier =
                                                                                Modifier.padding(
                                                                                        vertical =
                                                                                                4.dp
                                                                                )
                                                                ) {
                                                                        Text(
                                                                                text =
                                                                                        when (role
                                                                                        ) {
                                                                                                com.example
                                                                                                        .decalxeandroid
                                                                                                        .domain
                                                                                                        .model
                                                                                                        .UserRole
                                                                                                        .SALES ->
                                                                                                        "Nhân viên Bán hàng"
                                                                                                com.example
                                                                                                        .decalxeandroid
                                                                                                        .domain
                                                                                                        .model
                                                                                                        .UserRole
                                                                                                        .TECHNICIAN ->
                                                                                                        "Kỹ thuật viên"
                                                                                                com.example
                                                                                                        .decalxeandroid
                                                                                                        .domain
                                                                                                        .model
                                                                                                        .UserRole
                                                                                                        .CUSTOMER ->
                                                                                                        "Khách hàng"
                                                                                                com.example
                                                                                                        .decalxeandroid
                                                                                                        .domain
                                                                                                        .model
                                                                                                        .UserRole
                                                                                                        .ADMIN ->
                                                                                                        "Quản trị viên"
                                                                                                com.example
                                                                                                        .decalxeandroid
                                                                                                        .domain
                                                                                                        .model
                                                                                                        .UserRole
                                                                                                        .MANAGER ->
                                                                                                        "Quản lý"
                                                                                                else ->
                                                                                                        "Không xác định"
                                                                                        },
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .labelLarge,
                                                                                color =
                                                                                        when (role
                                                                                        ) {
                                                                                                com.example
                                                                                                        .decalxeandroid
                                                                                                        .domain
                                                                                                        .model
                                                                                                        .UserRole
                                                                                                        .SALES ->
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onPrimaryContainer
                                                                                                com.example
                                                                                                        .decalxeandroid
                                                                                                        .domain
                                                                                                        .model
                                                                                                        .UserRole
                                                                                                        .TECHNICIAN ->
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSecondaryContainer
                                                                                                com.example
                                                                                                        .decalxeandroid
                                                                                                        .domain
                                                                                                        .model
                                                                                                        .UserRole
                                                                                                        .CUSTOMER ->
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onTertiaryContainer
                                                                                                else ->
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant
                                                                                        },
                                                                                modifier =
                                                                                        Modifier.padding(
                                                                                                horizontal =
                                                                                                        16.dp,
                                                                                                vertical =
                                                                                                        6.dp
                                                                                        ),
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Medium
                                                                        )
                                                                }
                                                        }
                                                }
                                        }

                                        // Profile Details
                                        Card(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(horizontal = 16.dp)
                                        ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                        Row(
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically
                                                        ) {
                                                                Text(
                                                                        text = "Thông tin cá nhân",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleMedium,
                                                                        fontWeight = FontWeight.Bold
                                                                )

                                                                if (profileUiState.isLoading) {
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.width(
                                                                                                8.dp
                                                                                        )
                                                                        )
                                                                        CircularProgressIndicator(
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                16.dp
                                                                                        ),
                                                                                strokeWidth = 2.dp
                                                                        )
                                                                }
                                                        }

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        // Email - from employee data
                                                        ProfileInfoItem(
                                                                icon = Icons.Default.Email,
                                                                label = "Email",
                                                                value =
                                                                        profileUiState
                                                                                .currentEmployee
                                                                                ?.email
                                                                                ?: user?.email
                                                                                        ?: "Chưa có thông tin"
                                                        )

                                                        // Phone - from employee data
                                                        ProfileInfoItem(
                                                                icon = Icons.Default.Phone,
                                                                label = "Số điện thoại",
                                                                value =
                                                                        profileUiState
                                                                                .currentEmployee
                                                                                ?.phoneNumber
                                                                                ?: user?.phoneNumber
                                                                                        ?: "Chưa có thông tin"
                                                        )

                                                        // Address - from employee data
                                                        ProfileInfoItem(
                                                                icon = Icons.Default.LocationOn,
                                                                label = "Địa chỉ",
                                                                value =
                                                                        profileUiState
                                                                                .currentEmployee
                                                                                ?.address
                                                                                ?: "Chưa có thông tin"
                                                        )

                                                        // Show error if any
                                                        profileUiState.error?.let { error ->
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        8.dp
                                                                                )
                                                                )
                                                                Text(
                                                                        text = "⚠️ $error",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        color =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .error
                                                                )
                                                        }
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Account Information
                                        Card(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(horizontal = 16.dp)
                                        ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                        Text(
                                                                text = "Thông tin tài khoản",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium,
                                                                fontWeight = FontWeight.Bold
                                                        )

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        // Username
                                                        ProfileInfoItem(
                                                                icon = Icons.Default.Person,
                                                                label = "Tên đăng nhập",
                                                                value = user?.username
                                                                                ?: "Chưa có thông tin"
                                                        )

                                                        // Account Status
                                                        ProfileInfoItem(
                                                                icon = Icons.Default.CheckCircle,
                                                                label = "Trạng thái tài khoản",
                                                                value =
                                                                        if (user?.isActive == true)
                                                                                "Hoạt động"
                                                                        else "Không hoạt động"
                                                        )
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Actions
                                        Card(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .padding(horizontal = 16.dp)
                                        ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                        Text(
                                                                text = "Thao tác",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium,
                                                                fontWeight = FontWeight.Bold
                                                        )

                                                        Spacer(modifier = Modifier.height(16.dp))

                                                        // Change Password
                                                        Button(
                                                                onClick = { /* TODO: Implement change password */
                                                                },
                                                                modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default.Lock,
                                                                        contentDescription = null,
                                                                        modifier =
                                                                                Modifier.size(18.dp)
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(8.dp)
                                                                )
                                                                Text("Đổi mật khẩu")
                                                        }

                                                        Spacer(modifier = Modifier.height(8.dp))

                                                        // Logout
                                                        OutlinedButton(
                                                                onClick = {
                                                                        kotlinx.coroutines
                                                                                .CoroutineScope(
                                                                                        kotlinx.coroutines
                                                                                                .Dispatchers
                                                                                                .Main
                                                                                )
                                                                                .launch {
                                                                                        com.example
                                                                                                .decalxeandroid
                                                                                                .domain
                                                                                                .usecase
                                                                                                .auth
                                                                                                .GlobalAuthManager
                                                                                                .logout()
                                                                                        onNavigateToLogin()
                                                                                }
                                                                },
                                                                modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                                Icon(
                                                                        imageVector =
                                                                                Icons.Default
                                                                                        .Logout,
                                                                        contentDescription = null,
                                                                        modifier =
                                                                                Modifier.size(18.dp)
                                                                )
                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.width(8.dp)
                                                                )
                                                                Text("Đăng xuất")
                                                        }
                                                }
                                        }

                                        Spacer(modifier = Modifier.height(32.dp))
                                }
                        }
                }
        }
}

@Composable
fun ProfileInfoItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        label: String,
        value: String
) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = value, style = MaterialTheme.typography.bodyMedium)
                }
        }
}
