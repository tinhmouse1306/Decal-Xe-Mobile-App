package com.example.decalxeandroid.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.presentation.customers.CustomerDetailScreen
import com.example.decalxeandroid.presentation.customers.EditCustomerScreen
import com.example.decalxeandroid.presentation.navigation.Screen
import com.example.decalxeandroid.presentation.customers.CustomersScreen
import com.example.decalxeandroid.presentation.customers.AddCustomerScreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToLogin: () -> Unit
) {
    val navController = rememberNavController()
    
    MainScreen(
        navController = navController,
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun DashboardBottomNavigation(
    navController: NavHostController,
    onNavigateToLogin: () -> Unit
) {
    // Get current user to show role-based navigation
    val authManager = remember { com.example.decalxeandroid.domain.usecase.auth.AuthManager(com.example.decalxeandroid.di.AppContainer.authRepository) }
    val currentUser by authManager.currentUser.collectAsState()
    
    // Role-based navigation items
    val allScreens = listOf(
        DashboardBottomNavItem(
            route = Screen.Dashboard.route,
            title = "Dashboard",
            icon = Icons.Default.Dashboard,
            roles = listOf(
                com.example.decalxeandroid.domain.model.UserRole.SALES,
                com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN,
                com.example.decalxeandroid.domain.model.UserRole.CUSTOMER
            )
        ),
        DashboardBottomNavItem(
            route = Screen.Customers.route,
            title = "Khách hàng",
            icon = Icons.Default.People,
            roles = listOf(
                com.example.decalxeandroid.domain.model.UserRole.SALES
            )
        ),
        DashboardBottomNavItem(
            route = Screen.Orders.route,
            title = "Đơn hàng",
            icon = Icons.Default.ShoppingCart,
            roles = listOf(
                com.example.decalxeandroid.domain.model.UserRole.SALES,
                com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN,
                com.example.decalxeandroid.domain.model.UserRole.CUSTOMER
            )
        ),
        DashboardBottomNavItem(
            route = Screen.Vehicles.route,
            title = "Xe",
            icon = Icons.Default.DirectionsCar,
            roles = listOf(
                com.example.decalxeandroid.domain.model.UserRole.SALES,
                com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN
            )
        ),
        DashboardBottomNavItem(
            route = Screen.Services.route,
            title = "Dịch vụ",
            icon = Icons.Default.Build,
            roles = listOf(
                com.example.decalxeandroid.domain.model.UserRole.SALES,
                com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN
            )
        ),
        DashboardBottomNavItem(
            route = Screen.Profile.route,
            title = "Hồ sơ",
            icon = Icons.Default.Person,
            roles = listOf(
                com.example.decalxeandroid.domain.model.UserRole.SALES,
                com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN,
                com.example.decalxeandroid.domain.model.UserRole.CUSTOMER
            )
        ),
        DashboardBottomNavItem(
            route = Screen.ApiDebug.route,
            title = "Debug",
            icon = Icons.Default.Settings,
            roles = listOf(
                com.example.decalxeandroid.domain.model.UserRole.SALES,
                com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN
            )
        )
    )
    
    // Filter screens based on user role
    val screens = allScreens.filter { screen ->
        currentUser?.role in screen.roles
    }
    
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        screens.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            
            NavigationBarItem(
                icon = {
                    Surface(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                label = { 
                    Text(
                        screen.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    ) 
                },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
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

@Composable
fun DashboardNavHost(
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
            // Get current user to show role-based dashboard
            val authManager = remember { com.example.decalxeandroid.domain.usecase.auth.AuthManager(com.example.decalxeandroid.di.AppContainer.authRepository) }
            val currentUser by authManager.currentUser.collectAsState()
            
            when (currentUser?.role) {
                com.example.decalxeandroid.domain.model.UserRole.SALES -> {
                    val salesViewModel = remember { com.example.decalxeandroid.presentation.sales.SalesViewModel(
                        getSalesEmployeeInfoUseCase = com.example.decalxeandroid.domain.usecase.sales.GetSalesEmployeeInfoUseCase(com.example.decalxeandroid.di.AppContainer.employeeRepository),
                        getSalesStoreOrdersUseCase = com.example.decalxeandroid.domain.usecase.sales.GetSalesStoreOrdersUseCase(com.example.decalxeandroid.di.AppContainer.orderRepository),
                        createOrderUseCase = com.example.decalxeandroid.domain.usecase.sales.CreateOrderUseCase(com.example.decalxeandroid.di.AppContainer.orderRepository),
                        getDecalServicesUseCase = com.example.decalxeandroid.domain.usecase.sales.GetDecalServicesUseCase(com.example.decalxeandroid.di.AppContainer.decalServiceRepository),
                        getCustomersUseCase = com.example.decalxeandroid.domain.usecase.customer.GetCustomersUseCase(com.example.decalxeandroid.di.AppContainer.customerRepository),
                        createCustomerUseCase = com.example.decalxeandroid.domain.usecase.customer.CreateCustomerUseCase(com.example.decalxeandroid.di.AppContainer.customerRepository),
                        getCustomerVehiclesUseCase = com.example.decalxeandroid.domain.usecase.vehicle.GetCustomerVehiclesUseCase(com.example.decalxeandroid.di.AppContainer.customerVehicleRepository),
                        createCustomerVehicleUseCase = com.example.decalxeandroid.domain.usecase.vehicle.CreateCustomerVehicleUseCase(com.example.decalxeandroid.di.AppContainer.customerVehicleRepository)
                    ) }
                    
                    com.example.decalxeandroid.presentation.sales.SalesDashboardScreen(
                        viewModel = salesViewModel,
                        onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                        onNavigateToCustomers = { navController.navigate(Screen.Customers.route) },
                        onNavigateToVehicles = { navController.navigate(Screen.Vehicles.route) },
                        onNavigateToServices = { navController.navigate(Screen.Services.route) },
                        onNavigateToCreateOrder = { navController.navigate(Screen.SalesCreateOrder.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) }
                    )
                }
                else -> {
                    DashboardHomeScreen(
                        onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                        onNavigateToCustomers = { navController.navigate(Screen.Customers.route) },
                        onNavigateToCreateOrder = { navController.navigate(Screen.CreateOrder.route) },
                        onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                        onNavigateToServices = { navController.navigate(Screen.Services.route) }
                    )
                }
            }
        }
        composable(Screen.Customers.route) {
            CustomersScreen(
                onNavigateToCustomerDetail = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                },
                onNavigateToAddCustomer = {
                    navController.navigate(Screen.AddCustomer.route)
                }
            )
        }
        composable(Screen.Orders.route) {
            OrdersScreen(
                onNavigateToOrderDetail = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                },
                onNavigateToCreateOrder = {
                    navController.navigate(Screen.CreateOrder.route)
                }
            )
        }
        composable(Screen.Services.route) {
            ServicesScreen(
                onNavigateToCreateService = {
                    navController.navigate(Screen.CreateService.route)
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToLogin = onNavigateToLogin
            )
        }
        
        // Detail screens
        composable(
            route = Screen.CustomerDetail.route,
            arguments = listOf(
                navArgument("customerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            CustomerDetailScreen(
                customerId = customerId,
                onNavigateBack = {
                    navController.popBackStack()
                },
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
        
        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            println("DashboardScreen: Navigating to OrderDetail with orderId: '$orderId'")
            
            if (orderId.isEmpty()) {
                println("DashboardScreen: WARNING - orderId is empty!")
            }
            
            OrderDetailScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCustomer = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                },
                onNavigateToVehicle = { vehicleId ->
                    navController.navigate(Screen.VehicleDetail.createRoute(vehicleId))
                },
                onNavigateToEdit = { orderIdToEdit ->
                    navController.navigate(Screen.OrderEdit.createRoute(orderIdToEdit))
                }
            )
        }
        
        composable(
            route = Screen.VehicleDetail.route,
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailScreen(
                vehicleId = vehicleId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCustomer = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                },
                onNavigateToOrder = { orderId ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderId))
                },
                onNavigateToEdit = { vehicleIdToEdit ->
                    navController.navigate(Screen.VehicleEdit.createRoute(vehicleIdToEdit))
                }
            )
        }
        
        composable(
            route = Screen.VehicleEdit.route,
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleEditScreen(
                vehicleId = vehicleId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { vehicleIdToDetail ->
                    navController.navigate(Screen.VehicleDetail.createRoute(vehicleIdToDetail)) {
                        popUpTo(Screen.VehicleDetail.createRoute(vehicleIdToDetail)) { inclusive = true }
                    }
                }
            )
        }
        
        // Create/Add screens
        composable(Screen.AddCustomer.route) {
            AddCustomerScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCustomerCreated = { customer ->
                    // Navigate back to customers list
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = Screen.CustomerEdit.route,
            arguments = listOf(
                navArgument("customerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            EditCustomerScreen(
                customerId = customerId,
                onNavigateBack = {
                    navController.popBackStack()
                },
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
        
        composable(
            route = Screen.OrderEdit.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderEditScreen(
                orderId = orderId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { orderIdToDetail ->
                    navController.navigate(Screen.OrderDetail.createRoute(orderIdToDetail)) {
                        popUpTo(Screen.OrderDetail.createRoute(orderIdToDetail)) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.CreateOrder.route) {
            CreateOrderScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOrderCreated = { order ->
                    // Navigate back to orders list
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.SalesCreateOrder.route) {
            val authManager = remember { com.example.decalxeandroid.domain.usecase.auth.AuthManager(com.example.decalxeandroid.di.AppContainer.authRepository) }
            val currentUser by authManager.currentUser.collectAsState()
            
            currentUser?.let { user ->
                val salesViewModel = remember { com.example.decalxeandroid.presentation.sales.SalesViewModel(
                    getSalesEmployeeInfoUseCase = com.example.decalxeandroid.domain.usecase.sales.GetSalesEmployeeInfoUseCase(com.example.decalxeandroid.di.AppContainer.employeeRepository),
                    getSalesStoreOrdersUseCase = com.example.decalxeandroid.domain.usecase.sales.GetSalesStoreOrdersUseCase(com.example.decalxeandroid.di.AppContainer.orderRepository),
                    createOrderUseCase = com.example.decalxeandroid.domain.usecase.sales.CreateOrderUseCase(com.example.decalxeandroid.di.AppContainer.orderRepository),
                    getDecalServicesUseCase = com.example.decalxeandroid.domain.usecase.sales.GetDecalServicesUseCase(com.example.decalxeandroid.di.AppContainer.decalServiceRepository),
                    getCustomersUseCase = com.example.decalxeandroid.domain.usecase.customer.GetCustomersUseCase(com.example.decalxeandroid.di.AppContainer.customerRepository),
                    createCustomerUseCase = com.example.decalxeandroid.domain.usecase.customer.CreateCustomerUseCase(com.example.decalxeandroid.di.AppContainer.customerRepository),
                    getCustomerVehiclesUseCase = com.example.decalxeandroid.domain.usecase.vehicle.GetCustomerVehiclesUseCase(com.example.decalxeandroid.di.AppContainer.customerVehicleRepository),
                    createCustomerVehicleUseCase = com.example.decalxeandroid.domain.usecase.vehicle.CreateCustomerVehicleUseCase(com.example.decalxeandroid.di.AppContainer.customerVehicleRepository)
                ) }
                
                com.example.decalxeandroid.presentation.sales.CreateOrderScreen(
                    viewModel = salesViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToCustomer = {
                        navController.navigate(Screen.AddCustomer.route)
                    },
                    onNavigateToVehicle = {
                        navController.navigate(Screen.AddVehicle.route)
                    }
                )
            }
        }
        
        composable(Screen.AddVehicle.route) {
            AddVehicleScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVehicleCreated = { vehicle ->
                    // Navigate back to vehicles list
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.CreateService.route) {
            CreateServiceScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onServiceCreated = {
                    // Navigate back to services list and refresh
                    navController.popBackStack()
                }
            )
        }
    }
}

data class DashboardBottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val roles: List<com.example.decalxeandroid.domain.model.UserRole> = listOf()
)
