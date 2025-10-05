package com.example.decalxeandroid.presentation.technician

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
fun TechnicianInstallationScreen(
        onNavigateToOrderDetail: (String) -> Unit,
        viewModel: TechnicianInstallationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showStatusUpdateDialog by remember { mutableStateOf(false) }
    var selectedOrderId by remember { mutableStateOf("") }
    var selectedOrder by remember {
        mutableStateOf<com.example.decalxeandroid.domain.model.Order?>(null)
    }

    LaunchedEffect(Unit) { viewModel.loadInstallationOrders() }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Column {
                                Text("Lắp đặt")
                                uiState.employeeName?.let { employeeName ->
                                    Text(
                                            text = "Kỹ thuật viên: $employeeName",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.loadInstallationOrders() }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
                            }
                        }
                )
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
                        Button(onClick = { viewModel.loadInstallationOrders() }) { Text("Thử lại") }
                    }
                }
                uiState.orders.isEmpty() -> {
                    Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = "Không có đơn hàng cần lắp đặt",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = "Các đơn hàng ở giai đoạn 'Khảo sát' sẽ hiển thị ở đây",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.orders) { order ->
                            InstallationOrderCard(
                                    order = order,
                                    onClick = { onNavigateToOrderDetail(order.orderId) },
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
        InstallationStatusUpdateDialog(
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
fun InstallationOrderCard(
        order: com.example.decalxeandroid.domain.model.Order,
        onClick: () -> Unit,
        onUpdateStatus: () -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp),
            onClick = onClick
    ) {
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
                        color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                            text = "Khảo sát",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
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

            // Priority indicator
            if (!order.priority.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            imageVector =
                                    when (order.priority.lowercase()) {
                                        "high" -> Icons.Default.PriorityHigh
                                        "medium" -> Icons.Default.Remove
                                        "low" -> Icons.Default.LowPriority
                                        else -> Icons.Default.Info
                                    },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint =
                                    when (order.priority.lowercase()) {
                                        "high" -> MaterialTheme.colorScheme.error
                                        "medium" -> MaterialTheme.colorScheme.tertiary
                                        "low" -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "Ưu tiên: ${order.priority}",
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
                Button(
                        onClick = onUpdateStatus,
                        modifier = Modifier.padding(top = 8.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                )
                ) {
                    Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bắt đầu lắp đặt")
                }
            }
        }
    }
}
