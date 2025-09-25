package com.example.decalxeandroid.presentation.vehicles

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    vehicleId: String,
    onNavigateBack: () -> Unit,
    onNavigateToCustomer: (String) -> Unit,
    onNavigateToOrder: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: VehicleDetailViewModel = viewModel(
        factory = VehicleDetailViewModelFactory(
            vehicleId = vehicleId,
            customerVehicleRepository = AppContainer.customerVehicleRepository,
            orderRepository = AppContainer.orderRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDeleteConfirmDialog by viewModel.showDeleteConfirmDialog.collectAsState()
    val deleteState by viewModel.deleteState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết xe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadVehicle() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
                    }
                    IconButton(onClick = { viewModel.editVehicle(onNavigateToEdit) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                    }
                    IconButton(onClick = { viewModel.showDeleteConfirmDialog() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                    }
                }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is VehicleDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Đang tải thông tin xe...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            is VehicleDetailUiState.Success -> {
                val vehicle = (uiState as VehicleDetailUiState.Success).vehicle
                val orders = (uiState as VehicleDetailUiState.Success).orders
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Vehicle Info Section
                    item {
                        VehicleInfoCard(
                            vehicle = vehicle,
                            onCustomerClick = { onNavigateToCustomer(vehicle.customerID) }
                        )
                    }
                    
                    // Orders Section
                    item {
                        VehicleOrdersSection(
                            orders = orders,
                            onOrderClick = onNavigateToOrder
                        )
                    }
                }
            }
            is VehicleDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Không thể tải thông tin xe",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = (uiState as VehicleDetailUiState.Error).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateBack
                            ) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Quay lại")
                            }
                            
                            Button(
                                onClick = { viewModel.loadVehicle() }
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Thử lại")
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteConfirmDialog() },
            title = {
                Text(
                    text = "Xác nhận xóa xe",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Bạn có chắc chắn muốn xóa xe này? Hành động này không thể hoàn tác.")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteVehicle(onNavigateBack) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Xóa", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.hideDeleteConfirmDialog() }
                ) {
                    Text("Hủy")
                }
            }
        )
    }
    
    // Handle Delete State
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is com.example.decalxeandroid.presentation.vehicles.DeleteState.Error -> {
                // Show error snackbar
                // Note: Ideally we would use SnackbarHost, but for simplicity we'll clear the state
                viewModel.clearDeleteState()
            }
            is com.example.decalxeandroid.presentation.vehicles.DeleteState.Success -> {
                // Success is handled by navigation back, so we clear the state
                viewModel.clearDeleteState()
            }
            else -> { /* Do nothing */ }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleInfoCard(
    vehicle: CustomerVehicle,
    onCustomerClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    text = "Thông tin xe",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "ID: ${vehicle.vehicleID}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Divider()
            
            // License Plate (Highlighted)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = vehicle.licensePlate.ifEmpty { "Chưa có biển số" },
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Vehicle Model and Brand
            InfoRow(
                icon = Icons.Default.CarRental,
                label = "Mẫu xe",
                value = when {
                    !vehicle.vehicleModelName.isNullOrEmpty() && !vehicle.vehicleBrandName.isNullOrEmpty() -> 
                        "${vehicle.vehicleModelName} - ${vehicle.vehicleBrandName}"
                    !vehicle.vehicleModelName.isNullOrEmpty() -> vehicle.vehicleModelName
                    !vehicle.vehicleBrandName.isNullOrEmpty() -> vehicle.vehicleBrandName
                    else -> "Chưa có thông tin"
                }
            )
            
            // Chassis Number
            InfoRow(
                icon = Icons.Default.Numbers,
                label = "Số khung",
                value = vehicle.chassisNumber.ifEmpty { "Chưa có thông tin" }
            )
            
            // Color
            InfoRow(
                icon = Icons.Default.Palette,
                label = "Màu sắc",
                value = vehicle.color.ifEmpty { "Chưa có thông tin" }
            )
            
            // Year
            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = "Năm sản xuất",
                value = if (vehicle.year > 0) vehicle.year.toString() else "Chưa có thông tin"
            )
            
            // Initial KM
            InfoRow(
                icon = Icons.Default.Speed,
                label = "Số km ban đầu",
                value = if (vehicle.initialKM > 0) "${vehicle.initialKM} km" else "Chưa có thông tin"
            )
            
            // Customer Info (Clickable)
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCustomerClick,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chủ sở hữu",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = vehicle.customerFullName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Xem chi tiết khách hàng",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleOrdersSection(
    orders: List<Order>,
    onOrderClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Lịch sử đơn hàng (${orders.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (orders.isEmpty()) {
                Text(
                    text = "Chưa có đơn hàng nào",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                orders.forEach { order ->
                    VehicleOrderItem(
                        order = order,
                        onClick = { onOrderClick(order.orderId) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleOrderItem(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Đơn hàng #${order.orderId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Trạng thái: ${order.orderStatus}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tổng tiền: ${order.totalAmount} VNĐ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Ngày: ${order.orderDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Xem chi tiết đơn hàng",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
