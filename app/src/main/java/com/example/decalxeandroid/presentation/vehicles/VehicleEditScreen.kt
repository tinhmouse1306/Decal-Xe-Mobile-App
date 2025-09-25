package com.example.decalxeandroid.presentation.vehicles

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
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleEditScreen(
    vehicleId: String,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    viewModel: VehicleEditViewModel = viewModel(
        factory = VehicleEditViewModelFactory(
            vehicleId = vehicleId,
            customerVehicleRepository = AppContainer.customerVehicleRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Handle navigation on success
    LaunchedEffect(uiState) {
        if (uiState is VehicleEditUiState.Success) {
            onNavigateToDetail(vehicleId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin xe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is VehicleEditUiState.Editing -> {
                            if (state.formData.isValid) {
                                IconButton(onClick = { viewModel.updateVehicle() }) {
                                    Icon(Icons.Default.Check, contentDescription = "Lưu")
                                }
                            }
                        }
                        is VehicleEditUiState.Loading -> {
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
                is VehicleEditUiState.LoadingVehicle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Đang tải thông tin xe...")
                        }
                    }
                }
                
                is VehicleEditUiState.Editing -> {
                    EditVehicleForm(
                        uiState = state,
                        onChassisNumberChange = viewModel::updateChassisNumber,
                        onLicensePlateChange = viewModel::updateLicensePlate,
                        onColorChange = viewModel::updateColor,
                        onYearChange = viewModel::updateYear,
                        onInitialKMChange = viewModel::updateInitialKM,
                        onModelIdChange = viewModel::updateModelId,
                        onSubmit = viewModel::updateVehicle
                    )
                }
                
                is VehicleEditUiState.Loading -> {
                    EditVehicleForm(
                        uiState = state,
                        onChassisNumberChange = { },
                        onLicensePlateChange = { },
                        onColorChange = { },
                        onYearChange = { },
                        onInitialKMChange = { },
                        onModelIdChange = { },
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
                                    text = "Đang cập nhật thông tin xe...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                
                is VehicleEditUiState.Error -> {
                    EditVehicleForm(
                        uiState = state,
                        onChassisNumberChange = viewModel::updateChassisNumber,
                        onLicensePlateChange = viewModel::updateLicensePlate,
                        onColorChange = viewModel::updateColor,
                        onYearChange = viewModel::updateYear,
                        onInitialKMChange = viewModel::updateInitialKM,
                        onModelIdChange = viewModel::updateModelId,
                        onSubmit = viewModel::updateVehicle
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
                                    Button(onClick = { viewModel.updateVehicle() }) {
                                        Text("Thử lại")
                                    }
                                }
                            }
                        }
                    }
                }
                
                is VehicleEditUiState.Success -> {
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
private fun EditVehicleForm(
    uiState: VehicleEditUiState,
    onChassisNumberChange: (String) -> Unit,
    onLicensePlateChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onInitialKMChange: (String) -> Unit,
    onModelIdChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val formData = when (val state = uiState) {
        is VehicleEditUiState.Editing -> state.formData
        is VehicleEditUiState.Loading -> state.formData
        is VehicleEditUiState.Error -> state.formData
        else -> VehicleEditFormData()
    }
    
    val enabled = when (uiState) {
        is VehicleEditUiState.Editing -> true
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
                    text = "Chỉnh sửa thông tin xe",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                // Chassis Number
                OutlinedTextField(
                    value = formData.chassisNumber,
                    onValueChange = onChassisNumberChange,
                    label = { Text("Số khung xe") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Numbers, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    isError = formData.chassisNumberError != null,
                    supportingText = {
                        if (formData.chassisNumberError != null) {
                            Text(
                                text = formData.chassisNumberError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                // License Plate
                OutlinedTextField(
                    value = formData.licensePlate,
                    onValueChange = onLicensePlateChange,
                    label = { Text("Biển số xe") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.CreditCard, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    isError = formData.licensePlateError != null,
                    supportingText = {
                        if (formData.licensePlateError != null) {
                            Text(
                                text = formData.licensePlateError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                // Color
                OutlinedTextField(
                    value = formData.color,
                    onValueChange = onColorChange,
                    label = { Text("Màu xe") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Palette, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    isError = formData.colorError != null,
                    supportingText = {
                        if (formData.colorError != null) {
                            Text(
                                text = formData.colorError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                // Year
                OutlinedTextField(
                    value = formData.year,
                    onValueChange = onYearChange,
                    label = { Text("Năm sản xuất") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = formData.yearError != null,
                    supportingText = {
                        if (formData.yearError != null) {
                            Text(
                                text = formData.yearError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                // Initial KM
                OutlinedTextField(
                    value = formData.initialKM,
                    onValueChange = onInitialKMChange,
                    label = { Text("Số km ban đầu") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Speed, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = formData.initialKMError != null,
                    supportingText = {
                        if (formData.initialKMError != null) {
                            Text(
                                text = formData.initialKMError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                // Model ID
                OutlinedTextField(
                    value = formData.modelId,
                    onValueChange = onModelIdChange,
                    label = { Text("ID Model xe") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    isError = formData.modelIdError != null,
                    supportingText = {
                        if (formData.modelIdError != null) {
                            Text(
                                text = formData.modelIdError,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = enabled
                )
                
                if (enabled) {
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = formData.isValid
                    ) {
                        Text("Cập nhật thông tin xe")
                    }
                }
            }
        }
    }
}
