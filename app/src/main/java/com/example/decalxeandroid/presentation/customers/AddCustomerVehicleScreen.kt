package com.example.decalxeandroid.presentation.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.data.dto.VehicleModelDto
import com.example.decalxeandroid.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerVehicleScreen(
    customer: Customer,
    onNavigateBack: () -> Unit,
    onVehicleCreated: (CustomerVehicle) -> Unit,
    onComplete: () -> Unit,
    viewModel: AddCustomerVehicleViewModel = viewModel(
        factory = AddCustomerVehicleViewModelFactory(
            customerVehicleRepository = AppContainer.customerVehicleRepository,
            vehicleRepository = AppContainer.vehicleRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val vehicleModels by viewModel.vehicleModels.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadVehicleData()
    }
    
    LaunchedEffect(uiState) {
        if (uiState is AddCustomerVehicleUiState.Success) {
            onVehicleCreated((uiState as AddCustomerVehicleUiState.Success).vehicle)
            onComplete()
        }
    }
    
    Scaffold(
        topBar = {
            CustomerModernTopAppBar(
                title = "Thêm xe",
                subtitle = "Bước 2/2",
                onNavigateBack = onNavigateBack,
                showProgress = true,
                progress = 1.0f
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Customer Info Card
                ModernCustomerInfoCard(customer = customer)
                
                // Vehicle Form
                VehicleForm(
                    uiState = uiState,
                    vehicleModels = vehicleModels,
                    onChassisNumberChange = { viewModel.updateChassisNumber(it) },
                    onLicensePlateChange = { viewModel.updateLicensePlate(it) },
                    onColorChange = { viewModel.updateColor(it) },
                    onYearChange = { viewModel.updateYear(it) },
                    onInitialKMChange = { viewModel.updateInitialKM(it) },
                    onModelIdChange = { viewModel.updateModelId(it) },
                    onModelSelectionChange = { viewModel.updateModelSelection(it) },
                    onSubmit = { viewModel.createVehicle(customer.customerId) }
                )
                
                // Error Message
                if (uiState is AddCustomerVehicleUiState.Error) {
                    CustomerModernErrorCard(
                        message = (uiState as AddCustomerVehicleUiState.Error).message
                    )
                }
            }
        }
    }
}


@Composable
fun ModernCustomerInfoCard(customer: Customer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Customer avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Customer",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = customer.fullName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = customer.phoneNumber ?: "Không có số điện thoại",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = "Khách hàng mới",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun VehicleForm(
    uiState: AddCustomerVehicleUiState,
    vehicleModels: List<VehicleModelDto>,
    onChassisNumberChange: (String) -> Unit,
    onLicensePlateChange: (String) -> Unit,
    onColorChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onInitialKMChange: (String) -> Unit,
    onModelIdChange: (String) -> Unit,
    onModelSelectionChange: (VehicleModelDto) -> Unit,
    onSubmit: () -> Unit
) {
    val formData = when (uiState) {
        is AddCustomerVehicleUiState.Editing -> uiState.formData
        else -> VehicleFormData()
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4facfe),
                                    Color(0xFF00f2fe)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DirectionsCar,
                        contentDescription = "Vehicle",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = "Thông tin xe",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Chassis Number
            CustomerModernOutlinedTextField(
                value = formData.chassisNumber,
                onValueChange = onChassisNumberChange,
                label = "Số khung xe *",
                leadingIcon = Icons.Default.Fingerprint,
                keyboardType = KeyboardType.Text,
                isError = formData.chassisNumberError != null,
                errorMessage = formData.chassisNumberError
            )
            
            // License Plate
            CustomerModernOutlinedTextField(
                value = formData.licensePlate,
                onValueChange = onLicensePlateChange,
                label = "Biển số xe *",
                leadingIcon = Icons.Default.DirectionsCar,
                keyboardType = KeyboardType.Text,
                isError = formData.licensePlateError != null,
                errorMessage = formData.licensePlateError
            )
            
            // Color
            VietnameseDropdownField(
                value = formData.color,
                onValueChange = onColorChange,
                label = "Màu sắc *",
                leadingIcon = Icons.Default.Palette,
                options = listOf(
                    "Đen", "Trắng", "Xám", "Bạc", "Xanh dương", "Xanh lá", 
                    "Đỏ", "Vàng", "Cam", "Tím", "Hồng", "Nâu", "Khác"
                ),
                isError = formData.colorError != null,
                errorMessage = formData.colorError
            )
            
            // Year
            CustomerModernOutlinedTextField(
                value = formData.year,
                onValueChange = onYearChange,
                label = "Năm sản xuất *",
                leadingIcon = Icons.Default.CalendarToday,
                keyboardType = KeyboardType.Number,
                isError = formData.yearError != null,
                errorMessage = formData.yearError
            )
            
            // Initial KM
            CustomerModernOutlinedTextField(
                value = formData.initialKM,
                onValueChange = onInitialKMChange,
                label = "Số km ban đầu",
                leadingIcon = Icons.Default.Speed,
                keyboardType = KeyboardType.Number,
                isError = formData.initialKMError != null,
                errorMessage = formData.initialKMError
            )
            
            // Model Selection
                VehicleModelDropdown(
                    value = formData.modelName,
                    onValueChange = onModelIdChange,
                    onModelSelectionChange = onModelSelectionChange,
                    vehicleModels = vehicleModels,
                    isError = formData.modelIdError != null,
                    errorMessage = formData.modelIdError
                )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AddCustomerVehicleUiState.Loading && formData.isValid,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState is AddCustomerVehicleUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Hoàn thành",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}



data class VehicleFormData(
    val chassisNumber: String = "",
    val licensePlate: String = "",
    val color: String = "",
    val year: String = "",
    val initialKM: String = "",
    val modelId: String = "",
    val modelName: String = "", // Add model name for display
    val chassisNumberError: String? = null,
    val licensePlateError: String? = null,
    val colorError: String? = null,
    val yearError: String? = null,
    val initialKMError: String? = null,
    val modelIdError: String? = null
) {
    val isValid: Boolean
        get() = chassisNumber.isNotBlank() && 
                licensePlate.isNotBlank() && 
                color.isNotBlank() && 
                year.isNotBlank() &&
                modelId.isNotBlank() && // Require modelId instead of modelName
                chassisNumberError == null &&
                licensePlateError == null &&
                colorError == null &&
                yearError == null &&
                initialKMError == null &&
                modelIdError == null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleModelDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    onModelSelectionChange: (VehicleModelDto) -> Unit,
    vehicleModels: List<VehicleModelDto>,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            readOnly = true,
            label = { Text("Mẫu xe") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            leadingIcon = {
                Icon(Icons.Default.CarRental, contentDescription = null)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = isError,
            supportingText = {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF667eea),
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (vehicleModels.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Đang tải danh sách...") },
                    onClick = { }
                )
            } else {
                vehicleModels.forEach { vehicleModel ->
                    DropdownMenuItem(
                        text = { Text(vehicleModel.modelName) },
                        onClick = {
                            onValueChange(vehicleModel.modelName)
                            onModelSelectionChange(vehicleModel)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

// UI State
sealed class AddCustomerVehicleUiState {
    object Loading : AddCustomerVehicleUiState()
    data class Editing(val formData: VehicleFormData) : AddCustomerVehicleUiState()
    data class Success(val vehicle: CustomerVehicle) : AddCustomerVehicleUiState()
    data class Error(val message: String) : AddCustomerVehicleUiState()
}
