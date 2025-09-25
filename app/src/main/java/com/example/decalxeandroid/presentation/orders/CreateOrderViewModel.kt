package com.example.decalxeandroid.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.*
import com.example.decalxeandroid.domain.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CreateOrderViewModel(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val customerVehicleRepository: CustomerVehicleRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CreateOrderUiState>(
        CreateOrderUiState.Editing(
            formData = OrderFormData(),
            customers = emptyList(),
            vehicles = emptyList(),
            employees = emptyList()
        )
    )
    val uiState: StateFlow<CreateOrderUiState> = _uiState.asStateFlow()
    
    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = CreateOrderUiState.Loading
            
            try {
                // Load all data in parallel
                val customersFlow = customerRepository.getCustomers()
                val vehiclesFlow = customerVehicleRepository.getVehicles()
                val employeesFlow = employeeRepository.getEmployees()
                
                // Combine all flows
                kotlinx.coroutines.flow.combine(
                    customersFlow,
                    vehiclesFlow,
                    employeesFlow
                ) { customersResult, vehiclesResult, employeesResult ->
                    when {
                        customersResult is Result.Success && 
                        vehiclesResult is Result.Success && 
                        employeesResult is Result.Success -> {
                            _uiState.value = CreateOrderUiState.Editing(
                                formData = OrderFormData(),
                                customers = customersResult.data,
                                vehicles = vehiclesResult.data,
                                employees = employeesResult.data
                            )
                        }
                        customersResult is Result.Error -> {
                            _uiState.value = CreateOrderUiState.Error("Failed to load customers: ${customersResult.message}")
                        }
                        vehiclesResult is Result.Error -> {
                            _uiState.value = CreateOrderUiState.Error("Failed to load vehicles: ${vehiclesResult.message}")
                        }
                        employeesResult is Result.Error -> {
                            _uiState.value = CreateOrderUiState.Error("Failed to load employees: ${employeesResult.message}")
                        }
                        else -> {
                            _uiState.value = CreateOrderUiState.Error("Unknown error loading data")
                        }
                    }
                }.collect { }
            } catch (e: Exception) {
                _uiState.value = CreateOrderUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }
    
    fun updateSelectedCustomer(customerId: String?) {
        val currentState = _uiState.value
        if (currentState is CreateOrderUiState.Editing) {
            val updatedFormData = currentState.formData.copy(selectedCustomerId = customerId)
            _uiState.value = currentState.copy(formData = updatedFormData)
            
            // Load vehicles for selected customer
            if (customerId != null) {
                loadVehiclesForCustomer(customerId)
            }
        }
    }
    
    private fun loadVehiclesForCustomer(customerId: String) {
        viewModelScope.launch {
            try {
                customerVehicleRepository.getVehiclesByCustomerId(customerId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val currentState = _uiState.value
                            if (currentState is CreateOrderUiState.Editing) {
                                _uiState.value = currentState.copy(vehicles = result.data)
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
        if (currentState is CreateOrderUiState.Editing) {
            val updatedFormData = currentState.formData.copy(selectedVehicleId = vehicleId)
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateSelectedEmployee(employeeId: String?) {
        val currentState = _uiState.value
        if (currentState is CreateOrderUiState.Editing) {
            val updatedFormData = currentState.formData.copy(selectedEmployeeId = employeeId)
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateTotalAmount(amount: String) {
        val currentState = _uiState.value
        if (currentState is CreateOrderUiState.Editing) {
            val error = validateTotalAmount(amount)
            val updatedFormData = currentState.formData.copy(
                totalAmount = amount,
                totalAmountError = error
            )
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updatePriority(priority: String) {
        val currentState = _uiState.value
        if (currentState is CreateOrderUiState.Editing) {
            val updatedFormData = currentState.formData.copy(priority = priority)
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateDescription(description: String) {
        val currentState = _uiState.value
        if (currentState is CreateOrderUiState.Editing) {
            val updatedFormData = currentState.formData.copy(description = description)
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateIsCustomDecal(isCustom: Boolean) {
        val currentState = _uiState.value
        if (currentState is CreateOrderUiState.Editing) {
            val updatedFormData = currentState.formData.copy(isCustomDecal = isCustom)
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateExpectedArrivalTime(time: String) {
        val currentState = _uiState.value
        if (currentState is CreateOrderUiState.Editing) {
            val updatedFormData = currentState.formData.copy(expectedArrivalTime = time)
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun createOrder() {
        val currentState = _uiState.value
        if (currentState !is CreateOrderUiState.Editing || !currentState.formData.isValid) {
            return
        }
        
        viewModelScope.launch {
            _uiState.value = CreateOrderUiState.Loading
            
            try {
                val formData = currentState.formData
                val order = Order(
                    orderId = "", // Will be generated by backend
                    orderNumber = "",
                    customerId = formData.selectedCustomerId!!,
                    customerFullName = "",
                    vehicleId = formData.selectedVehicleId,
                    vehicleLicensePlate = null,
                    assignedEmployeeId = formData.selectedEmployeeId,
                    assignedEmployeeName = null,
                    orderStatus = "Pending",
                    currentStage = "Initial",
                    totalAmount = formData.totalAmount.toDoubleOrNull() ?: 0.0,
                    depositAmount = 0.0,
                    remainingAmount = 0.0,
                    orderDate = "",
                    expectedCompletionDate = formData.expectedArrivalTime.takeIf { it.isNotBlank() },
                    actualCompletionDate = null,
                    notes = formData.description.takeIf { it.isNotBlank() },
                    isActive = true,
                    createdAt = "",
                    updatedAt = null,
                    chassisNumber = null,
                    vehicleModelName = null,
                    vehicleBrandName = null,
                    expectedArrivalTime = formData.expectedArrivalTime.takeIf { it.isNotBlank() },
                    priority = formData.priority,
                    isCustomDecal = formData.isCustomDecal,
                    storeId = null,
                    description = formData.description.takeIf { it.isNotBlank() },
                    customerPhoneNumber = null,
                    customerEmail = null,
                    customerAddress = null,
                    accountId = null,
                    accountUsername = null,
                    accountCreated = null
                )
                
                orderRepository.createOrder(order).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.value = CreateOrderUiState.Success(result.data)
                        }
                        is Result.Error -> {
                            _uiState.value = CreateOrderUiState.Error(result.message)
                        }
                        else -> {
                            _uiState.value = CreateOrderUiState.Error("Unknown error creating order")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CreateOrderUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }
    
    private fun validateTotalAmount(amount: String): String? {
        return when {
            amount.isBlank() -> "Tổng tiền không được để trống"
            amount.toDoubleOrNull() == null -> "Tổng tiền không hợp lệ"
            amount.toDoubleOrNull()!! <= 0 -> "Tổng tiền phải lớn hơn 0"
            else -> null
        }
    }
}

sealed class CreateOrderUiState {
    data class Editing(
        val formData: OrderFormData,
        val customers: List<Customer>,
        val vehicles: List<CustomerVehicle>,
        val employees: List<Employee>
    ) : CreateOrderUiState()
    object Loading : CreateOrderUiState()
    data class Success(val order: Order) : CreateOrderUiState()
    data class Error(val message: String) : CreateOrderUiState()
}

data class OrderFormData(
    val selectedCustomerId: String? = null,
    val selectedVehicleId: String? = null,
    val selectedEmployeeId: String? = null,
    val totalAmount: String = "",
    val totalAmountError: String? = null,
    val priority: String = "Normal",
    val description: String = "",
    val isCustomDecal: Boolean = false,
    val expectedArrivalTime: String = ""
) {
    val isValid: Boolean
        get() = selectedCustomerId != null &&
                selectedVehicleId != null &&
                selectedEmployeeId != null &&
                totalAmount.isNotBlank() &&
                totalAmountError == null &&
                totalAmount.toDoubleOrNull() != null &&
                totalAmount.toDoubleOrNull()!! > 0
}
