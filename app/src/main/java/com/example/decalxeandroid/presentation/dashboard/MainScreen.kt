package com.example.decalxeandroid.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController, onNavigateToLogin: () -> Unit) {
        // Initialize GlobalAuthManager if not already initialized
        LaunchedEffect(Unit) {
                com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.initialize(
                        com.example.decalxeandroid.di.AppContainer.authRepository
                )
        }

        val currentUser by
                com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.currentUser
                        .collectAsState()
        val currentEmployeeInfo by
                com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.currentEmployeeInfo
                        .collectAsState()

        // Modern gradient colors
        val gradientColors = listOf(Color(0xFF667eea), Color(0xFF764ba2), Color(0xFFf093fb))

        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .background(
                                        brush =
                                                Brush.verticalGradient(
                                                        colors = gradientColors,
                                                        startY = 0f,
                                                        endY = 300f
                                                )
                                )
        ) {
                Scaffold(
                        topBar = {
                                ModernTopAppBar(
                                        currentEmployeeInfo = currentEmployeeInfo,
                                        currentUser = currentUser
                                )
                        },
                        bottomBar = {
                                ModernBottomNavigation(
                                        navController = navController,
                                        currentUser = currentUser
                                )
                        },
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                ) { paddingValues ->
                        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                                // Content with modern card design
                                Card(
                                        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
                                        shape =
                                                RoundedCornerShape(
                                                        topStart = 24.dp,
                                                        topEnd = 24.dp
                                                ),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.surface
                                                ),
                                        elevation =
                                                CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                        SimpleDashboardNavHost(
                                                navController = navController,
                                                onNavigateToLogin = onNavigateToLogin,
                                                modifier = Modifier.fillMaxSize()
                                        )
                                }
                        }
                }
        }
}

@Composable
fun ModernTopAppBar(
        currentEmployeeInfo: com.example.decalxeandroid.domain.model.EmployeeInfo?,
        currentUser: com.example.decalxeandroid.domain.model.User?
) {
        Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent,
                shadowElevation = 0.dp
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        // Left side - Greeting
                        Column {
                                Text(
                                        text = getGreeting(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.8f)
                                )
                                Text(
                                        text = currentEmployeeInfo?.fullName
                                                        ?: currentUser?.username ?: "Khách",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                )
                        }

                        // Right side - User avatar
                        Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f)
                        ) {
                                Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                                imageVector = getRoleIcon(currentUser?.role),
                                                contentDescription = "User Role",
                                                modifier = Modifier.size(24.dp),
                                                tint = Color.White
                                        )
                                }
                        }
                }
        }
}

@Composable
fun ModernBottomNavigation(
        navController: NavHostController,
        currentUser: com.example.decalxeandroid.domain.model.User?
) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val bottomNavItems = getBottomNavItems(currentUser?.role)

        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
                NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
                        bottomNavItems.forEach { item ->
                                val selected =
                                        currentDestination?.hierarchy?.any {
                                                it.route == item.route
                                        } == true

                                NavigationBarItem(
                                        icon = {
                                                Surface(
                                                        shape = CircleShape,
                                                        color =
                                                                if (selected) {
                                                                        Color(0xFF667eea)
                                                                } else Color.Transparent,
                                                        modifier = Modifier.size(40.dp)
                                                ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                                Icon(
                                                                        imageVector = item.icon,
                                                                        contentDescription =
                                                                                item.title,
                                                                        tint =
                                                                                if (selected)
                                                                                        Color.White
                                                                                else
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant,
                                                                        modifier =
                                                                                Modifier.size(20.dp)
                                                                )
                                                        }
                                                }
                                        },
                                        label = {
                                                Text(
                                                        item.title,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color =
                                                                if (selected) Color(0xFF667eea)
                                                                else
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                )
                                        },
                                        selected = selected,
                                        onClick = {
                                                navController.navigate(item.route) {
                                                        popUpTo(
                                                                navController
                                                                        .graph
                                                                        .startDestinationId
                                                        ) { saveState = true }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                }
                                        },
                                        colors =
                                                NavigationBarItemDefaults.colors(
                                                        selectedIconColor = Color.White,
                                                        unselectedIconColor =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant,
                                                        selectedTextColor = Color(0xFF667eea),
                                                        unselectedTextColor =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant,
                                                        indicatorColor = Color.Transparent
                                                )
                                )
                        }
                }
        }
}

@Composable
fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
                in 5..11 -> "Chào buổi sáng"
                in 12..17 -> "Chào buổi chiều"
                in 18..22 -> "Chào buổi tối"
                else -> "Chào buổi đêm"
        }
}

data class NavigationItem(val title: String, val route: String, val icon: ImageVector)

fun getBottomNavItems(
        role: com.example.decalxeandroid.domain.model.UserRole?
): List<NavigationItem> {
        return when (role) {
                com.example.decalxeandroid.domain.model.UserRole.SALES ->
                        listOf(
                                NavigationItem("Trang chủ", "dashboard", Icons.Default.Home),
                                NavigationItem("Khách hàng", "customers", Icons.Default.People),
                                NavigationItem("Đơn hàng", "orders", Icons.Default.ShoppingCart),
                                NavigationItem("Dịch vụ", "services", Icons.Default.DesignServices),
                                NavigationItem("Hồ sơ", "profile", Icons.Default.Person)
                        )
                com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN ->
                        listOf(
                                NavigationItem("Trang chủ", "dashboard", Icons.Default.Home),
                                NavigationItem(
                                        "Lắp đặt",
                                        "technician_installation",
                                        Icons.Default.Build
                                ),
                                NavigationItem("Hồ sơ", "profile", Icons.Default.Person)
                        )
                com.example.decalxeandroid.domain.model.UserRole.CUSTOMER ->
                        listOf(
                                NavigationItem("Trang chủ", "dashboard", Icons.Default.Home),
                                NavigationItem("Đơn hàng", "orders", Icons.Default.ShoppingCart),
                                NavigationItem("Dịch vụ", "services", Icons.Default.DesignServices),
                                NavigationItem("Hỗ trợ", "support", Icons.Default.SupportAgent),
                                NavigationItem("Hồ sơ", "profile", Icons.Default.Person)
                        )
                else ->
                        listOf(
                                NavigationItem("Trang chủ", "dashboard", Icons.Default.Home),
                                NavigationItem("Đơn hàng", "orders", Icons.Default.ShoppingCart),
                                NavigationItem("Hồ sơ", "profile", Icons.Default.Person)
                        )
        }
}

fun getRoleDisplayName(role: com.example.decalxeandroid.domain.model.UserRole?): String {
        return when (role) {
                com.example.decalxeandroid.domain.model.UserRole.SALES -> "Sales Staff"
                com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN -> "Kỹ thuật viên"
                com.example.decalxeandroid.domain.model.UserRole.CUSTOMER -> "Khách hàng"
                else -> "Người dùng"
        }
}

fun getRoleIcon(role: com.example.decalxeandroid.domain.model.UserRole?): ImageVector {
        return when (role) {
                com.example.decalxeandroid.domain.model.UserRole.SALES -> Icons.Default.Person
                com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN -> Icons.Default.Build
                com.example.decalxeandroid.domain.model.UserRole.CUSTOMER -> Icons.Default.Person
                else -> Icons.Default.Person
        }
}
