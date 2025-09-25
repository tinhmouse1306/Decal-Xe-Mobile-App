package com.example.decalxeandroid.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderEditScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: OrderEditViewModel = viewModel(
        factory = OrderEditViewModelFactory(
            orderId = orderId,
            orderRepository = AppContainer.orderRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Handle navigation on success
    LaunchedEffect(uiState) {
        if (uiState is OrderEditUiState.Success) {
            onNavigateToDetail(orderId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa đơn hàng") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is OrderEditUiState.Editing -> {
                            if (state.formData.isValid) {
                                IconButton(onClick = { viewModel.updateOrder() }) {
                                    Icon(Icons.Default.Check, contentDescription = "Lưu")
                                }
                            }
                        }
                        is OrderEditUiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        else -> {}
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is OrderEditUiState.LoadingOrder -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Đang tải thông tin đơn hàng...")
                        }
                    }
                }
                
                is OrderEditUiState.Editing -> {
                    EditOrderForm(
                        uiState = state,
                        onTotalAmountChange = viewModel::updateTotalAmount,
                        onAssignedEmployeeChange = viewModel::updateAssignedEmployee,
                        onVehicleIdChange = viewModel::updateVehicleId,
                        onExpectedArrivalTimeChange = viewModel::updateExpectedArrivalTime,
                        onPriorityChange = viewModel::updatePriority,
                        onIsCustomDecalChange = viewModel::updateIsCustomDecal,
                        onDescriptionChange = viewModel::updateDescription,
                        onSubmit = viewModel::updateOrder
                    )
                }
                
                is OrderEditUiState.Loading -> {
                    EditOrderForm(
                        uiState = state,
                        onTotalAmountChange = { },
                        onAssignedEmployeeChange = { },
                        onVehicleIdChange = { },
                        onExpectedArrivalTimeChange = { },
                        onPriorityChange = { },
                        onIsCustomDecalChange = { },
                        onDescriptionChange = { },
                        onSubmit = { }
                    )
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(
                                    text = "Đang cập nhật đơn hàng...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                
                is OrderEditUiState.Error -> {
                    EditOrderForm(
                        uiState = state,
                        onTotalAmountChange = viewModel::updateTotalAmount,
                        onAssignedEmployeeChange = viewModel::updateAssignedEmployee,
                        onVehicleIdChange = viewModel::updateVehicleId,
                        onExpectedArrivalTimeChange = viewModel::updateExpectedArrivalTime,
                        onPriorityChange = viewModel::updatePriority,
                        onIsCustomDecalChange = viewModel::updateIsCustomDecal,
                        onDescriptionChange = viewModel::updateDescription,
                        onSubmit = viewModel::updateOrder
                    )
                    
                    // Error overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = state.message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    TextButton(onClick = { viewModel.resetError() }) {
                                        Text("Đóng")
                                    }
                                    Button(onClick = { viewModel.updateOrder() }) {
                                        Text("Thử lại")
                                    }
                                }
                            }
                        }
                    }
                }
                
                is OrderEditUiState.Success -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = "Cập nhật thành công!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Đang chuyển về trang chi tiết...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditOrderForm(
    uiState: OrderEditUiState,
    onTotalAmountChange: (String) -> Unit,
    onAssignedEmployeeChange: (String) -> Unit,
    onVehicleIdChange: (String) -> Unit,
    onExpectedArrivalTimeChange: (String) -> Unit,
    onPriorityChange: (String) -> Unit,
    onIsCustomDecalChange: (Boolean) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val formData = when (val state = uiState) {
        is OrderEditUiState.Editing -> state.formData
        is OrderEditUiState.Loading -> state.formData
        is OrderEditUiState.Error -> state.formData
        else -> OrderEditFormData()
    }
    
    val enabled = when (uiState) {
        is OrderEditUiState.Editing -> true
        else -> false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Chỉnh sửa thông tin đơn hàng",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // Total Amount
                OutlinedTextField(
                    value = formData.totalAmount,
                    onValueChange = onTotalAmountChange,
                    label = { Text("Tổng tiền") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = formData.totalAmountError != null,
                    supportingText = {
                        if (formData.totalAmountError != null) {
                            Text(
                                text = formData.totalAmountError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                // Assigned Employee ID
                OutlinedTextField(
                    value = formData.assignedEmployeeId,
                    onValueChange = onAssignedEmployeeChange,
                    label = { Text("ID Nhân viên phụ trách") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    isError = formData.assignedEmployeeIdError != null,
                    supportingText = {
                        if (formData.assignedEmployeeIdError != null) {
                            Text(
                                text = formData.assignedEmployeeIdError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                // Vehicle ID
                OutlinedTextField(
                    value = formData.vehicleId,
                    onValueChange = onVehicleIdChange,
                    label = { Text("ID Xe") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    isError = formData.vehicleIdError != null,
                    supportingText = {
                        if (formData.vehicleIdError != null) {
                            Text(
                                text = formData.vehicleIdError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                // Expected Arrival Time
                OutlinedTextField(
                    value = formData.expectedArrivalTime,
                    onValueChange = onExpectedArrivalTimeChange,
                    label = { Text("Thời gian dự kiến hoàn thành") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    isError = formData.expectedArrivalTimeError != null,
                    supportingText = {
                        if (formData.expectedArrivalTimeError != null) {
                            Text(
                                text = formData.expectedArrivalTimeError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                // Priority
                OutlinedTextField(
                    value = formData.priority,
                    onValueChange = onPriorityChange,
                    label = { Text("Độ ưu tiên") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.PriorityHigh, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    isError = formData.priorityError != null,
                    supportingText = {
                        if (formData.priorityError != null) {
                            Text(
                                text = formData.priorityError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                // Is Custom Decal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = formData.isCustomDecal,
                        onCheckedChange = onIsCustomDecalChange,
                        enabled = enabled
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Decal tùy chỉnh",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                // Description
                OutlinedTextField(
                    value = formData.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    isError = formData.descriptionError != null,
                    supportingText = {
                        if (formData.descriptionError != null) {
                            Text(
                                text = formData.descriptionError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled,
                    minLines = 3
                )
                
                if (enabled) {
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = formData.isValid
                    ) {
                        Text("Cập nhật đơn hàng")
                    }
                }
            }
        }
    }
}
