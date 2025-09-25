package com.example.decalxeandroid.presentation.sales

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.decalxeandroid.domain.model.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(
    viewModel: SalesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCustomer: () -> Unit,
    onNavigateToVehicle: () -> Unit
) {
    var customer by remember { mutableStateOf<Customer?>(null) }
    var vehicle by remember { mutableStateOf<CustomerVehicle?>(null) }
    var selectedServices by remember { mutableStateOf<List<DecalService>>(emptyList()) }
    var notes by remember { mutableStateOf("") }
    var expectedArrivalTime by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Normal") }
    
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val customerVehicles by viewModel.customerVehicles.collectAsStateWithLifecycle()
    val decalServices by viewModel.decalServices.collectAsStateWithLifecycle()
    val currentEmployee by viewModel.currentEmployee.collectAsStateWithLifecycle()
    
    // Calculate total amount
    val totalAmount = selectedServices.sumOf { service ->
        (service.price ?: 0.0)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại"
                )
            }
            Text(
                text = "Tạo đơn hàng mới",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Selection
            item {
                CustomerSelectionCard(
                    selectedCustomer = customer,
                    customers = customers,
                    onCustomerSelected = { customer = it },
                    onNavigateToCustomer = onNavigateToCustomer
                )
            }
            
            // Vehicle Selection
            item {
                VehicleSelectionCard(
                    selectedVehicle = vehicle,
                    vehicles = customerVehicles.filter { it.customerID == customer?.customerId },
                    onVehicleSelected = { vehicle = it },
                    onNavigateToVehicle = onNavigateToVehicle,
                    enabled = customer != null
                )
            }
            
            // Service Selection
            item {
                ServiceSelectionCard(
                    selectedServices = selectedServices,
                    availableServices = decalServices,
                    onServiceSelected = { service ->
                        selectedServices = if (selectedServices.contains(service)) {
                            selectedServices - service
                        } else {
                            selectedServices + service
                        }
                    }
                )
            }
            
            // Order Details
            item {
                OrderDetailsCard(
                    notes = notes,
                    onNotesChanged = { notes = it },
                    expectedArrivalTime = expectedArrivalTime,
                    onExpectedArrivalTimeChanged = { expectedArrivalTime = it },
                    priority = priority,
                    onPriorityChanged = { priority = it }
                )
            }
            
            // Order Summary
            item {
                OrderSummaryCard(
                    customer = customer,
                    vehicle = vehicle,
                    services = selectedServices,
                    totalAmount = totalAmount
                )
            }
            
            // Create Order Button
            item {
                Button(
                    onClick = {
                        if (customer != null && vehicle != null && selectedServices.isNotEmpty()) {
                            val order = createOrderFromInputs(
                                customer = customer!!,
                                vehicle = vehicle!!,
                                services = selectedServices,
                                notes = notes,
                                expectedArrivalTime = expectedArrivalTime,
                                priority = priority,
                                totalAmount = totalAmount,
                                currentEmployee = currentEmployee
                            )
                            viewModel.createNewOrder(order)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = customer != null && vehicle != null && selectedServices.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tạo đơn hàng")
                }
            }
        }
    }
}

@Composable
fun CustomerSelectionCard(
    selectedCustomer: Customer?,
    customers: List<Customer>,
    onCustomerSelected: (Customer) -> Unit,
    onNavigateToCustomer: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chọn khách hàng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToCustomer) {
                    Text("Thêm khách hàng")
                }
            }
            
            if (selectedCustomer != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = selectedCustomer.fullName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = selectedCustomer.phoneNumber ?: "Không có số điện thoại",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        selectedCustomer.email?.let { email ->
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Chưa chọn khách hàng",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun VehicleSelectionCard(
    selectedVehicle: CustomerVehicle?,
    vehicles: List<CustomerVehicle>,
    onVehicleSelected: (CustomerVehicle) -> Unit,
    onNavigateToVehicle: () -> Unit,
    enabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chọn xe",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = onNavigateToVehicle,
                    enabled = enabled
                ) {
                    Text("Thêm xe")
                }
            }
            
            if (selectedVehicle != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = selectedVehicle.licensePlate,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${selectedVehicle.vehicleBrandName} ${selectedVehicle.vehicleModelName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        selectedVehicle.chassisNumber?.let { chassis ->
                            Text(
                                text = "Chassis: $chassis",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else if (!enabled) {
                Text(
                    text = "Vui lòng chọn khách hàng trước",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Chưa chọn xe",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ServiceSelectionCard(
    selectedServices: List<DecalService>,
    availableServices: List<DecalService>,
    onServiceSelected: (DecalService) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Chọn dịch vụ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableServices) { service ->
                    ServiceSelectionItem(
                        service = service,
                        isSelected = selectedServices.contains(service),
                        onSelected = { onServiceSelected(service) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceSelectionItem(
    service: DecalService,
    isSelected: Boolean,
    onSelected: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onSelected,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.serviceName ?: "Dịch vụ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                service.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = "đ${String.format("%,.0f", service.price ?: 0.0)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Đã chọn",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsCard(
    notes: String,
    onNotesChanged: (String) -> Unit,
    expectedArrivalTime: String,
    onExpectedArrivalTimeChanged: (String) -> Unit,
    priority: String,
    onPriorityChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Chi tiết đơn hàng",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Priority
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Độ ưu tiên:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                val priorities = listOf("Low", "Normal", "High")
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = priority,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .weight(2f)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        priorities.forEach { priorityOption ->
                            DropdownMenuItem(
                                text = { Text(priorityOption) },
                                onClick = {
                                    onPriorityChanged(priorityOption)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Expected Arrival Time
            OutlinedTextField(
                value = expectedArrivalTime,
                onValueChange = onExpectedArrivalTimeChanged,
                label = { Text("Thời gian dự kiến") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChanged,
                label = { Text("Ghi chú") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
        }
    }
}

@Composable
fun OrderSummaryCard(
    customer: Customer?,
    vehicle: CustomerVehicle?,
    services: List<DecalService>,
    totalAmount: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Tóm tắt đơn hàng",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Customer info
            customer?.let {
                Text(
                    text = "Khách hàng: ${it.fullName}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Vehicle info
            vehicle?.let {
                Text(
                    text = "Xe: ${it.licensePlate}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Services
            Text(
                text = "Dịch vụ: ${services.size} dịch vụ",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Total amount
            Text(
                text = "Tổng tiền: đ${String.format("%,.0f", totalAmount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun createOrderFromInputs(
    customer: Customer,
    vehicle: CustomerVehicle,
    services: List<DecalService>,
    notes: String,
    expectedArrivalTime: String,
    priority: String,
    totalAmount: Double,
    currentEmployee: Employee?
): Order {
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    
    return Order(
        orderId = "", // Will be generated by backend
        orderNumber = "",
        customerId = customer.customerId,
        customerFullName = customer.fullName,
        vehicleId = vehicle.vehicleID,
        vehicleLicensePlate = vehicle.licensePlate,
        assignedEmployeeId = currentEmployee?.employeeId,
        assignedEmployeeName = if (currentEmployee != null) {
            "${currentEmployee.firstName} ${currentEmployee.lastName}"
        } else null,
        orderStatus = "Pending",
        currentStage = "Order Created",
        totalAmount = totalAmount,
        depositAmount = totalAmount * 0.3, // 30% deposit
        remainingAmount = totalAmount * 0.7,
        orderDate = currentDate,
        expectedCompletionDate = expectedArrivalTime,
        actualCompletionDate = null,
        notes = notes,
        isActive = true,
        createdAt = currentDate,
        updatedAt = null,
        chassisNumber = vehicle.chassisNumber,
        vehicleModelName = vehicle.vehicleModelName,
        vehicleBrandName = vehicle.vehicleBrandName,
        expectedArrivalTime = expectedArrivalTime,
        priority = priority,
        isCustomDecal = false,
        storeId = currentEmployee?.storeId,
        description = notes,
        customerPhoneNumber = customer.phoneNumber,
        customerEmail = customer.email,
        customerAddress = customer.address,
        accountId = currentEmployee?.accountId,
        accountUsername = currentEmployee?.accountUsername,
        accountCreated = true
    )
}
