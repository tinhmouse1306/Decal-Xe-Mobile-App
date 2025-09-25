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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    onNavigateBack: () -> Unit,
    onVehicleCreated: (CustomerVehicle) -> Unit,
    viewModel: AddVehicleViewModel = viewModel(
        factory = AddVehicleViewModelFactory(
            customerVehicleRepository = AppContainer.customerVehicleRepository,
            customerRepository = AppContainer.customerRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        if (uiState is AddVehicleUiState.Success) {
            onVehicleCreated((uiState as AddVehicleUiState.Success).vehicle)
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm xe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
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
            when {
                uiState is AddVehicleUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Form Fields
                        VehicleForm(
                            uiState = uiState,
                            onCustomerChanged = { viewModel.updateSelectedCustomer(it) },
                            onVehicleModelChanged = { viewModel.updateSelectedVehicleModel(it) },
                            onChassisNumberChanged = { viewModel.updateChassisNumber(it) },
                            onLicensePlateChanged = { viewModel.updateLicensePlate(it) },
                            onColorChanged = { viewModel.updateColor(it) },
                            onYearChanged = { viewModel.updateYear(it) },
                            onInitialKMChanged = { viewModel.updateInitialKM(it) },
                            onSubmit = { viewModel.createVehicle() }
                        )
                        
                        // Error Message
                        if (uiState is AddVehicleUiState.Error) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = (uiState as AddVehicleUiState.Error).message,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleForm(
    uiState: AddVehicleUiState,
    onCustomerChanged: (String?) -> Unit,
    onVehicleModelChanged: (String?) -> Unit,
    onChassisNumberChanged: (String) -> Unit,
    onLicensePlateChanged: (String) -> Unit,
    onColorChanged: (String) -> Unit,
    onYearChanged: (String) -> Unit,
    onInitialKMChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val formData = when (uiState) {
        is AddVehicleUiState.Editing -> uiState.formData
        else -> VehicleFormData()
    }
    
    val customers = when (uiState) {
        is AddVehicleUiState.Editing -> uiState.customers
        else -> emptyList()
    }
    
    val vehicleModels = when (uiState) {
        is AddVehicleUiState.Editing -> uiState.vehicleModels
        else -> emptyList()
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Thông tin xe",
                style = MaterialTheme.typography.titleLarge
            )
            
            // Customer Selection
            CustomerSelectionField(
                customers = customers,
                selectedCustomerId = formData.selectedCustomerId,
                onCustomerChanged = onCustomerChanged
            )
            
            // Vehicle Model Selection
            VehicleModelSelectionField(
                vehicleModels = vehicleModels,
                selectedModelId = formData.selectedModelId,
                onVehicleModelChanged = onVehicleModelChanged
            )
            
            // Chassis Number
            OutlinedTextField(
                value = formData.chassisNumber,
                onValueChange = onChassisNumberChanged,
                label = { Text("Số khung *") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Badge, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                isError = formData.chassisNumberError != null,
                supportingText = formData.chassisNumberError?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )
            
            // License Plate
            OutlinedTextField(
                value = formData.licensePlate,
                onValueChange = onLicensePlateChanged,
                label = { Text("Biển số *") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.CreditCard, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                isError = formData.licensePlateError != null,
                supportingText = formData.licensePlateError?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )
            
            // Color
            OutlinedTextField(
                value = formData.color,
                onValueChange = onColorChanged,
                label = { Text("Màu sắc") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Palette, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
            
            // Year
            OutlinedTextField(
                value = formData.year,
                onValueChange = onYearChanged,
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
                supportingText = formData.yearError?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                placeholder = { Text("VD: 2023") }
            )
            
            // Initial KM
            OutlinedTextField(
                value = formData.initialKM,
                onValueChange = onInitialKMChanged,
                label = { Text("Số km ban đầu") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Speed, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                isError = formData.initialKMError != null,
                supportingText = formData.initialKMError?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                placeholder = { Text("VD: 15000") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AddVehicleUiState.Loading && formData.isValid
            ) {
                if (uiState is AddVehicleUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Thêm xe")
            }
        }
    }
}