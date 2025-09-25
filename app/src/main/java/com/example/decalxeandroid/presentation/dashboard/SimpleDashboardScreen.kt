package com.example.decalxeandroid.presentation.dashboard

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.decalxeandroid.presentation.navigation.Screen

@Composable
fun SimpleDashboardScreen(
    onNavigateToLogin: () -> Unit
) {
    val navController = rememberNavController()
    
    MainScreen(
        navController = navController,
        onNavigateToLogin = onNavigateToLogin
    )
}
