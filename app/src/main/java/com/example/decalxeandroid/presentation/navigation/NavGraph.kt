package com.example.decalxeandroid.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.decalxeandroid.presentation.auth.LoginScreen
import com.example.decalxeandroid.presentation.auth.RegisterScreen
import com.example.decalxeandroid.presentation.dashboard.SimpleDashboardScreen
import com.example.decalxeandroid.presentation.customers.CustomersScreen
import com.example.decalxeandroid.presentation.customers.AddCustomerScreen
import com.example.decalxeandroid.presentation.customers.CustomerDetailScreen
import com.example.decalxeandroid.presentation.customers.CustomerEditScreen
import com.example.decalxeandroid.presentation.orders.OrdersScreen
import com.example.decalxeandroid.presentation.orders.OrderDetailScreen
import com.example.decalxeandroid.presentation.orders.OrderEditScreen
import com.example.decalxeandroid.presentation.orders.CreateOrderScreen
import com.example.decalxeandroid.presentation.vehicles.VehiclesScreen
import com.example.decalxeandroid.presentation.vehicles.AddVehicleScreen
import com.example.decalxeandroid.presentation.vehicles.VehicleDetailScreen
import com.example.decalxeandroid.presentation.vehicles.VehicleEditScreen
import com.example.decalxeandroid.presentation.services.ServicesScreen
import com.example.decalxeandroid.presentation.services.CreateServiceScreen
import com.example.decalxeandroid.presentation.profile.ProfileScreen
import com.example.decalxeandroid.presentation.debug.ApiDebugScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
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
    }
}
