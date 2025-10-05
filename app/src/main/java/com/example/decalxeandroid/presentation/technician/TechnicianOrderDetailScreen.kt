package com.example.decalxeandroid.presentation.technician

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.OrderDetail
import com.example.decalxeandroid.presentation.customers.CustomerModernTopAppBar
import com.example.decalxeandroid.presentation.orders.OrderDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicianOrderDetailScreen(
        orderId: String,
        viewModel: OrderDetailViewModel,
        onNavigateBack: () -> Unit,
        onNavigateToCustomer: (String) -> Unit = {},
        onNavigateToVehicle: (String) -> Unit = {},
        onEditOrderDetail: (OrderDetail) -> Unit = {},
        onDeleteOrderDetail: (OrderDetail) -> Unit = {},
        modifier: Modifier = Modifier
) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val listState = rememberLazyListState()
        var showStatusUpdateDialog by remember { mutableStateOf(false) }

        // Load order detail when screen is first composed
        LaunchedEffect(orderId) { viewModel.loadOrderDetail(orderId) }

        Column(modifier = modifier.fillMaxSize()) {
                // Top App Bar
                CustomerModernTopAppBar(
                        title = "Chi tiết đơn hàng lắp đặt",
                        subtitle = uiState.order?.let { "Đơn hàng #${it.orderId}" } ?: "",
                        onNavigateBack = onNavigateBack
                )

                // Content
                when {
                        uiState.isLoading -> {
                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                CircularProgressIndicator()
                                                Text(
                                                        text = "Đang tải thông tin đơn hàng...",
                                                        style = MaterialTheme.typography.bodyMedium
                                                )
                                        }
                                }
                        }
                        uiState.error != null -> {
                                ErrorState(
                                        error = uiState.error ?: "Unknown error",
                                        onRetry = { viewModel.loadOrderDetail(orderId) },
                                        onDismissError = { viewModel.clearError() }
                                )
                        }
                        uiState.order != null -> {
                                TechnicianOrderDetailContent(
                                        uiState = uiState,
                                        listState = listState,
                                        onRefresh = { viewModel.refreshOrderDetail(orderId) },
                                        onNavigateToCustomer = onNavigateToCustomer,
                                        onNavigateToVehicle = onNavigateToVehicle,
                                        onEditOrderDetail = onEditOrderDetail,
                                        onDeleteOrderDetail = onDeleteOrderDetail,
                                        onUpdateStatus = { showStatusUpdateDialog = true }
                                )
                        }
                        else -> {
                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = "Không tìm thấy đơn hàng",
                                                style = MaterialTheme.typography.bodyLarge
                                        )
                                }
                        }
                }
        }

        // Status Update Dialog
        if (showStatusUpdateDialog && uiState.order != null) {
                val order = uiState.order!!
                InstallationStatusUpdateDialog(
                        currentStatus = order.orderStatus,
                        currentStage = order.currentStage,
                        isCustomDecal = order.isCustomDecal,
                        onDismiss = { showStatusUpdateDialog = false },
                        onStatusUpdate = { status, stage ->
                                viewModel.updateOrderStatus(orderId, status, stage)
                        }
                )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicianOrderDetailContent(
        uiState: com.example.decalxeandroid.presentation.orders.OrderDetailUiState,
        listState: androidx.compose.foundation.lazy.LazyListState,
        onRefresh: () -> Unit,
        onNavigateToCustomer: (String) -> Unit,
        onNavigateToVehicle: (String) -> Unit,
        onEditOrderDetail: (OrderDetail) -> Unit,
        onDeleteOrderDetail: (OrderDetail) -> Unit,
        onUpdateStatus: () -> Unit,
        modifier: Modifier = Modifier
) {
        val order = uiState.order!!

        LazyColumn(
                state = listState,
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // Order Info Card
                item { TechnicianOrderInfoCard(order = order, currentStage = order.currentStage) }

                // Customer Info Card
                item {
                        com.example.decalxeandroid.presentation.orders.CustomerInfoCard(
                                order = order,
                                onCustomerClick = { onNavigateToCustomer(order.customerId) }
                        )
                }

                // Vehicle Info Card
                item {
                        com.example.decalxeandroid.presentation.orders.VehicleInfoCard(
                                order = order,
                                onVehicleClick = { onNavigateToVehicle(order.vehicleId ?: "") }
                        )
                }

                // Order Details List
                item {
                        com.example.decalxeandroid.presentation.orders.OrderDetailsList(
                                orderDetails = uiState.orderDetails,
                                onEditOrderDetail = onEditOrderDetail,
                                onDeleteOrderDetail = onDeleteOrderDetail
                        )
                }

                // Stage History Timeline
                item {
                        com.example.decalxeandroid.presentation.orders.OrderStageTimeline(
                                stageHistory = uiState.stageHistory
                        )
                }

                // Action Buttons
                item {
                        TechnicianOrderActionButtons(order = order, onUpdateStatus = onUpdateStatus)
                }
        }
}

@Composable
fun ErrorState(
        error: String,
        onRetry: () -> Unit,
        onDismissError: () -> Unit,
        modifier: Modifier = Modifier
) {
        Column(
                modifier = modifier.fillMaxSize(),
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

                Text(
                        text = "Có lỗi xảy ra",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(onClick = onDismissError) { Text("Đóng") }
                        Button(onClick = onRetry) { Text("Thử lại") }
                }
        }
}

@Composable
fun TechnicianOrderActionButtons(
        order: com.example.decalxeandroid.domain.model.Order,
        onUpdateStatus: () -> Unit,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        Text(
                                text = "Thao tác lắp đặt",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                        )

                        // Only show update status button if current stage is "Survey"
                        if (order.currentStage == "Survey") {
                                Button(
                                        onClick = onUpdateStatus,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.primary
                                                )
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.Build,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Bắt đầu lắp đặt")
                                }
                        } else {
                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant
                                                )
                                ) {
                                        Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.Info,
                                                        contentDescription = null,
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                        text =
                                                                "Đơn hàng đã chuyển sang giai đoạn: ${order.currentStage}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
fun TechnicianOrderInfoCard(
        order: com.example.decalxeandroid.domain.model.Order,
        currentStage: String?,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        text = "Thông tin đơn hàng",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )

                                Surface(
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 4.dp
                                                ),
                                        shape =
                                                androidx.compose.foundation.shape
                                                        .RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                        Text(
                                                text = "Khảo sát",
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 12.dp,
                                                                vertical = 6.dp
                                                        ),
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                }
                        }

                        Divider()

                        com.example.decalxeandroid.presentation.orders.InfoRow(
                                icon = Icons.Default.Numbers,
                                label = "Mã đơn hàng",
                                value = order.orderNumber
                        )

                        com.example.decalxeandroid.presentation.orders.InfoRow(
                                icon = Icons.Default.Person,
                                label = "Khách hàng",
                                value = order.customerFullName
                        )

                        com.example.decalxeandroid.presentation.orders.InfoRow(
                                icon = Icons.Default.DirectionsCar,
                                label = "Xe",
                                value = "${order.vehicleBrandName} ${order.vehicleModelName}"
                        )

                        if (!order.chassisNumber.isNullOrBlank()) {
                                com.example.decalxeandroid.presentation.orders.InfoRow(
                                        icon = Icons.Default.Fingerprint,
                                        label = "Số khung",
                                        value = order.chassisNumber
                                )
                        }

                        com.example.decalxeandroid.presentation.orders.InfoRow(
                                icon = Icons.Default.AttachMoney,
                                label = "Tổng tiền",
                                value = "${order.totalAmount}đ"
                        )

                        com.example.decalxeandroid.presentation.orders.InfoRow(
                                icon = Icons.Default.Schedule,
                                label = "Ngày tạo",
                                value = order.orderDate?.substring(0, 10) ?: ""
                        )

                        if (order.priority != null) {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        Icon(
                                                imageVector =
                                                        when (order.priority.lowercase()) {
                                                                "high" -> Icons.Default.PriorityHigh
                                                                "medium" -> Icons.Default.Remove
                                                                "low" -> Icons.Default.LowPriority
                                                                else -> Icons.Default.Info
                                                        },
                                                contentDescription = null,
                                                tint =
                                                        when (order.priority.lowercase()) {
                                                                "high" ->
                                                                        MaterialTheme.colorScheme
                                                                                .error
                                                                "medium" ->
                                                                        MaterialTheme.colorScheme
                                                                                .tertiary
                                                                "low" ->
                                                                        MaterialTheme.colorScheme
                                                                                .secondary
                                                                else ->
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                        }
                                        )
                                        Text(
                                                text = "Ưu tiên: ${order.priority}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        }

                        if (order.isCustomDecal) {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.Build,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.secondary
                                        )
                                        Text(
                                                text = "Decal tùy chỉnh",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.secondary
                                        )
                                }
                        }
                }
        }
}
