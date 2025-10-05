package com.example.decalxeandroid.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.decalxeandroid.domain.model.OrderDetail
import com.example.decalxeandroid.presentation.customers.CustomerModernTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
        orderId: String,
        viewModel: OrderDetailViewModel,
        onNavigateBack: () -> Unit,
        onNavigateToCustomer: (String) -> Unit = {},
        onNavigateToVehicle: (String) -> Unit = {},
        onEditOrderDetail: (OrderDetail) -> Unit = {},
        onDeleteOrderDetail: (OrderDetail) -> Unit = {},
        onNavigateToPayment: (String) -> Unit = {},
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
                        title = "Chi tiết đơn hàng",
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
                                OrderDetailContent(
                                        uiState = uiState,
                                        listState = listState,
                                        onRefresh = { viewModel.refreshOrderDetail(orderId) },
                                        onNavigateToCustomer = onNavigateToCustomer,
                                        onNavigateToVehicle = onNavigateToVehicle,
                                        onEditOrderDetail = onEditOrderDetail,
                                        onDeleteOrderDetail = onDeleteOrderDetail,
                                        onUpdateStatus = { showStatusUpdateDialog = true },
                                        onNavigateToPayment = onNavigateToPayment
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
                val availableStatuses = viewModel.getAvailableNextStatuses()
                OrderStatusUpdateDialog(
                        currentStatus = uiState.order!!.orderStatus,
                        currentStage = uiState.order!!.currentStage,
                        isCustomDecal = uiState.order!!.isCustomDecal,
                        availableStatuses = availableStatuses,
                        onDismiss = { showStatusUpdateDialog = false },
                        onStatusUpdate = { status, stage ->
                                viewModel.updateOrderStatus(orderId, status, stage)
                        }
                )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailContent(
        uiState: OrderDetailUiState,
        listState: androidx.compose.foundation.lazy.LazyListState,
        onRefresh: () -> Unit,
        onNavigateToCustomer: (String) -> Unit,
        onNavigateToVehicle: (String) -> Unit,
        onEditOrderDetail: (OrderDetail) -> Unit,
        onDeleteOrderDetail: (OrderDetail) -> Unit,
        onUpdateStatus: () -> Unit,
        onNavigateToPayment: (String) -> Unit,
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
                item { OrderInfoCard(order = order, currentStage = uiState.currentStage) }

                // Customer Info Card
                item {
                        CustomerInfoCard(
                                order = order,
                                onCustomerClick = { onNavigateToCustomer(order.customerId) }
                        )
                }

                // Vehicle Info Card
                item {
                        VehicleInfoCard(
                                order = order,
                                onVehicleClick = { onNavigateToVehicle(order.vehicleId ?: "") }
                        )
                }

                // Order Details List
                item {
                        OrderDetailsList(
                                orderDetails = uiState.orderDetails,
                                onEditOrderDetail = onEditOrderDetail,
                                onDeleteOrderDetail = onDeleteOrderDetail
                        )
                }

                // Stage History Timeline
                item { OrderStageTimeline(stageHistory = uiState.stageHistory) }

                // Action Buttons
                item {
                        OrderActionButtons(
                                order = order,
                                onEditOrder = { /* TODO: Implement edit order */},
                                onDeleteOrder = { /* TODO: Implement delete order */},
                                onUpdateStatus = onUpdateStatus,
                                onPayment = { onNavigateToPayment(order.orderId) }
                        )
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
fun OrderActionButtons(
        order: com.example.decalxeandroid.domain.model.Order,
        onEditOrder: () -> Unit,
        onDeleteOrder: () -> Unit,
        onUpdateStatus: () -> Unit,
        onPayment: () -> Unit = {},
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
                                text = "Thao tác",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                        )

                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                // Chỉ hiển thị nút "Trạng thái" và "Thanh toán" (nếu cần)
                                OutlinedButton(
                                        onClick = onUpdateStatus,
                                        modifier = Modifier.weight(1f),
                                        colors =
                                                ButtonDefaults.outlinedButtonColors(
                                                        contentColor =
                                                                MaterialTheme.colorScheme.primary
                                                )
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.Update,
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Trạng thái")
                                }

                                // Hiển thị nút "Thanh toán" nếu đơn hàng ở trạng thái "Nghiệm thu
                                // và nhận hàng"
                                if (order.orderStatus == "Nghiệm thu và nhận hàng") {
                                        OutlinedButton(
                                                onClick = onPayment,
                                                modifier = Modifier.weight(1f),
                                                colors =
                                                        ButtonDefaults.outlinedButtonColors(
                                                                contentColor =
                                                                        Color(
                                                                                0xFF4CAF50
                                                                        ) // Màu xanh lá
                                                        )
                                        ) {
                                                Icon(
                                                        imageVector = Icons.Default.Payment,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Thanh toán")
                                        }
                                }
                        }
                }
        }
}

// Enhanced CustomerInfoCard with click functionality
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerInfoCard(
        order: com.example.decalxeandroid.domain.model.Order,
        onCustomerClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                onClick = onCustomerClick
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                        text = "Thông tin khách hàng",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Xem chi tiết",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                        Divider()

                        InfoRow(
                                icon = Icons.Default.Person,
                                label = "Họ tên",
                                value = order.customerFullName
                        )

                        InfoRow(
                                icon = Icons.Default.Phone,
                                label = "Số điện thoại",
                                value = order.customerPhoneNumber ?: "Chưa có thông tin"
                        )

                        if (order.customerEmail?.isNotEmpty() == true) {
                                InfoRow(
                                        icon = Icons.Default.Email,
                                        label = "Email",
                                        value = order.customerEmail ?: ""
                                )
                        }

                        if (order.customerAddress?.isNotEmpty() == true) {
                                InfoRow(
                                        icon = Icons.Default.LocationOn,
                                        label = "Địa chỉ",
                                        value = order.customerAddress ?: ""
                                )
                        }
                }
        }
}

// Enhanced VehicleInfoCard with click functionality
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleInfoCard(
        order: com.example.decalxeandroid.domain.model.Order,
        onVehicleClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                onClick = onVehicleClick
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                        text = "Thông tin xe",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = "Xem chi tiết",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                        Divider()

                        InfoRow(
                                icon = Icons.Default.DirectionsCar,
                                label = "Dòng xe",
                                value = "${order.vehicleBrandName} ${order.vehicleModelName}"
                        )

                        InfoRow(
                                icon = Icons.Default.Fingerprint,
                                label = "Số khung",
                                value = order.chassisNumber ?: "Chưa có thông tin"
                        )

                        InfoRow(
                                icon = Icons.Default.Schedule,
                                label = "Thời gian dự kiến",
                                value = order.expectedArrivalTime ?: "Chưa có thông tin"
                        )

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
