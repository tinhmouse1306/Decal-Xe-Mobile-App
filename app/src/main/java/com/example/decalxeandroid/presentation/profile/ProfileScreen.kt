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
fun ProfileScreen(
    onNavigateToLogin: () -> Unit
) {
    // Initialize GlobalAuthManager if not already initialized
    LaunchedEffect(Unit) {
        com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.initialize(com.example.decalxeandroid.di.AppContainer.authRepository)
    }
    
    val currentUser by com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.currentUser.collectAsState()
    val isLoggedIn by com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.isLoggedIn.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "No User",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Không có thông tin người dùng",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Vui lòng đăng nhập lại",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = onNavigateToLogin
                            ) {
                                Text("Đăng nhập")
                            }
                        }
                    }
                }
                else -> {
                    val user = currentUser
                    
                    // Profile Header
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Avatar
                            Surface(
                                modifier = Modifier.size(80.dp),
                                shape = MaterialTheme.shapes.large,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user?.fullName?.firstOrNull()?.uppercase() ?: "U",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Name
                            Text(
                                text = user?.fullName ?: "Unknown User",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // Role with better display
                            user?.role?.let { role ->
                                Surface(
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                    color = when (role) {
                                        com.example.decalxeandroid.domain.model.UserRole.SALES -> MaterialTheme.colorScheme.primaryContainer
                                        com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN -> MaterialTheme.colorScheme.secondaryContainer
                                        com.example.decalxeandroid.domain.model.UserRole.CUSTOMER -> MaterialTheme.colorScheme.tertiaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Text(
                                        text = getRoleDisplayName(role),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = when (role) {
                                            com.example.decalxeandroid.domain.model.UserRole.SALES -> MaterialTheme.colorScheme.onPrimaryContainer
                                            com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN -> MaterialTheme.colorScheme.onSecondaryContainer
                                            com.example.decalxeandroid.domain.model.UserRole.CUSTOMER -> MaterialTheme.colorScheme.onTertiaryContainer
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    
                    // Profile Details
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Thông tin cá nhân",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Email
                            ProfileInfoItem(
                                icon = Icons.Default.Email,
                                label = "Email",
                                value = user?.email ?: "Chưa có thông tin"
                            )
                            
                            // Phone
                            ProfileInfoItem(
                                icon = Icons.Default.Phone,
                                label = "Số điện thoại",
                                value = user?.phoneNumber ?: "Chưa có thông tin"
                            )
                            
                            // Address
                            ProfileInfoItem(
                                icon = Icons.Default.LocationOn,
                                label = "Địa chỉ",
                                value = "Chưa có thông tin"
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Role-specific Information
                    user?.role?.let { role ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = getRoleIcon(role),
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Chức năng ${getRoleDisplayName(role)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                getRoleResponsibilities(role).forEach { responsibility ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "• ",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = responsibility,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    
                    // Account Information
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Thông tin tài khoản",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Username
                            ProfileInfoItem(
                                icon = Icons.Default.Person,
                                label = "Tên đăng nhập",
                                value = user?.username ?: "Chưa có thông tin"
                            )
                            
                            // Account Status
                            ProfileInfoItem(
                                icon = Icons.Default.CheckCircle,
                                label = "Trạng thái tài khoản",
                                value = if (user?.isActive == true) "Hoạt động" else "Không hoạt động"
                            )
                            
                            // Created Date
                            ProfileInfoItem(
                                icon = Icons.Default.DateRange,
                                label = "Ngày tạo tài khoản",
                                value = user?.createdAt ?: "Chưa có thông tin"
                            )
                            
                            // Last Login
                            ProfileInfoItem(
                                icon = Icons.Default.AccessTime,
                                label = "Lần đăng nhập cuối",
                                value = user?.lastLoginAt ?: "Chưa có thông tin"
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Actions
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Thao tác",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Change Password
                            Button(
                                onClick = { /* TODO: Implement change password */ },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Đổi mật khẩu")
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Logout
                            OutlinedButton(
                                onClick = {
                                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                        com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.logout()
                                        onNavigateToLogin()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Helper functions for role display
fun getRoleDisplayName(role: com.example.decalxeandroid.domain.model.UserRole): String {
    return when (role) {
        com.example.decalxeandroid.domain.model.UserRole.SALES -> "Nhân viên Bán hàng"
        com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN -> "Kỹ thuật viên Lắp đặt"
        com.example.decalxeandroid.domain.model.UserRole.CUSTOMER -> "Khách hàng"
        com.example.decalxeandroid.domain.model.UserRole.ADMIN -> "Quản trị viên"
        com.example.decalxeandroid.domain.model.UserRole.MANAGER -> "Quản lý"
    }
}

fun getRoleIcon(role: com.example.decalxeandroid.domain.model.UserRole): androidx.compose.ui.graphics.vector.ImageVector {
    return when (role) {
        com.example.decalxeandroid.domain.model.UserRole.SALES -> Icons.Default.ShoppingCart
        com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN -> Icons.Default.Build
        com.example.decalxeandroid.domain.model.UserRole.CUSTOMER -> Icons.Default.Person
        com.example.decalxeandroid.domain.model.UserRole.ADMIN -> Icons.Default.AdminPanelSettings
        com.example.decalxeandroid.domain.model.UserRole.MANAGER -> Icons.Default.ManageAccounts
    }
}

fun getRoleResponsibilities(role: com.example.decalxeandroid.domain.model.UserRole): List<String> {
    return when (role) {
        com.example.decalxeandroid.domain.model.UserRole.SALES -> listOf(
            "Tư vấn và tiếp nhận yêu cầu từ khách hàng",
            "Ghi nhận thông tin khách hàng và đơn hàng",
            "Giới thiệu dịch vụ decal và mẫu thiết kế có sẵn",
            "Theo dõi và báo cáo hiệu suất bán hàng",
            "Hướng dẫn khách hàng về quy trình dịch vụ decal xe",
            "Tạo đơn hàng và chuyển yêu cầu đến bộ phận thiết kế"
        )
        com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN -> listOf(
            "Áp dụng decal lên xe theo thiết kế đã được duyệt",
            "Kiểm tra chất lượng lắp đặt và bảo vệ lớp decal khỏi các yếu tố ảnh hưởng",
            "Bàn giao sản phẩm hoàn thành cho khách hàng sau khi lắp đặt",
            "Thực hiện điều chỉnh và sửa chữa nếu cần thiết trong quá trình lắp đặt"
        )
        com.example.decalxeandroid.domain.model.UserRole.CUSTOMER -> listOf(
            "Xem thông tin đơn hàng của mình",
            "Theo dõi tiến độ thực hiện đơn hàng",
            "Nhận thông báo về tình trạng đơn hàng",
            "Liên hệ với nhân viên khi cần hỗ trợ"
        )
        // Admin, Manager không được phép truy cập mobile app
        else -> listOf("Không có quyền truy cập ứng dụng mobile")
    }
}
