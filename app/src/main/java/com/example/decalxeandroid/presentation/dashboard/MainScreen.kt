package com.example.decalxeandroid.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    onNavigateToLogin: () -> Unit
) {
    // Initialize GlobalAuthManager if not already initialized
    LaunchedEffect(Unit) {
        com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.initialize(com.example.decalxeandroid.di.AppContainer.authRepository)
    }
    
    val currentUser by com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.currentUser.collectAsState()
    val currentEmployeeInfo by com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.currentEmployeeInfo.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DecalXe",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // User avatar and role indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = currentEmployeeInfo?.fullName ?: currentUser?.username ?: "Khách",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = getRoleIcon(currentUser?.role),
                                    contentDescription = "User Role",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            MainBottomNavigation(
                navController = navController,
                currentUser = currentUser
            )
        }
    ) { paddingValues ->
        SimpleDashboardNavHost(
            navController = navController,
            onNavigateToLogin = onNavigateToLogin,
            modifier = Modifier.padding(paddingValues)
        )
    }
}


@Composable
fun MainBottomNavigation(
    navController: NavHostController,
    currentUser: com.example.decalxeandroid.domain.model.User?
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomNavItems = getBottomNavItems(currentUser?.role)
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            
            NavigationBarItem(
                icon = {
                    Surface(
                        shape = CircleShape,
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                label = { 
                    Text(
                        item.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    ) 
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)


fun getBottomNavItems(role: com.example.decalxeandroid.domain.model.UserRole?): List<NavigationItem> {
    return when (role) {
        com.example.decalxeandroid.domain.model.UserRole.SALES -> listOf(
            NavigationItem("Trang chủ", "dashboard", Icons.Default.Home),
            NavigationItem("Khách hàng", "customers", Icons.Default.People),
            NavigationItem("Đơn hàng", "orders", Icons.Default.ShoppingCart),
            NavigationItem("Dịch vụ", "services", Icons.Default.DesignServices),
            NavigationItem("Hồ sơ", "profile", Icons.Default.Person)
        )
        com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN -> listOf(
            NavigationItem("Trang chủ", "dashboard", Icons.Default.Home),
            NavigationItem("Đơn hàng", "orders", Icons.Default.ShoppingCart),
            NavigationItem("Lắp đặt", "installations", Icons.Default.Build),
            NavigationItem("Kiểm tra", "inspections", Icons.Default.CheckCircle),
            NavigationItem("Hồ sơ", "profile", Icons.Default.Person)
        )
        com.example.decalxeandroid.domain.model.UserRole.CUSTOMER -> listOf(
            NavigationItem("Trang chủ", "dashboard", Icons.Default.Home),
            NavigationItem("Đơn hàng", "orders", Icons.Default.ShoppingCart),
            NavigationItem("Dịch vụ", "services", Icons.Default.DesignServices),
            NavigationItem("Hỗ trợ", "support", Icons.Default.SupportAgent),
            NavigationItem("Hồ sơ", "profile", Icons.Default.Person)
        )
        else -> listOf(
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
