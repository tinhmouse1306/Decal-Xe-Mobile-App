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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    onNavigateBack: () -> Unit,
    onOrderCreated: (Order) -> Unit,
    viewModel: CreateOrderViewModel = viewModel(
        factory = CreateOrderViewModelFactory(
            orderRepository = AppContainer.orderRepository,
            customerRepository = AppContainer.customerRepository,
            customerVehicleRepository = AppContainer.customerVehicleRepository,
            employeeRepository = AppContainer.employeeRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        if (uiState is CreateOrderUiState.Success) {
            onOrderCreated((uiState as CreateOrderUiState.Success).order)
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo đơn hàng") },
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
                uiState is CreateOrderUiState.Loading -> {
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
                        OrderForm(
                            uiState = uiState,
                            onCustomerChanged = { viewModel.updateSelectedCustomer(it) },
                            onVehicleChanged = { viewModel.updateSelectedVehicle(it) },
                            onEmployeeChanged = { viewModel.updateSelectedEmployee(it) },
                            onTotalAmountChanged = { viewModel.updateTotalAmount(it) },
                            onPriorityChanged = { viewModel.updatePriority(it) },
                            onDescriptionChanged = { viewModel.updateDescription(it) },
                            onIsCustomDecalChanged = { viewModel.updateIsCustomDecal(it) },
                            onExpectedArrivalTimeChanged = { viewModel.updateExpectedArrivalTime(it) },
                            onSubmit = { viewModel.createOrder() }
                        )
                        
                        // Error Message
                        if (uiState is CreateOrderUiState.Error) {
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
                                        text = (uiState as CreateOrderUiState.Error).message,
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
private fun OrderForm(
    uiState: CreateOrderUiState,
    onCustomerChanged: (String?) -> Unit,
    onVehicleChanged: (String?) -> Unit,
    onEmployeeChanged: (String?) -> Unit,
    onTotalAmountChanged: (String) -> Unit,
    onPriorityChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onIsCustomDecalChanged: (Boolean) -> Unit,
    onExpectedArrivalTimeChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val formData = when (uiState) {
        is CreateOrderUiState.Editing -> uiState.formData
        else -> OrderFormData()
    }
    
    val customers = when (uiState) {
        is CreateOrderUiState.Editing -> uiState.customers
        else -> emptyList()
    }
    
    val vehicles = when (uiState) {
        is CreateOrderUiState.Editing -> uiState.vehicles
        else -> emptyList()
    }
    
    val employees = when (uiState) {
        is CreateOrderUiState.Editing -> uiState.employees
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
                text = "Thông tin đơn hàng",
                style = MaterialTheme.typography.titleLarge
            )
            
            // Customer Selection
            CustomerSelectionField(
                customers = customers,
                selectedCustomerId = formData.selectedCustomerId,
                onCustomerChanged = onCustomerChanged
            )
            
            // Vehicle Selection
            VehicleSelectionField(
                vehicles = vehicles,
                selectedVehicleId = formData.selectedVehicleId,
                onVehicleChanged = onVehicleChanged
            )
            
            // Employee Selection
            EmployeeSelectionField(
                employees = employees,
                selectedEmployeeId = formData.selectedEmployeeId,
                onEmployeeChanged = onEmployeeChanged
            )
            
            // Total Amount
            OutlinedTextField(
                value = formData.totalAmount,
                onValueChange = onTotalAmountChanged,
                label = { Text("Tổng tiền *") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = formData.totalAmountError != null,
                supportingText = formData.totalAmountError?.let { 
                    { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            )
            
            // Priority Selection
            PrioritySelectionField(
                priority = formData.priority,
                onPriorityChanged = onPriorityChanged
            )
            
            // Expected Arrival Time
            OutlinedTextField(
                value = formData.expectedArrivalTime,
                onValueChange = onExpectedArrivalTimeChanged,
                label = { Text("Thời gian dự kiến") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                placeholder = { Text("yyyy-mm-dd hh:mm:ss") }
            )
            
            // Custom Decal Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Decal tùy chỉnh")
                Switch(
                    checked = formData.isCustomDecal,
                    onCheckedChange = onIsCustomDecalChanged
                )
            }
            
            // Description
            OutlinedTextField(
                value = formData.description,
                onValueChange = onDescriptionChanged,
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                minLines = 3
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is CreateOrderUiState.Loading && formData.isValid
            ) {
                if (uiState is CreateOrderUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Tạo đơn hàng")
            }
        }
    }
}