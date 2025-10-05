package com.example.decalxeandroid.presentation.dashboard

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SimpleDashboardScreen(onNavigateToLogin: () -> Unit) {
    val navController = rememberNavController()

    MainScreen(navController = navController, onNavigateToLogin = onNavigateToLogin)
}
