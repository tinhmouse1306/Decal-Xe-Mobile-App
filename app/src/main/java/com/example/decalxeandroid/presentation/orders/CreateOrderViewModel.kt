package com.example.decalxeandroid.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.*
import com.example.decalxeandroid.domain.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CreateOrderViewModel(
        private val orderRepository: OrderRepository,
        private val customerRepository: CustomerRepository,
        private val customerVehicleRepository: CustomerVehicleRepository,
        private val employeeRepository: EmployeeRepository,
        private val decalServiceRepository: DecalServiceRepository,
        private val orderDetailRepository: OrderDetailRepository
) : ViewModel() {

    private val _uiState =
            MutableStateFlow<CreateOrderUiState>(
                    CreateOrderUiState.Step1Editing(
                            formData = OrderFormData(),
                            customers = emptyList(),
                            vehicles = emptyList(),
                            employees = emptyList(),
                            decalServices = emptyList()
                    )
            )
    val uiState: StateFlow<CreateOrderUiState> = _uiState.asStateFlow()

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = CreateOrderUiState.Loading

            try {
                // Get current user's store ID first
                val currentUser =
                        com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.currentUser
                                .value
                val currentUserAccountId = currentUser?.accountId

                if (currentUserAccountId == null) {
                    _uiState.value = CreateOrderUiState.Error("Không tìm thấy thông tin người dùng")
                    return@launch
                }

                // Load all data in parallel
                val customersFlow = customerRepository.getCustomers()
                val vehiclesFlow = customerVehicleRepository.getVehicles()
                val employeesFlow = employeeRepository.getEmployees(1, 100)
                val decalServicesFlow = decalServiceRepository.getServices(1, 100)

                // Combine all flows
                kotlinx.coroutines.flow
                        .combine(customersFlow, vehiclesFlow, employeesFlow, decalServicesFlow) {
                                customersResult,
                                vehiclesResult,
                                employeesResult,
                                decalServicesResult ->
                            when {
                                customersResult is Result.Success &&
                                        vehiclesResult is Result.Success &&
                                        employeesResult is Result.Success &&
                                        decalServicesResult is Result.Success -> {

                                    // Find current user's store ID
                                    val currentUserEmployee =
                                            employeesResult.data.find {
                                                it.accountId == currentUserAccountId
                                            }
                                    val currentUserStoreId = currentUserEmployee?.storeId

                                    // Filter employees: same store + Technician role only
                                    val filteredEmployees =
                                            employeesResult.data.filter { employee ->
                                                // Check if employee is in same store as current
                                                // user
                                                val isSameStore =
                                                        employee.storeId == currentUserStoreId

                                                // Check if employee has Technician role
                                                val isTechnician =
                                                        employee.accountRoleName
                                                                ?.lowercase()
                                                                ?.contains("technician") == true ||
                                                                employee.accountRoleName
                                                                        ?.lowercase()
                                                                        ?.contains("kỹ thuật") ==
                                                                        true

                                                isSameStore && isTechnician
                                            }

                                    _uiState.value =
                                            CreateOrderUiState.Step1Editing(
                                                    formData = OrderFormData(),
                                                    customers = customersResult.data,
                                                    vehicles = vehiclesResult.data,
                                                    employees = filteredEmployees,
                                                    decalServices = decalServicesResult.data
                                            )
                                }
                                customersResult is Result.Error -> {
                                    _uiState.value =
                                            CreateOrderUiState.Error(
                                                    "Failed to load customers: ${customersResult.message}"
                                            )
                                }
                                vehiclesResult is Result.Error -> {
                                    _uiState.value =
                                            CreateOrderUiState.Error(
                                                    "Failed to load vehicles: ${vehiclesResult.message}"
                                            )
                                }
                                employeesResult is Result.Error -> {
                                    _uiState.value =
                                            CreateOrderUiState.Error(
                                                    "Failed to load employees: ${employeesResult.message}"
                                            )
                                }
                                decalServicesResult is Result.Error -> {
                                    _uiState.value =
                                            CreateOrderUiState.Error(
                                                    "Failed to load decal services: ${decalServicesResult.message}"
                                            )
                                }
                                else -> {
                                    _uiState.value =
                                            CreateOrderUiState.Error("Unknown error loading data")
                                }
                            }
                        }
                        .collect {}
            } catch (e: Exception) {
                _uiState.value = CreateOrderUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    fun updateSelectedCustomer(customerId: String?) {
        val currentState = _uiState.value
        when (currentState) {
            is CreateOrderUiState.Step1Editing -> {
                val updatedFormData = currentState.formData.copy(selectedCustomerId = customerId)
                _uiState.value = currentState.copy(formData = updatedFormData)

                // Load vehicles for selected customer
                if (customerId != null) {
                    loadVehiclesForCustomer(customerId)
                }
            }
            is CreateOrderUiState.Step2Editing -> {
                val updatedFormData = currentState.formData.copy(selectedCustomerId = customerId)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            else -> {}
        }
    }

    private fun loadVehiclesForCustomer(customerId: String) {
        viewModelScope.launch {
            try {
                customerVehicleRepository.getVehiclesByCustomerId(customerId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val currentState = _uiState.value
                            when (currentState) {
                                is CreateOrderUiState.Step1Editing -> {
                                    _uiState.value = currentState.copy(vehicles = result.data)
                                }
                                is CreateOrderUiState.Step2Editing -> {
                                    _uiState.value = currentState.copy(vehicles = result.data)
                                }
                                else -> {}
                            }
                        }
                        is Result.Error -> {
                            // Keep all vehicles if customer-specific loading fails
                        }
                        else -> {
                            // Keep current vehicles for unknown result types
                        }
                    }
                }
            } catch (e: Exception) {
                // Keep current vehicles
            }
        }
    }

    fun updateSelectedVehicle(vehicleId: String?) {
        val currentState = _uiState.value
        when (currentState) {
            is CreateOrderUiState.Step1Editing -> {
                val updatedFormData = currentState.formData.copy(selectedVehicleId = vehicleId)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            is CreateOrderUiState.Step2Editing -> {
                val updatedFormData = currentState.formData.copy(selectedVehicleId = vehicleId)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            else -> {}
        }
    }

    fun updateSelectedEmployee(employeeId: String?) {
        val currentState = _uiState.value
        when (currentState) {
            is CreateOrderUiState.Step1Editing -> {
                val updatedFormData = currentState.formData.copy(selectedEmployeeId = employeeId)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            is CreateOrderUiState.Step2Editing -> {
                val updatedFormData = currentState.formData.copy(selectedEmployeeId = employeeId)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            else -> {}
        }
    }

    fun updatePriority(priority: String) {
        val currentState = _uiState.value
        when (currentState) {
            is CreateOrderUiState.Step1Editing -> {
                val updatedFormData = currentState.formData.copy(priority = priority)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            is CreateOrderUiState.Step2Editing -> {
                val updatedFormData = currentState.formData.copy(priority = priority)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            else -> {}
        }
    }

    fun updateDescription(description: String) {
        val currentState = _uiState.value
        when (currentState) {
            is CreateOrderUiState.Step1Editing -> {
                val updatedFormData = currentState.formData.copy(description = description)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            is CreateOrderUiState.Step2Editing -> {
                val updatedFormData = currentState.formData.copy(description = description)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            else -> {}
        }
    }

    fun updateIsCustomDecal(isCustom: Boolean) {
        val currentState = _uiState.value
        when (currentState) {
            is CreateOrderUiState.Step1Editing -> {
                val updatedFormData = currentState.formData.copy(isCustomDecal = isCustom)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            is CreateOrderUiState.Step2Editing -> {
                val updatedFormData = currentState.formData.copy(isCustomDecal = isCustom)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            else -> {}
        }
    }

    fun updateExpectedArrivalTime(time: String) {
        // Validate that the time is not in the past
        if (time.isNotBlank() && time != "Chọn ngày và giờ") {
            try {
                val dateTimeFormat =
                        java.text.SimpleDateFormat(
                                "yyyy-MM-dd HH:mm",
                                java.util.Locale.getDefault()
                        )
                val selectedDateTime = dateTimeFormat.parse(time)
                val currentDateTime = java.util.Date()

                if (selectedDateTime != null && selectedDateTime.before(currentDateTime)) {
                    // Time is in the past, don't update
                    return
                }
            } catch (e: Exception) {
                // Invalid format, allow update (will be handled by other validation)
            }
        }

        val currentState = _uiState.value
        when (currentState) {
            is CreateOrderUiState.Step1Editing -> {
                val updatedFormData = currentState.formData.copy(expectedArrivalTime = time)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            is CreateOrderUiState.Step2Editing -> {
                val updatedFormData = currentState.formData.copy(expectedArrivalTime = time)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            else -> {}
        }
    }

    fun addService(service: DecalService, quantity: Int) {
        val currentState = _uiState.value
        when (currentState) {
            is CreateOrderUiState.Step1Editing -> {
                val orderDetailFormData =
                        OrderDetailFormData(
                                serviceId = service.serviceId,
                                serviceName = service.serviceName,
                                quantity = quantity,
                                unitPrice = service.price,
                                totalPrice = service.price * quantity
                        )

                val updatedServices = currentState.formData.selectedServices + orderDetailFormData
                val updatedFormData = currentState.formData.copy(selectedServices = updatedServices)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            is CreateOrderUiState.Step2Editing -> {
                val orderDetailFormData =
                        OrderDetailFormData(
                                serviceId = service.serviceId,
                                serviceName = service.serviceName,
                                quantity = quantity,
                                unitPrice = service.price,
                                totalPrice = service.price * quantity
                        )

                val updatedServices = currentState.formData.selectedServices + orderDetailFormData
                val updatedFormData = currentState.formData.copy(selectedServices = updatedServices)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            else -> {}
        }
    }

    fun removeService(serviceId: String) {
        val currentState = _uiState.value
        when (currentState) {
            is CreateOrderUiState.Step1Editing -> {
                val updatedServices =
                        currentState.formData.selectedServices.filter { it.serviceId != serviceId }
                val updatedFormData = currentState.formData.copy(selectedServices = updatedServices)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            is CreateOrderUiState.Step2Editing -> {
                val updatedServices =
                        currentState.formData.selectedServices.filter { it.serviceId != serviceId }
                val updatedFormData = currentState.formData.copy(selectedServices = updatedServices)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            else -> {}
        }
    }

    fun updateServiceQuantity(serviceId: String, quantity: Int) {
        val currentState = _uiState.value
        when (currentState) {
            is CreateOrderUiState.Step1Editing -> {
                val updatedServices =
                        currentState.formData.selectedServices.map { service ->
                            if (service.serviceId == serviceId) {
                                service.copy(
                                        quantity = quantity,
                                        totalPrice = service.unitPrice * quantity
                                )
                            } else {
                                service
                            }
                        }
                val updatedFormData = currentState.formData.copy(selectedServices = updatedServices)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            is CreateOrderUiState.Step2Editing -> {
                val updatedServices =
                        currentState.formData.selectedServices.map { service ->
                            if (service.serviceId == serviceId) {
                                service.copy(
                                        quantity = quantity,
                                        totalPrice = service.unitPrice * quantity
                                )
                            } else {
                                service
                            }
                        }
                val updatedFormData = currentState.formData.copy(selectedServices = updatedServices)
                _uiState.value = currentState.copy(formData = updatedFormData)
            }
            else -> {}
        }
    }

    fun createOrder() {
        val currentState = _uiState.value
        if (currentState !is CreateOrderUiState.Step1Editing || !currentState.formData.isValid) {
            println("CreateOrder: Form invalid or wrong state")
            return
        }

        viewModelScope.launch {
            _uiState.value = CreateOrderUiState.Loading
            println("CreateOrder: Starting order creation...")

            try {
                val formData = currentState.formData
                println(
                        "CreateOrder FormData: customerId=${formData.selectedCustomerId}, vehicleId=${formData.selectedVehicleId}, employeeId=${formData.selectedEmployeeId}"
                )

                // Debug: In danh sách customers để kiểm tra
                println("CreateOrder: Available customers:")
                currentState.customers.forEach { customer ->
                    println("  - ID: ${customer.customerId}, Name: ${customer.fullName}")
                }

                // STEP 1 DEBUG: Use selected customer and vehicle from UI
                println("CreateOrder: STEP 1 DEBUG - Using selected customer and vehicle from UI")
                println(
                        "CreateOrder FormData: priority=${formData.priority}, expectedArrivalTime=${formData.expectedArrivalTime}"
                )
                println(
                        "CreateOrder FormData: services count=${formData.selectedServices.size}, totalAmount=${formData.totalAmount}"
                )
                // Validate required fields before creating order
                if (formData.selectedCustomerId.isNullOrBlank()) {
                    _uiState.value = CreateOrderUiState.Error("Vui lòng chọn khách hàng")
                    return@launch
                }

                if (formData.selectedVehicleId.isNullOrBlank()) {
                    _uiState.value = CreateOrderUiState.Error("Vui lòng chọn xe")
                    return@launch
                }

                if (formData.selectedEmployeeId.isNullOrBlank()) {
                    _uiState.value = CreateOrderUiState.Error("Vui lòng chọn nhân viên")
                    return@launch
                }

                if (formData.selectedServices.isEmpty()) {
                    _uiState.value = CreateOrderUiState.Error("Vui lòng chọn ít nhất một dịch vụ")
                    return@launch
                }

                // Validate vehicle exists
                val selectedVehicle =
                        currentState.vehicles.find { it.vehicleID == formData.selectedVehicleId }
                if (selectedVehicle == null) {
                    _uiState.value = CreateOrderUiState.Error("Xe được chọn không tồn tại")
                    return@launch
                }

                // Validate employee exists
                val selectedEmployee =
                        currentState.employees.find { it.employeeId == formData.selectedEmployeeId }
                if (selectedEmployee == null) {
                    _uiState.value = CreateOrderUiState.Error("Nhân viên được chọn không tồn tại")
                    return@launch
                }

                // Validate customer exists
                val selectedCustomer =
                        currentState.customers.find { it.customerId == formData.selectedCustomerId }
                if (selectedCustomer == null) {
                    _uiState.value = CreateOrderUiState.Error("Khách hàng được chọn không tồn tại")
                    return@launch
                }

                val order =
                        Order(
                                orderId = "", // Will be generated by backend
                                orderNumber = "",
                                customerId = formData.selectedCustomerId,
                                customerFullName = "",
                                vehicleId = formData.selectedVehicleId,
                                vehicleLicensePlate = null,
                                assignedEmployeeId = formData.selectedEmployeeId,
                                assignedEmployeeName = null,
                                orderStatus = "Pending",
                                currentStage = "Initial",
                                totalAmount = formData.totalAmount, // Use calculated total
                                depositAmount = 0.0,
                                remainingAmount = 0.0,
                                orderDate = "",
                                expectedCompletionDate = null,
                                actualCompletionDate = null,
                                notes = formData.description.takeIf { it.isNotBlank() },
                                isActive = true,
                                createdAt = "",
                                updatedAt = null,
                                chassisNumber = null,
                                vehicleModelName = null,
                                vehicleBrandName = null,
                                expectedArrivalTime =
                                        formData.expectedArrivalTime.takeIf { it.isNotBlank() },
                                priority = formData.priority.takeIf { it.isNotBlank() } ?: "Medium",
                                isCustomDecal = formData.isCustomDecal,
                                storeId = null, // Backend sẽ tự xử lý StoreID
                                description = formData.description.takeIf { it.isNotBlank() },
                                customerPhoneNumber = null,
                                customerEmail = null,
                                customerAddress = null,
                                accountId = null,
                                accountUsername = null,
                                accountCreated = null
                        )

                println("CreateOrder: Calling orderRepository.createOrder...")
                orderRepository.createOrder(order).collect { result ->
                    println("CreateOrder API Result: $result")
                    when (result) {
                        is Result.Success -> {
                            println("CreateOrder: Order created successfully!")
                            val createdOrder = result.data

                            // Chuyển sang Step 2 để tạo OrderDetails
                            _uiState.value =
                                    CreateOrderUiState.Step2Editing(
                                            createdOrder = createdOrder,
                                            formData = formData,
                                            customers = currentState.customers,
                                            vehicles = currentState.vehicles,
                                            employees = currentState.employees,
                                            decalServices = currentState.decalServices
                                    )
                        }
                        is Result.Error -> {
                            println("CreateOrder ERROR: ${result.message}")
                            _uiState.value = CreateOrderUiState.Error(result.message)
                        }
                        else -> {
                            println("CreateOrder UNKNOWN ERROR: $result")
                            _uiState.value =
                                    CreateOrderUiState.Error("Unknown error creating order")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CreateOrderUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }

    fun createOrderDetails() {
        val currentState = _uiState.value
        if (currentState !is CreateOrderUiState.Step2Editing) {
            println("CreateOrderDetails: Wrong state")
            return
        }

        val orderId = currentState.createdOrder.orderId
        val services = currentState.formData.selectedServices

        createOrderDetailsInternal(orderId, services)
    }

    fun createNewOrder(order: Order, services: List<OrderDetailFormData>) {
        viewModelScope.launch {
            try {
                // Tạo Order trước
                orderRepository.createOrder(order).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val createdOrder = result.data
                            println(
                                    "CreateNewOrder: Order created successfully with ID: ${createdOrder.orderId}"
                            )

                            // Tạo OrderDetails sau khi tạo Order thành công
                            createOrderDetailsInternal(createdOrder.orderId, services)
                        }
                        is Result.Error -> {
                            println("CreateNewOrder ERROR: ${result.message}")
                        }
                        else -> {
                            println("CreateNewOrder UNKNOWN ERROR: $result")
                        }
                    }
                }
            } catch (e: Exception) {
                println("CreateNewOrder EXCEPTION: ${e.message}")
            }
        }
    }

    // Method đơn giản cho CreateOrderScreen cũ
    fun createOrderWithDetails(order: Order, services: List<DecalService>) {
        viewModelScope.launch {
            try {
                // Tạo Order trước
                orderRepository.createOrder(order).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val createdOrder = result.data
                            println(
                                    "CreateOrderWithDetails: Order created successfully with ID: ${createdOrder.orderId}"
                            )

                            // Convert DecalService to OrderDetailFormData
                            val orderDetailFormData =
                                    services.map { service ->
                                        OrderDetailFormData(
                                                serviceId = service.serviceId,
                                                serviceName = service.serviceName,
                                                quantity = 1,
                                                unitPrice = service.price,
                                                totalPrice = service.price
                                        )
                                    }

                            // Tạo OrderDetails
                            createOrderDetailsInternal(createdOrder.orderId, orderDetailFormData)
                        }
                        is Result.Error -> {
                            println("CreateOrderWithDetails ERROR: ${result.message}")
                        }
                        else -> {
                            println("CreateOrderWithDetails UNKNOWN ERROR: $result")
                        }
                    }
                }
            } catch (e: Exception) {
                println("CreateOrderWithDetails EXCEPTION: ${e.message}")
            }
        }
    }

    private fun createOrderDetailsInternal(orderId: String, services: List<OrderDetailFormData>) {
        viewModelScope.launch {
            try {
                println(
                        "CreateOrderDetails: Creating ${services.size} order details for order: $orderId"
                )

                var successCount = 0
                var errorCount = 0

                // Tạo OrderDetails tuần tự sử dụng collect thay vì first()
                for (service in services) {
                    try {
                        // Tạo OrderDetail domain model - repository sẽ tự động convert sang DTO
                        val orderDetail =
                                OrderDetail(
                                        orderDetailId = "", // Sẽ được tạo bởi backend
                                        orderId = orderId,
                                        serviceId = service.serviceId,
                                        serviceName =
                                                service.serviceName, // Chỉ để hiển thị, không gửi
                                        // lên backend
                                        quantity = service.quantity,
                                        unitPrice =
                                                service.unitPrice, // Chỉ để hiển thị, không gửi lên
                                        // backend
                                        totalPrice =
                                                service.totalPrice, // Chỉ để hiển thị, không gửi
                                        // lên backend
                                        description = null
                                )

                        // Debug: Log thông tin OrderDetail trước khi gửi
                        println(
                                "CreateOrderDetails: Sending OrderDetail - orderId: ${orderDetail.orderId}, serviceId: ${orderDetail.serviceId}, quantity: ${orderDetail.quantity}"
                        )

                        // Sử dụng collect để xử lý Flow đúng cách
                        var result: Result<OrderDetail>? = null
                        orderDetailRepository.createOrderDetail(orderDetail).collect { flowResult ->
                            result = flowResult
                            println(
                                    "CreateOrderDetails: Flow result for service ${service.serviceName}: $flowResult"
                            )
                        }

                        val finalResult = result
                        when (finalResult) {
                            is Result.Success -> {
                                successCount++
                                println(
                                        "CreateOrderDetails: Successfully created detail for service: ${service.serviceName}"
                                )
                            }
                            is Result.Error -> {
                                errorCount++
                                println(
                                        "CreateOrderDetails: Failed to create detail for service: ${service.serviceName}, Error: ${finalResult.message}"
                                )
                            }
                            else -> {
                                errorCount++
                                println(
                                        "CreateOrderDetails: Unknown error for service: ${service.serviceName}"
                                )
                            }
                        }
                    } catch (e: Exception) {
                        errorCount++
                        println(
                                "CreateOrderDetails: Exception creating detail for service: ${service.serviceName}, Error: ${e.message}"
                        )
                    }
                }

                println(
                        "CreateOrderDetails: Completed - Success: $successCount, Errors: $errorCount"
                )

                if (errorCount == 0) {
                    // All order details created successfully
                    _uiState.value =
                            CreateOrderUiState.Success(
                                    Order(
                                            orderId = orderId,
                                            orderNumber = "",
                                            customerId = "",
                                            customerFullName = "",
                                            vehicleId = "",
                                            vehicleLicensePlate = null,
                                            assignedEmployeeId = "",
                                            assignedEmployeeName = null,
                                            orderStatus = "Pending",
                                            currentStage = "Initial",
                                            totalAmount = services.sumOf { it.totalPrice },
                                            depositAmount = 0.0,
                                            remainingAmount = 0.0,
                                            orderDate = "",
                                            expectedCompletionDate = null,
                                            actualCompletionDate = null,
                                            notes = null,
                                            isActive = true,
                                            createdAt = "",
                                            updatedAt = null,
                                            chassisNumber = null,
                                            vehicleModelName = null,
                                            vehicleBrandName = null,
                                            expectedArrivalTime = null,
                                            priority = null,
                                            isCustomDecal = false,
                                            storeId = null,
                                            description = null,
                                            customerPhoneNumber = null,
                                            customerEmail = null,
                                            customerAddress = null,
                                            accountId = null,
                                            accountUsername = null,
                                            accountCreated = null
                                    )
                            )
                } else {
                    // Có lỗi khi tạo OrderDetail nhưng Order đã tạo thành công - vẫn quay về màn
                    // hình đơn hàng
                    _uiState.value =
                            CreateOrderUiState.Success(
                                    Order(
                                            orderId = orderId,
                                            orderNumber = "",
                                            customerId = "",
                                            customerFullName = "",
                                            vehicleId = "",
                                            vehicleLicensePlate = null,
                                            assignedEmployeeId = "",
                                            assignedEmployeeName = null,
                                            orderStatus = "Pending",
                                            currentStage = "Initial",
                                            totalAmount = services.sumOf { it.totalPrice },
                                            depositAmount = 0.0,
                                            remainingAmount = 0.0,
                                            orderDate = "",
                                            expectedCompletionDate = null,
                                            actualCompletionDate = null,
                                            notes = null,
                                            isActive = true,
                                            createdAt = "",
                                            updatedAt = null,
                                            chassisNumber = null,
                                            vehicleModelName = null,
                                            vehicleBrandName = null,
                                            expectedArrivalTime = null,
                                            priority = null,
                                            isCustomDecal = false,
                                            storeId = null,
                                            description = null,
                                            customerPhoneNumber = null,
                                            customerEmail = null,
                                            customerAddress = null,
                                            accountId = null,
                                            accountUsername = null,
                                            accountCreated = null
                                    )
                            )
                }
            } catch (e: Exception) {
                println("CreateOrderDetails: Unexpected error: ${e.message}")
                _uiState.value =
                        CreateOrderUiState.Error("Lỗi khi tạo chi tiết đơn hàng: ${e.message}")
            }
        }
    }
}

sealed class CreateOrderUiState {
    data class Step1Editing(
            val formData: OrderFormData,
            val customers: List<Customer>,
            val vehicles: List<CustomerVehicle>,
            val employees: List<Employee>,
            val decalServices: List<DecalService>
    ) : CreateOrderUiState()

    data class Step2Editing(
            val createdOrder: Order,
            val formData: OrderFormData,
            val customers: List<Customer>,
            val vehicles: List<CustomerVehicle>,
            val employees: List<Employee>,
            val decalServices: List<DecalService>
    ) : CreateOrderUiState()

    object Loading : CreateOrderUiState()
    data class Success(val order: Order) : CreateOrderUiState()
    data class Error(val message: String) : CreateOrderUiState()
}

// Helper function để lấy StoreID từ employee được chọn (assignedEmployeeId)
private fun getStoreIdFromSelectedEmployee(
        employees: List<Employee>,
        selectedEmployeeId: String?
): String {
    return try {
        println("CreateOrderViewModel: Available employees count: ${employees.size}")
        println("CreateOrderViewModel: Selected employeeId: $selectedEmployeeId")

        if (selectedEmployeeId != null) {
            // Debug: In ra tất cả employees để check
            employees.forEach { emp ->
                println(
                        "CreateOrderViewModel: Employee - ID: ${emp.employeeId}, Name: ${emp.firstName} ${emp.lastName}, StoreID: ${emp.storeId}"
                )
            }

            // Tìm employee được chọn trong danh sách để lấy storeId
            val selectedEmployee = employees.find { it.employeeId == selectedEmployeeId }
            println(
                    "CreateOrderViewModel: Found selected employee: ${selectedEmployee?.firstName} ${selectedEmployee?.lastName}, StoreID: ${selectedEmployee?.storeId}"
            )

            selectedEmployee?.storeId ?: "STORE001" // Fallback nếu không tìm thấy
        } else {
            println("CreateOrderViewModel: No selected employeeId, using fallback")
            "STORE001" // Fallback hardcode
        }
    } catch (e: Exception) {
        println("CreateOrderViewModel: Error getting selected employee storeId: ${e.message}")
        "STORE001" // Fallback hardcode
    }
}

data class OrderDetailFormData(
        val serviceId: String,
        val serviceName: String,
        val quantity: Int,
        val unitPrice: Double,
        val totalPrice: Double
)

data class OrderFormData(
        val selectedCustomerId: String? = null,
        val selectedVehicleId: String? = null,
        val selectedEmployeeId: String? = null,
        val priority: String = "",
        val description: String = "",
        val isCustomDecal: Boolean = false,
        val expectedArrivalTime: String = "",
        val selectedServices: List<OrderDetailFormData> = emptyList()
) {
    val totalAmount: Double
        get() = selectedServices.sumOf { it.totalPrice }

    val isValid: Boolean
        get() =
                selectedCustomerId != null &&
                        selectedVehicleId != null &&
                        selectedEmployeeId != null &&
                        selectedServices.isNotEmpty() &&
                        totalAmount > 0
}
