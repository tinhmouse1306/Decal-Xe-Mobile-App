package com.example.decalxeandroid.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.decalxeandroid.di.AppContainer
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.UserRole
import com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFilteredOrdersScreen(
        onNavigateToOrderDetail: (String) -> Unit,
        onNavigateToCreateOrder: () -> Unit
) {
    val currentUser by GlobalAuthManager.currentUser.collectAsState()
    val currentEmployeeInfo by GlobalAuthManager.currentEmployeeInfo.collectAsState()
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var retryKey by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    // Initialize GlobalAuthManager
    LaunchedEffect(Unit) { GlobalAuthManager.initialize(AppContainer.authRepository) }

    // Load orders based on user role and store
    LaunchedEffect(currentUser, currentEmployeeInfo, retryKey) {
        currentUser?.let { user ->
            try {
                isLoading = true
                error = null

                when (user.role) {
                    UserRole.SALES -> {
                        // For Sales staff, use employee info from GlobalAuthManager
                        val storeId = currentEmployeeInfo?.storeId
                        if (storeId != null) {
                            // Get all orders and filter by storeId on client side
                            AppContainer.orderRepository.getOrders().collect { ordersResult ->
                                when (ordersResult) {
                                    is com.example.decalxeandroid.domain.model.Result.Success -> {
                                        val filteredOrders = ordersResult.data.filter { it.storeId == storeId }
                                        orders = filteredOrders.sortedWith(
                                                compareByDescending<Order> { order ->
                                                        // Ưu tiên: High = 3, Medium = 2, Low = 1, null = 0
                                                        when (order.priority?.lowercase()) {
                                                                "high" -> 3
                                                                "medium" -> 2
                                                                "low" -> 1
                                                                else -> 0
                                                        }
                                                }.thenByDescending { order ->
                                                        // Sắp xếp theo thời gian tạo (createdAt) từ mới đến cũ
                                                        try {
                                                                val format = java.text.SimpleDateFormat(
                                                                        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                                                                        java.util.Locale.getDefault()
                                                                )
                                                                format.parse(order.createdAt)?.time ?: 0L
                                                        } catch (e: Exception) {
                                                                try {
                                                                        val format2 = java.text.SimpleDateFormat(
                                                                                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                                                                                java.util.Locale.getDefault()
                                                                        )
                                                                        format2.parse(order.createdAt)?.time ?: 0L
                                                                } catch (e2: Exception) {
                                                                        0L
                                                                }
                                                        }
                                                }
                                        )
                                        isLoading = false
                                    }
                                    is com.example.decalxeandroid.domain.model.Result.Error -> {
                                        error = ordersResult.message
                                        isLoading = false
                                    }
                                    else -> {
                                        isLoading = false
                                    }
                                }
                            }
                        } else {
                            // If no storeId, show all orders for now
                            AppContainer.orderRepository.getOrders().collect { ordersResult ->
                                when (ordersResult) {
                                    is com.example.decalxeandroid.domain.model.Result.Success -> {
                                        orders = ordersResult.data.sortedWith(
                                                compareByDescending<Order> { order ->
                                                        // Ưu tiên: High = 3, Medium = 2, Low = 1, null = 0
                                                        when (order.priority?.lowercase()) {
                                                                "high" -> 3
                                                                "medium" -> 2
                                                                "low" -> 1
                                                                else -> 0
                                                        }
                                                }.thenByDescending { order ->
                                                        // Sắp xếp theo thời gian tạo (createdAt) từ mới đến cũ
                                                        try {
                                                                val format = java.text.SimpleDateFormat(
                                                                        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                                                                        java.util.Locale.getDefault()
                                                                )
                                                                format.parse(order.createdAt)?.time ?: 0L
                                                        } catch (e: Exception) {
                                                                try {
                                                                        val format2 = java.text.SimpleDateFormat(
                                                                                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                                                                                java.util.Locale.getDefault()
                                                                        )
                                                                        format2.parse(order.createdAt)?.time ?: 0L
                                                                } catch (e2: Exception) {
                                                                        0L
                                                                }
                                                        }
                                                }
                                        )
                                        isLoading = false
                                    }
                                    is com.example.decalxeandroid.domain.model.Result.Error -> {
                                        error = ordersResult.message
                                        isLoading = false
                                    }
                                    else -> {
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    }
                    UserRole.TECHNICIAN -> {
                        // For Technicians, get all orders (they can work on orders from any store)
                        AppContainer.orderRepository.getOrders().collect { result ->
                            when (result) {
                                is com.example.decalxeandroid.domain.model.Result.Success -> {
                                    orders = result.data.sortedWith(
                                            compareByDescending<Order> { order ->
                                                    // Ưu tiên: High = 3, Medium = 2, Low = 1, null = 0
                                                    when (order.priority?.lowercase()) {
                                                            "high" -> 3
                                                            "medium" -> 2
                                                            "low" -> 1
                                                            else -> 0
                                                    }
                                            }.thenByDescending { order ->
                                                    // Sắp xếp theo thời gian tạo (createdAt) từ mới đến cũ
                                                    try {
                                                            val format = java.text.SimpleDateFormat(
                                                                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                                                                    java.util.Locale.getDefault()
                                                            )
                                                            format.parse(order.createdAt)?.time ?: 0L
                                                    } catch (e: Exception) {
                                                            try {
                                                                    val format2 = java.text.SimpleDateFormat(
                                                                            "yyyy-MM-dd'T'HH:mm:ss'Z'",
                                                                            java.util.Locale.getDefault()
                                                                    )
                                                                    format2.parse(order.createdAt)?.time ?: 0L
                                                            } catch (e2: Exception) {
                                                                    0L
                                                            }
                                                    }
                                            }
                                    )
                                    isLoading = false
                                }
                                is com.example.decalxeandroid.domain.model.Result.Error -> {
                                    error = result.message
                                    isLoading = false
                                }
                                else -> {
                                    isLoading = false
                                }
                            }
                        }
                    }
                    UserRole.CUSTOMER -> {
                        // For Customers, get their own orders
                        AppContainer.orderRepository.getOrders().collect { result ->
                            when (result) {
                                is com.example.decalxeandroid.domain.model.Result.Success -> {
                                    val customerOrders = result.data.filter { it.customerId == user.accountId }
                                    orders = customerOrders.sortedWith(
                                            compareByDescending<Order> { order ->
                                                    // Ưu tiên: High = 3, Medium = 2, Low = 1, null = 0
                                                    when (order.priority?.lowercase()) {
                                                            "high" -> 3
                                                            "medium" -> 2
                                                            "low" -> 1
                                                            else -> 0
                                                    }
                                            }.thenByDescending { order ->
                                                    // Sắp xếp theo thời gian tạo (createdAt) từ mới đến cũ
                                                    try {
                                                            val format = java.text.SimpleDateFormat(
                                                                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                                                                    java.util.Locale.getDefault()
                                                            )
                                                            format.parse(order.createdAt)?.time ?: 0L
                                                    } catch (e: Exception) {
                                                            try {
                                                                    val format2 = java.text.SimpleDateFormat(
                                                                            "yyyy-MM-dd'T'HH:mm:ss'Z'",
                                                                            java.util.Locale.getDefault()
                                                                    )
                                                                    format2.parse(order.createdAt)?.time ?: 0L
                                                            } catch (e2: Exception) {
                                                                    0L
                                                            }
                                                    }
                                            }
                                    )
                                    isLoading = false
                                }
                                is com.example.decalxeandroid.domain.model.Result.Error -> {
                                    error = result.message
                                    isLoading = false
                                }
                                else -> {
                                    isLoading = false
                                }
                            }
                        }
                    }
                    else -> {
                        error = "Không có quyền xem đơn hàng"
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                error = e.message ?: "Có lỗi xảy ra khi tải đơn hàng"
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = "Đơn hàng",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
            )

            // Add order button (only for Sales)
            if (currentUser?.role == UserRole.SALES) {
                FloatingActionButton(
                        onClick = onNavigateToCreateOrder,
                        modifier = Modifier.size(48.dp)
                ) { Icon(imageVector = Icons.Default.Add, contentDescription = "Tạo đơn hàng") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Đang tải đơn hàng...")
                    }
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                                text = "Có lỗi xảy ra",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.error
                        )
                        Text(
                                text = error ?: "Unknown error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = { retryKey++ }) { Text("Thử lại") }
                    }
                }
            }
            orders.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "No Orders",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                                text = "Không có đơn hàng nào",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                                text =
                                        when (currentUser?.role) {
                                            UserRole.SALES -> {
                                                val storeName = currentEmployeeInfo?.storeName
                                                if (storeName != null) {
                                                    "Chưa có đơn hàng nào trong cửa hàng $storeName"
                                                } else {
                                                    "Chưa có đơn hàng nào trong cửa hàng của bạn"
                                                }
                                            }
                                            UserRole.TECHNICIAN ->
                                                    "Chưa có đơn hàng nào cần lắp đặt"
                                            UserRole.CUSTOMER -> "Bạn chưa có đơn hàng nào"
                                            else -> "Không có đơn hàng"
                                        },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(orders) { order ->
                        StoreOrderCard(
                                order = order,
                                onClick = { onNavigateToOrderDetail(order.orderId) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreOrderCard(order: Order, onClick: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = order.customerFullName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = "Vehicle",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                                text = order.vehicleLicensePlate ?: "Chưa có biển số",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                            shape = RoundedCornerShape(16.dp),
                            color =
                                    when (order.orderStatus) {
                                        "Hoàn thành" -> MaterialTheme.colorScheme.primaryContainer
                                        "Đang xử lý" -> MaterialTheme.colorScheme.secondaryContainer
                                        "Thiết kế" -> MaterialTheme.colorScheme.tertiaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                    ) {
                        Text(
                                text = order.orderStatus,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color =
                                        when (order.orderStatus) {
                                            "Hoàn thành" ->
                                                    MaterialTheme.colorScheme.onPrimaryContainer
                                            "Đang xử lý" ->
                                                    MaterialTheme.colorScheme.onSecondaryContainer
                                            "Thiết kế" ->
                                                    MaterialTheme.colorScheme.onTertiaryContainer
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                            text = "${order.totalAmount.toInt()}₫",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                            text = order.orderDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
