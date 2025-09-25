package com.example.decalxeandroid.presentation.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.decalxeandroid.presentation.navigation.Screen
import com.example.decalxeandroid.presentation.customers.CustomersScreen
import com.example.decalxeandroid.presentation.orders.StoreFilteredOrdersScreen
import com.example.decalxeandroid.presentation.services.ServicesScreen
import com.example.decalxeandroid.presentation.profile.ProfileScreen

@Composable
fun SimpleDashboardNavHost(
    navController: NavHostController,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardHomeScreen()
        }
        
        composable(Screen.Customers.route) {
            CustomersScreen(
                onNavigateToCustomerDetail = { customerId ->
                    // Simplified: Just show a toast or navigate to a simple detail
                },
                onNavigateToAddCustomer = {
                    // Simplified: Just show a toast
                }
            )
        }
        
        composable(Screen.Orders.route) {
            StoreFilteredOrdersScreen(
                onNavigateToOrderDetail = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                },
                onNavigateToCreateOrder = {
                    navController.navigate(Screen.CreateOrder.route)
                }
            )
        }
        
        // Order Detail Screen
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(
                androidx.navigation.navArgument("orderId") { 
                    type = androidx.navigation.NavType.StringType 
                }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            com.example.decalxeandroid.presentation.orders.OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCustomer = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                },
                onNavigateToVehicle = { vehicleId ->
                    // Simplified: Just show a toast for now
                },
                onNavigateToEdit = { orderId ->
                    navController.navigate(Screen.OrderEdit.createRoute(orderId))
                }
            )
        }
        
        // Create Order Screen
        composable(Screen.CreateOrder.route) {
            com.example.decalxeandroid.presentation.orders.CreateOrderScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOrderCreated = { order ->
                    // Navigate to order detail after creation
                    navController.navigate(Screen.OrderDetail.createRoute(order.orderId)) {
                        popUpTo(Screen.CreateOrder.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Services.route) {
            ServicesScreen(
                onNavigateToCreateService = {
                    // Simplified: Just show a toast
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = onNavigateToLogin
            )
        }
    }
}
