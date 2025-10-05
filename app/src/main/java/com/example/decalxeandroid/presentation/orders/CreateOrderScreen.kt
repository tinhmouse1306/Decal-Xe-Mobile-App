package com.example.decalxeandroid.presentation.orders

// Remove common imports - use inline components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decalxeandroid.di.AppContainer
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Employee
import com.example.decalxeandroid.presentation.customers.CustomerModernTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderScreen(onNavigateBack: () -> Unit, onOrderCreated: () -> Unit) {
        val viewModel: CreateOrderViewModel =
                viewModel(
                        factory =
                                CreateOrderViewModelFactory(
                                        orderRepository = AppContainer.orderRepository,
                                        customerRepository = AppContainer.customerRepository,
                                        customerVehicleRepository =
                                                AppContainer.customerVehicleRepository,
                                        employeeRepository = AppContainer.employeeRepository,
                                        decalServiceRepository =
                                                AppContainer.decalServiceRepository,
                                        orderDetailRepository = AppContainer.orderDetailRepository
                                )
                )

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.loadInitialData() }

        LaunchedEffect(uiState) {
                if (uiState is CreateOrderUiState.Success) {
                        onOrderCreated()
                }
        }

        Scaffold(
                topBar = {
                        CustomerModernTopAppBar(
                                title = "Tạo đơn hàng mới",
                                subtitle = "Nhập thông tin đơn hàng",
                                onNavigateBack = onNavigateBack
                        )
                }
        ) { paddingValues ->
                when (val currentState = uiState) {
                        is CreateOrderUiState.Loading -> {
                                Box(
                                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                                        contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                        }
                        is CreateOrderUiState.Error -> {
                                Box(
                                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                Text(
                                                        text = currentState.message,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        textAlign = TextAlign.Center
                                                )
                                                Button(onClick = { viewModel.loadInitialData() }) {
                                                        Text("Thử lại")
                                                }
                                        }
                                }
                        }
                        is CreateOrderUiState.Step1Editing -> {
                                OrderForm(
                                        formData = currentState.formData,
                                        customers = currentState.customers,
                                        vehicles = currentState.vehicles,
                                        employees = currentState.employees,
                                        decalServices = currentState.decalServices,
                                        onCustomerSelectionChange = { customer ->
                                                viewModel.updateSelectedCustomer(
                                                        customer.customerId
                                                )
                                        },
                                        onVehicleSelectionChange = { vehicle ->
                                                viewModel.updateSelectedVehicle(vehicle.vehicleID)
                                        },
                                        onEmployeeSelectionChange = { employee ->
                                                viewModel.updateSelectedEmployee(
                                                        employee.employeeId
                                                )
                                        },
                                        onPriorityChange = viewModel::updatePriority,
                                        onDescriptionChange = viewModel::updateDescription,
                                        onIsCustomDecalChange = viewModel::updateIsCustomDecal,
                                        onExpectedArrivalTimeChange =
                                                viewModel::updateExpectedArrivalTime,
                                        onServiceAdd = viewModel::addService,
                                        onServiceRemove = viewModel::removeService,
                                        onServiceQuantityChange = viewModel::updateServiceQuantity,
                                        onCreateOrder = viewModel::createOrder,
                                        paddingValues = paddingValues
                                )
                        }
                        is CreateOrderUiState.Step2Editing -> {
                                OrderDetailForm(
                                        createdOrder = currentState.createdOrder,
                                        formData = currentState.formData,
                                        customers = currentState.customers,
                                        vehicles = currentState.vehicles,
                                        employees = currentState.employees,
                                        decalServices = currentState.decalServices,
                                        onServiceAdd = viewModel::addService,
                                        onServiceRemove = viewModel::removeService,
                                        onServiceQuantityChange = viewModel::updateServiceQuantity,
                                        onCompleteOrder = viewModel::createOrderDetails,
                                        paddingValues = paddingValues
                                )
                        }
                        is CreateOrderUiState.Success -> {
                                // This will trigger navigation via LaunchedEffect
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderForm(
        formData: OrderFormData,
        customers: List<Customer>,
        vehicles: List<CustomerVehicle>,
        employees: List<Employee>,
        decalServices: List<com.example.decalxeandroid.domain.model.DecalService>,
        onCustomerSelectionChange: (Customer) -> Unit,
        onVehicleSelectionChange: (CustomerVehicle) -> Unit,
        onEmployeeSelectionChange: (Employee) -> Unit,
        onPriorityChange: (String) -> Unit,
        onDescriptionChange: (String) -> Unit,
        onIsCustomDecalChange: (Boolean) -> Unit,
        onExpectedArrivalTimeChange: (String) -> Unit,
        onServiceAdd: (com.example.decalxeandroid.domain.model.DecalService, Int) -> Unit,
        onServiceRemove: (String) -> Unit,
        onServiceQuantityChange: (String, Int) -> Unit,
        onCreateOrder: () -> Unit,
        paddingValues: PaddingValues
) {
        var showServiceDialog by remember { mutableStateOf(false) }

        LazyColumn(
                modifier =
                        Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Customer Selection
                item {
                        CustomerSelectionCard(
                                customers = customers,
                                selectedCustomer =
                                        customers.find {
                                                it.customerId == formData.selectedCustomerId
                                        },
                                onCustomerSelected = onCustomerSelectionChange
                        )
                }

                // Vehicle Selection
                item {
                        VehicleSelectionCard(
                                vehicles = vehicles,
                                selectedVehicle =
                                        vehicles.find {
                                                it.vehicleID == formData.selectedVehicleId
                                        },
                                onVehicleSelected = onVehicleSelectionChange
                        )
                }

                // Employee Selection
                item {
                        EmployeeSelectionCard(
                                employees = employees,
                                selectedEmployee =
                                        employees.find {
                                                it.employeeId == formData.selectedEmployeeId
                                        },
                                onEmployeeSelected = onEmployeeSelectionChange
                        )
                }

                // Services Selection
                // Services Selection Section - ENABLED FOR STEP 2
                item {
                        ServicesSelectionCard(
                                selectedServices = formData.selectedServices,
                                onAddService = { showServiceDialog = true },
                                onRemoveService = onServiceRemove,
                                onQuantityChange = onServiceQuantityChange
                        )
                }

                // Total Amount Display
                item { TotalAmountCard(totalAmount = formData.totalAmount) }

                // Other Fields
                item {
                        OtherFieldsCard(
                                priority = formData.priority,
                                description = formData.description,
                                isCustomDecal = formData.isCustomDecal,
                                expectedArrivalTime = formData.expectedArrivalTime,
                                onPriorityChange = onPriorityChange,
                                onDescriptionChange = onDescriptionChange,
                                onIsCustomDecalChange = onIsCustomDecalChange,
                                onExpectedArrivalTimeChange = onExpectedArrivalTimeChange
                        )
                }

                // Create Order Button
                item {
                        Button(
                                onClick = onCreateOrder,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = formData.isValid
                        ) { Text("Tạo đơn hàng") }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Service Selection Dialog - ENABLED FOR STEP 2
        if (showServiceDialog) {
                ServiceSelectionDialog(
                        decalServices = decalServices,
                        onDismiss = { showServiceDialog = false },
                        onServiceSelected = { service, quantity ->
                                onServiceAdd(service, quantity)
                                showServiceDialog = false
                        }
                )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerSelectionCard(
        customers: List<Customer>,
        selectedCustomer: Customer?,
        onCustomerSelected: (Customer) -> Unit
) {
        var expanded by remember { mutableStateOf(false) }

        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Text(
                                text = "Khách hàng",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                        )

                        ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                        ) {
                                OutlinedTextField(
                                        value = selectedCustomer?.fullName ?: "Chọn khách hàng",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Khách hàng *") },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        leadingIcon = {
                                                Icon(
                                                        Icons.Default.Person,
                                                        contentDescription = null
                                                )
                                        },
                                        trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = expanded
                                                )
                                        }
                                )

                                ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                ) {
                                        customers.forEach { customer ->
                                                DropdownMenuItem(
                                                        text = {
                                                                Column {
                                                                        Text(customer.fullName)
                                                                        customer.phoneNumber?.let {
                                                                                Text(
                                                                                        it,
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .bodySmall,
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onSurfaceVariant
                                                                                )
                                                                        }
                                                                }
                                                        },
                                                        onClick = {
                                                                onCustomerSelected(customer)
                                                                expanded = false
                                                        }
                                                )
                                        }
                                }
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleSelectionCard(
        vehicles: List<CustomerVehicle>,
        selectedVehicle: CustomerVehicle?,
        onVehicleSelected: (CustomerVehicle) -> Unit
) {
        var expanded by remember { mutableStateOf(false) }

        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Text(
                                text = "Xe",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                        )

                        ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                        ) {
                                OutlinedTextField(
                                        value = selectedVehicle?.licensePlate ?: "Chọn xe",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Xe *") },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        leadingIcon = {
                                                Icon(
                                                        Icons.Default.DirectionsCar,
                                                        contentDescription = null
                                                )
                                        },
                                        trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = expanded
                                                )
                                        }
                                )

                                ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                ) {
                                        vehicles.forEach { vehicle ->
                                                DropdownMenuItem(
                                                        text = {
                                                                Column {
                                                                        Text(vehicle.licensePlate)
                                                                        Text(
                                                                                "${vehicle.vehicleModelName} - ${vehicle.color}",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant
                                                                        )
                                                                }
                                                        },
                                                        onClick = {
                                                                onVehicleSelected(vehicle)
                                                                expanded = false
                                                        }
                                                )
                                        }
                                }
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmployeeSelectionCard(
        employees: List<Employee>,
        selectedEmployee: Employee?,
        onEmployeeSelected: (Employee) -> Unit
) {
        var expanded by remember { mutableStateOf(false) }

        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                        Text(
                                text = "Kỹ thuật viên",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                        )

                        ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                        ) {
                                OutlinedTextField(
                                        value =
                                                selectedEmployee?.let {
                                                        "${it.firstName} ${it.lastName}"
                                                }
                                                        ?: "Chọn kỹ thuật viên",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Kỹ thuật viên *") },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        leadingIcon = {
                                                Icon(
                                                        Icons.Default.Engineering,
                                                        contentDescription = null
                                                )
                                        },
                                        trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = expanded
                                                )
                                        }
                                )

                                ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                ) {
                                        employees.forEach { employee ->
                                                DropdownMenuItem(
                                                        text = {
                                                                Column {
                                                                        Text(
                                                                                "${employee.firstName} ${employee.lastName}"
                                                                        )
                                                                        employee.phoneNumber?.let {
                                                                                Text(
                                                                                        it,
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .bodySmall,
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onSurfaceVariant
                                                                                )
                                                                        }
                                                                }
                                                        },
                                                        onClick = {
                                                                onEmployeeSelected(employee)
                                                                expanded = false
                                                        }
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun ServicesSelectionCard(
        selectedServices: List<OrderDetailFormData>,
        onAddService: () -> Unit,
        onRemoveService: (String) -> Unit,
        onQuantityChange: (String, Int) -> Unit
) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                                Text(
                                        text = "Dịch vụ",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                                Button(onClick = onAddService, modifier = Modifier.height(32.dp)) {
                                        Icon(
                                                Icons.Default.Add,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                "Thêm dịch vụ",
                                                style = MaterialTheme.typography.labelSmall
                                        )
                                }
                        }

                        if (selectedServices.isEmpty()) {
                                Text(
                                        text = "Chưa có dịch vụ nào được chọn",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        } else {
                                selectedServices.forEach { service ->
                                        ServiceItemCard(
                                                service = service,
                                                onQuantityChange = { quantity ->
                                                        onQuantityChange(
                                                                service.serviceId,
                                                                quantity
                                                        )
                                                },
                                                onRemove = { onRemoveService(service.serviceId) }
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun ServiceItemCard(
        service: OrderDetailFormData,
        onQuantityChange: (Int) -> Unit,
        onRemove: () -> Unit
) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
                Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = service.serviceName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Medium
                                )
                                Text(
                                        text = "${service.unitPrice.toInt()} VNĐ/đơn vị",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                // Quantity controls
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                        IconButton(
                                                onClick = {
                                                        if (service.quantity > 1)
                                                                onQuantityChange(
                                                                        service.quantity - 1
                                                                )
                                                },
                                                modifier = Modifier.size(24.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.Remove,
                                                        contentDescription = "Giảm",
                                                        modifier = Modifier.size(16.dp)
                                                )
                                        }
                                        Text(
                                                text = service.quantity.toString(),
                                                style = MaterialTheme.typography.titleSmall,
                                                modifier = Modifier.width(24.dp),
                                                textAlign = TextAlign.Center
                                        )
                                        IconButton(
                                                onClick = {
                                                        onQuantityChange(service.quantity + 1)
                                                },
                                                modifier = Modifier.size(24.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.Add,
                                                        contentDescription = "Tăng",
                                                        modifier = Modifier.size(16.dp)
                                                )
                                        }
                                }

                                // Total price
                                Text(
                                        text = "${service.totalPrice.toInt()} VNĐ",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.width(80.dp),
                                        textAlign = TextAlign.End
                                )

                                // Remove button
                                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                                        Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Xóa",
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.error
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun TotalAmountCard(totalAmount: Double) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
        ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                        Icons.Default.AttachMoney,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = "Tổng tiền",
                                        style = MaterialTheme.typography.titleMedium
                                )
                        }
                        Text(
                                text = "${totalAmount.toInt()} VNĐ",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                        )
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OtherFieldsCard(
        priority: String,
        description: String,
        isCustomDecal: Boolean,
        expectedArrivalTime: String,
        onPriorityChange: (String) -> Unit,
        onDescriptionChange: (String) -> Unit,
        onIsCustomDecalChange: (Boolean) -> Unit,
        onExpectedArrivalTimeChange: (String) -> Unit
) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
        ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        Text(
                                text = "Thông tin khác",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                        )

                        // Priority ComboBox
                        var priorityExpanded by remember { mutableStateOf(false) }
                        val priorityOptions = listOf("Low", "Normal", "High", "Urgent")

                        ExposedDropdownMenuBox(
                                expanded = priorityExpanded,
                                onExpandedChange = { priorityExpanded = !priorityExpanded }
                        ) {
                                OutlinedTextField(
                                        value = priority.ifEmpty { "Normal" },
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Độ ưu tiên") },
                                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                                        leadingIcon = {
                                                Icon(
                                                        Icons.Default.PriorityHigh,
                                                        contentDescription = null
                                                )
                                        },
                                        trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = priorityExpanded
                                                )
                                        }
                                )

                                ExposedDropdownMenu(
                                        expanded = priorityExpanded,
                                        onDismissRequest = { priorityExpanded = false }
                                ) {
                                        priorityOptions.forEach { option ->
                                                DropdownMenuItem(
                                                        text = { Text(option) },
                                                        onClick = {
                                                                onPriorityChange(option)
                                                                priorityExpanded = false
                                                        }
                                                )
                                        }
                                }
                        }

                        // Description
                        OutlinedTextField(
                                value = description,
                                onValueChange = onDescriptionChange,
                                label = { Text("Mô tả") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = {
                                        Icon(Icons.Default.Description, contentDescription = null)
                                },
                                maxLines = 3
                        )

                        // Expected Arrival Time - DateTime Picker
                        var showDatePicker by remember { mutableStateOf(false) }
                        var showTimePicker by remember { mutableStateOf(false) }

                        OutlinedTextField(
                                value = expectedArrivalTime.ifEmpty { "Chọn ngày và giờ" },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Thời gian dự kiến") },
                                modifier =
                                        Modifier.fillMaxWidth().clickable {
                                                println("DateTime picker clicked!")
                                                showDatePicker = true
                                        },
                                leadingIcon = {
                                        Icon(Icons.Default.Schedule, contentDescription = null)
                                },
                                trailingIcon = {
                                        IconButton(
                                                onClick = {
                                                        println("Calendar icon clicked!")
                                                        showDatePicker = true
                                                }
                                        ) {
                                                Icon(
                                                        Icons.Default.CalendarToday,
                                                        contentDescription = null
                                                )
                                        }
                                }
                        )

                        // Date Picker Dialog
                        if (showDatePicker) {
                                val datePickerState =
                                        rememberDatePickerState(
                                                initialSelectedDateMillis =
                                                        System.currentTimeMillis()
                                        )
                                DatePickerDialog(
                                        onDateSelected = { selectedDate ->
                                                if (selectedDate != null) {
                                                        val selectedDateTime =
                                                                java.util.Date(selectedDate)
                                                        val currentDateTime = java.util.Date()

                                                        // Check if selected date is in the past
                                                        if (selectedDateTime.before(currentDateTime)
                                                        ) {
                                                                // Show error or prevent selection
                                                                return@DatePickerDialog
                                                        }

                                                        val date =
                                                                java.text.SimpleDateFormat(
                                                                                "yyyy-MM-dd",
                                                                                java.util.Locale
                                                                                        .getDefault()
                                                                        )
                                                                        .format(selectedDateTime)
                                                        // Store selected date and show time picker
                                                        val currentTime =
                                                                expectedArrivalTime.substringAfter(
                                                                                " "
                                                                        )
                                                                        .takeIf {
                                                                                it.contains(":") &&
                                                                                        it !=
                                                                                                expectedArrivalTime
                                                                        }
                                                                        ?: "09:00"
                                                        onExpectedArrivalTimeChange(
                                                                "$date $currentTime"
                                                        )
                                                        showTimePicker = true
                                                }
                                                showDatePicker = false
                                        },
                                        onDismiss = { showDatePicker = false }
                                )
                        }

                        // Time Picker Dialog
                        if (showTimePicker) {
                                val timePickerState = rememberTimePickerState()
                                TimePickerDialog(
                                        onTimeSelected = { hour, minute ->
                                                val time = String.format("%02d:%02d", hour, minute)
                                                val currentDate =
                                                        expectedArrivalTime.substringBefore(" ")
                                                                .takeIf {
                                                                        it.contains("-") &&
                                                                                it !=
                                                                                        expectedArrivalTime
                                                                }
                                                                ?: java.text.SimpleDateFormat(
                                                                                "yyyy-MM-dd",
                                                                                java.util.Locale
                                                                                        .getDefault()
                                                                        )
                                                                        .format(java.util.Date())

                                                // Check if selected date is today and time is in
                                                // the past
                                                val selectedDateStr = currentDate
                                                val todayStr =
                                                        java.text.SimpleDateFormat(
                                                                        "yyyy-MM-dd",
                                                                        java.util.Locale
                                                                                .getDefault()
                                                                )
                                                                .format(java.util.Date())

                                                if (selectedDateStr == todayStr) {
                                                        val currentTime =
                                                                java.util.Calendar.getInstance()
                                                        val selectedTime =
                                                                java.util.Calendar.getInstance()
                                                                        .apply {
                                                                                set(
                                                                                        java.util
                                                                                                .Calendar
                                                                                                .HOUR_OF_DAY,
                                                                                        hour
                                                                                )
                                                                                set(
                                                                                        java.util
                                                                                                .Calendar
                                                                                                .MINUTE,
                                                                                        minute
                                                                                )
                                                                                set(
                                                                                        java.util
                                                                                                .Calendar
                                                                                                .SECOND,
                                                                                        0
                                                                                )
                                                                                set(
                                                                                        java.util
                                                                                                .Calendar
                                                                                                .MILLISECOND,
                                                                                        0
                                                                                )
                                                                        }

                                                        if (selectedTime.before(currentTime)) {
                                                                // Time is in the past, don't update
                                                                return@TimePickerDialog
                                                        }
                                                }

                                                onExpectedArrivalTimeChange("$currentDate $time")
                                                showTimePicker = false
                                        },
                                        onDismiss = { showTimePicker = false }
                                )
                        }

                        // Custom Decal Switch
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Text("Decal tùy chỉnh")
                                Switch(
                                        checked = isCustomDecal,
                                        onCheckedChange = onIsCustomDecalChange
                                )
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceSelectionDialog(
        decalServices: List<com.example.decalxeandroid.domain.model.DecalService>,
        onDismiss: () -> Unit,
        onServiceSelected: (com.example.decalxeandroid.domain.model.DecalService, Int) -> Unit
) {
        var selectedService by remember {
                mutableStateOf<com.example.decalxeandroid.domain.model.DecalService?>(null)
        }
        var quantity by remember { mutableStateOf(1) }

        AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Chọn dịch vụ") },
                text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                // Service Selection
                                var expanded by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = { expanded = !expanded }
                                ) {
                                        OutlinedTextField(
                                                value = selectedService?.serviceName
                                                                ?: "Chọn dịch vụ",
                                                onValueChange = {},
                                                readOnly = true,
                                                label = { Text("Dịch vụ *") },
                                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                                leadingIcon = {
                                                        Icon(
                                                                Icons.Default.Build,
                                                                contentDescription = null
                                                        )
                                                },
                                                trailingIcon = {
                                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                                                expanded = expanded
                                                        )
                                                }
                                        )
                                        ExposedDropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                        ) {
                                                decalServices.forEach { service ->
                                                        DropdownMenuItem(
                                                                text = {
                                                                        Column {
                                                                                Text(
                                                                                        service.serviceName
                                                                                )
                                                                                Text(
                                                                                        "${service.price.toInt()} VNĐ",
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .bodySmall,
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onSurfaceVariant
                                                                                )
                                                                        }
                                                                },
                                                                onClick = {
                                                                        selectedService = service
                                                                        expanded = false
                                                                }
                                                        )
                                                }
                                        }
                                }

                                // Quantity Selection
                                OutlinedTextField(
                                        value = quantity.toString(),
                                        onValueChange = {
                                                quantity = it.toIntOrNull()?.takeIf { it > 0 } ?: 1
                                        },
                                        label = { Text("Số lượng *") },
                                        modifier = Modifier.fillMaxWidth(),
                                        leadingIcon = {
                                                Icon(
                                                        Icons.Default.Numbers,
                                                        contentDescription = null
                                                )
                                        },
                                        keyboardOptions =
                                                KeyboardOptions(keyboardType = KeyboardType.Number)
                                )

                                // Total Price Preview
                                selectedService?.let { service ->
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primaryContainer
                                                        )
                                        ) {
                                                Row(
                                                        modifier = Modifier.padding(12.dp),
                                                        horizontalArrangement =
                                                                Arrangement.SpaceBetween,
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Text(
                                                                text = "Tổng tiền:",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleSmall
                                                        )
                                                        Text(
                                                                text =
                                                                        "${(service.price * quantity).toInt()} VNĐ",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleMedium,
                                                                fontWeight = FontWeight.Bold,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .primary
                                                        )
                                                }
                                        }
                                }
                        }
                },
                confirmButton = {
                        Button(
                                onClick = {
                                        selectedService?.let { service ->
                                                onServiceSelected(service, quantity)
                                        }
                                },
                                enabled = selectedService != null && quantity > 0
                        ) { Text("Thêm") }
                },
                dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit) {
        val datePickerState = rememberDatePickerState()

        Dialog(onDismissRequest = onDismiss) {
                Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(16.dp)
                ) {
                        Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                Text(
                                        text = "Chọn ngày",
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                )

                                DatePicker(state = datePickerState)

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                ) {
                                        TextButton(onClick = onDismiss) { Text("Hủy") }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                                onClick = {
                                                        onDateSelected(
                                                                datePickerState.selectedDateMillis
                                                        )
                                                }
                                        ) { Text("Chọn") }
                                }
                        }
                }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(onTimeSelected: (Int, Int) -> Unit, onDismiss: () -> Unit) {
        val timePickerState = rememberTimePickerState()

        Dialog(onDismissRequest = onDismiss) {
                Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(16.dp)
                ) {
                        Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                Text(
                                        text = "Chọn giờ",
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                )

                                TimePicker(state = timePickerState)

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                ) {
                                        TextButton(onClick = onDismiss) { Text("Hủy") }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                                onClick = {
                                                        onTimeSelected(
                                                                timePickerState.hour,
                                                                timePickerState.minute
                                                        )
                                                }
                                        ) { Text("Chọn") }
                                }
                        }
                }
        }
}
