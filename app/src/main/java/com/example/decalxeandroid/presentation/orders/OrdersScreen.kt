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
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
        onNavigateToOrderDetail: (String) -> Unit,
        onNavigateToCreateOrder: () -> Unit,
        viewModel: OrdersViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showStatusUpdateDialog by remember { mutableStateOf(false) }
    var selectedOrderId by remember { mutableStateOf("") }
    var selectedOrder by remember {
        mutableStateOf<com.example.decalxeandroid.domain.model.Order?>(null)
    }

    LaunchedEffect(Unit) { viewModel.loadOrders() }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Column {
                                Text("Quản lý đơn hàng")
                                uiState.storeName?.let { employeeName ->
                                    Text(
                                            text = "Nhân viên: $employeeName",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.loadOrders() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
                            }
                        }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNavigateToCreateOrder) {
                    Icon(Icons.Default.Add, contentDescription = "Tạo đơn hàng")
                }
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        val error = uiState.error
                        if (error != null) {
                            Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadOrders() }) { Text("Thử lại") }
                    }
                }
                uiState.orders.isEmpty() -> {
                    Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = "Chưa có đơn hàng nào",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateToCreateOrder) { Text("Tạo đơn hàng đầu tiên") }
                    }
                }
                else -> {
                    LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.orders) { order ->
                            OrderCard(
                                    order = order,
                                    onClick = {
                                        println(
                                                "OrdersScreen: Clicking order with ID: '${order.orderId}'"
                                        )
                                        onNavigateToOrderDetail(order.orderId)
                                    },
                                    onUpdateStatus = {
                                        selectedOrderId = order.orderId
                                        selectedOrder = order
                                        showStatusUpdateDialog = true
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

    // Status Update Dialog
    if (showStatusUpdateDialog && selectedOrder != null) {
        OrderStatusUpdateDialog(
                currentStatus = selectedOrder!!.orderStatus,
                currentStage = selectedOrder!!.currentStage,
                isCustomDecal = selectedOrder!!.isCustomDecal,
                onDismiss = {
                    showStatusUpdateDialog = false
                    selectedOrder = null
                    selectedOrderId = ""
                },
                onStatusUpdate = { status, stage ->
                    viewModel.updateOrderStatus(selectedOrderId, status, stage)
                    showStatusUpdateDialog = false
                    selectedOrder = null
                    selectedOrderId = ""
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderCard(
        order: com.example.decalxeandroid.domain.model.Order,
        onClick: () -> Unit,
        onUpdateStatus: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header with customer name and status
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = order.customerFullName ?: "Khách hàng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )

                Surface(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        color =
                                when (order.orderStatus) {
                                    "Pending" -> MaterialTheme.colorScheme.tertiary
                                    "InProgress" -> MaterialTheme.colorScheme.primary
                                    "Completed" -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                ) {
                    Text(
                            text =
                                    when (order.orderStatus) {
                                        "Pending" -> "Chờ xử lý"
                                        "InProgress" -> "Đang xử lý"
                                        "Completed" -> "Hoàn thành"
                                        else -> order.orderStatus
                                    },
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Vehicle info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                        text =
                                "${order.vehicleBrandName ?: ""} ${order.vehicleModelName ?: ""}".trim(),
                        style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Employee info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                        text = order.assignedEmployeeName ?: "Chưa gán nhân viên",
                        style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Chassis number
            if (!order.chassisNumber.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            imageVector = Icons.Default.Numbers,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "Số khung: ${order.chassisNumber}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Amount and date
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = "${order.totalAmount}đ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                )

                Text(
                        text = order.orderDate?.substring(0, 10) ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Update Status Button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(onClick = onUpdateStatus, modifier = Modifier.padding(top = 8.dp)) {
                    Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cập nhật trạng thái")
                }
            }
        }
    }
}
