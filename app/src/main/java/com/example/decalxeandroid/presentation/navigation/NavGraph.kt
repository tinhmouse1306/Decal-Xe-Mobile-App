package com.example.decalxeandroid.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.decalxeandroid.di.AppContainer
import com.example.decalxeandroid.presentation.auth.LoginScreen
import com.example.decalxeandroid.presentation.auth.RegisterScreen
import com.example.decalxeandroid.presentation.dashboard.SimpleDashboardScreen
import com.example.decalxeandroid.presentation.payment.PaymentScreen
import com.example.decalxeandroid.presentation.payment.PaymentViewModel

@Composable
fun NavGraph(navController: NavHostController, startDestination: String = Screen.Login.route) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onNavigateToHome = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    }
            )
        }

        composable(Screen.Dashboard.route) {
            SimpleDashboardScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
            )
        }

        composable(
                route = Screen.Payment.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            PaymentScreen(
                    orderId = orderId,
                    viewModel = PaymentViewModel(orderRepository = AppContainer.orderRepository),
                    onNavigateBack = { navController.popBackStack() },
                    onPaymentSuccess = {
                        // Navigate back to order detail or dashboard
                        navController.popBackStack()
                    }
            )
        }
    }
}
