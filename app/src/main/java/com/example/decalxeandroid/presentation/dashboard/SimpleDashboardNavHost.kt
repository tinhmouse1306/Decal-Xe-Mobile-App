package com.example.decalxeandroid.presentation.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.decalxeandroid.di.AppContainer
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.presentation.customers.AddCustomerScreen
import com.example.decalxeandroid.presentation.customers.AddCustomerVehicleScreen
import com.example.decalxeandroid.presentation.customers.CustomerDetailScreen
import com.example.decalxeandroid.presentation.customers.CustomerVehicleDetailScreen
import com.example.decalxeandroid.presentation.customers.CustomersScreen
import com.example.decalxeandroid.presentation.customers.EditCustomerScreen
import com.example.decalxeandroid.presentation.customers.EditCustomerVehicleScreen
import com.example.decalxeandroid.presentation.navigation.Screen
import com.example.decalxeandroid.presentation.orders.StoreFilteredOrdersScreen
import com.example.decalxeandroid.presentation.payment.PaymentScreen
import com.example.decalxeandroid.presentation.payment.PaymentViewModel
import com.example.decalxeandroid.presentation.profile.ProfileScreen
import com.example.decalxeandroid.presentation.services.ServicesScreen

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
        composable(Screen.Dashboard.route) { DashboardHomeScreen() }

        composable(Screen.Customers.route) {
            CustomersScreen(
                    onNavigateToCustomerDetail = { customerId ->
                        navController.navigate("customer_detail/$customerId")
                    },
                    onNavigateToAddCustomer = { navController.navigate("add_customer") }
            )
        }

        // Add Customer Screen (Step 1)
        composable("add_customer") {
            AddCustomerScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCustomerCreated = { customer ->
                        navController.navigate("add_customer_vehicle/${customer.customerId}")
                    }
            )
        }

        // Add Customer Vehicle Screen (Step 2)
        composable(
                route = "add_customer_vehicle/{customerId}",
                arguments =
                        listOf(
                                androidx.navigation.navArgument("customerId") {
                                    type = androidx.navigation.NavType.StringType
                                }
                        )
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            // For now, create a mock customer object
            val customer =
                    com.example.decalxeandroid.domain.model.Customer(
                            customerId = customerId,
                            fullName = "Khách hàng Mới",
                            phoneNumber = null,
                            email = null,
                            address = null,
                            dateOfBirth = null,
                            gender = null,
                            isActive = true,
                            createdAt = "",
                            updatedAt = null
                    )

            AddCustomerVehicleScreen(
                    customer = customer,
                    onNavigateBack = { navController.popBackStack() },
                    onVehicleCreated = { vehicle ->
                        // Navigate back to customers list
                        navController.navigate(Screen.Customers.route) {
                            popUpTo(Screen.Customers.route) { inclusive = true }
                        }
                    },
                    onComplete = {
                        // Navigate back to customers list
                        navController.navigate(Screen.Customers.route) {
                            popUpTo(Screen.Customers.route) { inclusive = true }
                        }
                    }
            )
        }

        composable(Screen.Orders.route) {
            StoreFilteredOrdersScreen(
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate(Screen.OrderDetail.createRoute(orderId))
                    },
                    onNavigateToCreateOrder = { navController.navigate(Screen.CreateOrder.route) }
            )
        }

        // Technician Installation Screen
        composable(Screen.TechnicianInstallation.route) {
            com.example.decalxeandroid.presentation.technician.TechnicianInstallationScreen(
                    onNavigateToOrderDetail = { orderId ->
                        navController.navigate(Screen.TechnicianOrderDetail.createRoute(orderId))
                    }
            )
        }

        // Order Detail Screen
        composable(
                route = Screen.OrderDetail.route,
                arguments =
                        listOf(
                                androidx.navigation.navArgument("orderId") {
                                    type = androidx.navigation.NavType.StringType
                                }
                        )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val viewModel: com.example.decalxeandroid.presentation.orders.OrderDetailViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel(
                            factory =
                                    com.example.decalxeandroid.presentation.orders
                                            .OrderDetailViewModelFactory(
                                                    orderRepository =
                                                            com.example.decalxeandroid.di
                                                                    .AppContainer.orderRepository,
                                                    orderDetailRepository =
                                                            com.example.decalxeandroid.di
                                                                    .AppContainer
                                                                    .orderDetailRepository,
                                                    orderStageHistoryRepository =
                                                            com.example.decalxeandroid.di
                                                                    .AppContainer
                                                                    .orderStageHistoryRepository
                                            )
                    )

            com.example.decalxeandroid.presentation.orders.OrderDetailScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCustomer = { customerId ->
                        navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                    },
                    onNavigateToVehicle = { vehicleId ->
                        navController.navigate(Screen.VehicleDetail.createRoute(vehicleId))
                    },
                    onEditOrderDetail = { orderDetail ->
                        // TODO: Navigate to edit order detail screen
                    },
                    onDeleteOrderDetail = { orderDetail ->
                        // TODO: Show confirmation dialog and delete
                    },
                    onNavigateToPayment = { orderId ->
                        navController.navigate(Screen.Payment.createRoute(orderId))
                    }
            )
        }

        // Create Order Screen
        composable(Screen.CreateOrder.route) {
            com.example.decalxeandroid.presentation.orders.CreateOrderScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onOrderCreated = {
                        // Navigate back to orders list after creation
                        navController.navigate(Screen.Orders.route) {
                            popUpTo(Screen.CreateOrder.route) { inclusive = true }
                        }
                    }
            )
        }

        // Technician Order Detail Screen
        composable(
                route = Screen.TechnicianOrderDetail.route,
                arguments =
                        listOf(
                                androidx.navigation.navArgument("orderId") {
                                    type = androidx.navigation.NavType.StringType
                                }
                        )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val viewModel: com.example.decalxeandroid.presentation.orders.OrderDetailViewModel =
                    androidx.lifecycle.viewmodel.compose.viewModel(
                            factory =
                                    com.example.decalxeandroid.presentation.orders
                                            .OrderDetailViewModelFactory(
                                                    orderRepository =
                                                            com.example.decalxeandroid.di
                                                                    .AppContainer.orderRepository,
                                                    orderDetailRepository =
                                                            com.example.decalxeandroid.di
                                                                    .AppContainer
                                                                    .orderDetailRepository,
                                                    orderStageHistoryRepository =
                                                            com.example.decalxeandroid.di
                                                                    .AppContainer
                                                                    .orderStageHistoryRepository
                                            )
                    )

            com.example.decalxeandroid.presentation.technician.TechnicianOrderDetailScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCustomer = { customerId ->
                        navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                    },
                    onNavigateToVehicle = { vehicleId ->
                        navController.navigate(Screen.VehicleDetail.createRoute(vehicleId))
                    },
                    onEditOrderDetail = { orderDetail ->
                        // TODO: Navigate to edit order detail screen
                    },
                    onDeleteOrderDetail = { orderDetail ->
                        // TODO: Show confirmation dialog and delete
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

        composable(Screen.Profile.route) { ProfileScreen(onNavigateToLogin = onNavigateToLogin) }

        // Customer Detail Screen
        composable(
                route = "customer_detail/{customerId}",
                arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""

            CustomerDetailScreen(
                    customerId = customerId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditCustomer = { customer ->
                        navController.navigate("edit_customer/${customer.customerId}")
                    },
                    onViewVehicleDetail = { vehicle ->
                        navController.navigate("customer_vehicle_detail/${vehicle.vehicleID}")
                    },
                    onAddVehicle = { customer ->
                        navController.navigate("add_customer_vehicle/${customer.customerId}")
                    }
            )
        }

        // Customer Vehicle Detail Screen
        composable(
                route = "customer_vehicle_detail/{vehicleId}",
                arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""

            CustomerVehicleDetailScreen(
                    vehicleId = vehicleId,
                    onNavigateBack = { customerId ->
                        if (customerId != null) {
                            // Navigate back to customer detail
                            navController.navigate("customer_detail/$customerId") {
                                popUpTo("customer_detail/$customerId") { inclusive = true }
                            }
                        } else {
                            // Fallback: navigate back to customer list
                            navController.navigate(Screen.Customers.route) {
                                popUpTo(Screen.Customers.route) { inclusive = true }
                            }
                        }
                    },
                    onEditVehicle = { vehicle ->
                        navController.navigate("edit_customer_vehicle/${vehicle.vehicleID}")
                    },
                    onDeleteVehicle = { vehicle ->
                        // Navigate back to customer detail
                        navController.navigate("customer_detail/${vehicle.customerID}") {
                            popUpTo("customer_detail/${vehicle.customerID}") { inclusive = true }
                        }
                    }
            )
        }

        // Edit Customer Screen
        composable(
                route = "edit_customer/{customerId}",
                arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""

            EditCustomerScreen(
                    customerId = customerId,
                    onNavigateBack = { navController.popBackStack() },
                    onCustomerUpdated = { updatedCustomer ->
                        // Navigate back to customer detail
                        navController.navigate("customer_detail/${updatedCustomer.customerId}") {
                            popUpTo("customer_detail/${updatedCustomer.customerId}") {
                                inclusive = true
                            }
                        }
                    }
            )
        }

        // Edit Customer Vehicle Screen
        composable(
                route = "edit_customer_vehicle/{vehicleId}",
                arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""

            EditCustomerVehicleScreen(
                    vehicleId = vehicleId,
                    onNavigateBack = { navController.popBackStack() },
                    onVehicleUpdated = { updatedVehicle ->
                        // Navigate back to vehicle detail
                        navController.navigate(
                                "customer_vehicle_detail/${updatedVehicle.vehicleID}"
                        ) {
                            popUpTo("customer_vehicle_detail/${updatedVehicle.vehicleID}") {
                                inclusive = true
                            }
                        }
                    }
            )
        }

        // Payment Screen
        composable(
                route = Screen.Payment.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val viewModel = remember {
                PaymentViewModel(orderRepository = AppContainer.orderRepository)
            }
            PaymentScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPaymentSuccess = {
                        // Navigate back to order detail or dashboard
                        navController.popBackStack()
                    }
            )
        }
    }
}
