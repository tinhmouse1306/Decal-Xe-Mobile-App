package com.example.decalxeandroid.presentation.customers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.repository.CustomerRepository
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import com.example.decalxeandroid.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class CustomerDetailViewModel(
    private val customerId: String,
    private val customerRepository: CustomerRepository,
    private val customerVehicleRepository: CustomerVehicleRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CustomerDetailUiState>(CustomerDetailUiState.Loading)
    val uiState: StateFlow<CustomerDetailUiState> = _uiState.asStateFlow()
    
    private val _deleteState = MutableStateFlow<DeleteCustomerState>(DeleteCustomerState.Idle)
    val deleteState: StateFlow<DeleteCustomerState> = _deleteState.asStateFlow()
    
    init {
        loadCustomer()
    }
    
    fun loadCustomer() {
        viewModelScope.launch {
            _uiState.value = CustomerDetailUiState.Loading
            Log.d(TAG, "Loading customer details for ID: $customerId")
            
            try {
                // Load customer details
                Log.d(TAG, "Fetching customer data...")
                val customerResult = customerRepository.getCustomerById(customerId)
                customerResult.collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            val customer = result.data
                            Log.d(TAG, "Successfully loaded customer: ${customer.fullName}")
                            
                            // Load customer vehicles and orders concurrently
                            loadVehiclesAndOrders(customer)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            Log.e(TAG, "Failed to load customer: ${result.message}")
                            _uiState.value = CustomerDetailUiState.Error(
                                "Không thể tải thông tin khách hàng: ${result.message}"
                            )
                        }
                        else -> {
                            Log.e(TAG, "Unknown customer result type")
                            _uiState.value = CustomerDetailUiState.Error(
                                "Kết quả khách hàng không xác định"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading customer", e)
                _uiState.value = CustomerDetailUiState.Error(
                    "Lỗi không xác định: ${e.message}"
                )
            }
        }
    }
    
    private suspend fun loadVehiclesAndOrders(customer: Customer) {
        var vehicles = emptyList<CustomerVehicle>()
        var orders = emptyList<Order>()
        var vehiclesLoaded = false
        var ordersLoaded = false
        
        // Load vehicles
        val vehiclesFlow = customerVehicleRepository.getVehiclesByCustomerId(customerId)
        vehiclesFlow.collect { vehiclesResult ->
            when (vehiclesResult) {
                is com.example.decalxeandroid.domain.model.Result.Success -> {
                    vehicles = vehiclesResult.data
                    vehiclesLoaded = true
                    Log.d(TAG, "Successfully loaded ${vehicles.size} vehicles for customer")
                }
                is com.example.decalxeandroid.domain.model.Result.Error -> {
                    Log.w(TAG, "Failed to load vehicles: ${vehiclesResult.message}")
                    vehicles = emptyList()
                    vehiclesLoaded = true
                }
                else -> {
                    Log.w(TAG, "Unknown vehicles result type")
                    vehicles = emptyList()
                    vehiclesLoaded = true
                }
            }
            
            // Update UI if both are loaded
            if (vehiclesLoaded && ordersLoaded) {
                _uiState.value = CustomerDetailUiState.Success(
                    customer = customer,
                    vehicles = vehicles,
                    orders = orders
                )
            }
        }
        
        // Load orders
        val ordersFlow = orderRepository.getOrdersByCustomerId(customerId)
        ordersFlow.collect { ordersResult ->
            when (ordersResult) {
                is com.example.decalxeandroid.domain.model.Result.Success -> {
                    orders = ordersResult.data
                    ordersLoaded = true
                    Log.d(TAG, "Successfully loaded ${orders.size} orders for customer")
                }
                is com.example.decalxeandroid.domain.model.Result.Error -> {
                    Log.w(TAG, "Failed to load orders: ${ordersResult.message}")
                    orders = emptyList()
                    ordersLoaded = true
                }
                else -> {
                    Log.w(TAG, "Unknown orders result type")
                    orders = emptyList()
                    ordersLoaded = true
                }
            }
            
            // Update UI if both are loaded
            if (vehiclesLoaded && ordersLoaded) {
                _uiState.value = CustomerDetailUiState.Success(
                    customer = customer,
                    vehicles = vehicles,
                    orders = orders
                )
            }
        }
    }
    
    companion object {
        private const val TAG = "CustomerDetailViewModel"
    }
    
    fun editCustomer(onNavigateToEdit: (String) -> Unit) {
        Log.d(TAG, "Navigating to edit customer: $customerId")
        onNavigateToEdit(customerId)
    }
    
    fun showDeleteConfirmation() {
        _deleteState.value = DeleteCustomerState.ConfirmationRequired
    }
    
    fun dismissDeleteConfirmation() {
        _deleteState.value = DeleteCustomerState.Idle
    }
    
    fun deleteCustomer(onNavigateBack: () -> Unit) {
        viewModelScope.launch {
            _deleteState.value = DeleteCustomerState.Deleting
            Log.d(TAG, "Starting delete process for customer: $customerId")
            
            try {
                // First check if customer has related data
                val currentState = uiState.value
                if (currentState is CustomerDetailUiState.Success) {
                    val hasVehicles = currentState.vehicles.isNotEmpty()
                    val hasOrders = currentState.orders.isNotEmpty()
                    
                    if (hasVehicles || hasOrders) {
                        val conflicts = mutableListOf<String>()
                        if (hasVehicles) conflicts.add("${currentState.vehicles.size} xe")
                        if (hasOrders) conflicts.add("${currentState.orders.size} đơn hàng")
                        
                        _deleteState.value = DeleteCustomerState.Error(
                            "Không thể xóa khách hàng vì còn liên kết với: ${conflicts.joinToString(", ")}. " +
                            "Vui lòng xóa các dữ liệu liên quan trước."
                        )
                        return@launch
                    }
                }
                
                // Proceed with deletion
                customerRepository.deleteCustomer(customerId).collect { deleteResult ->
                    when (deleteResult) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            Log.d(TAG, "Successfully deleted customer: $customerId")
                            _deleteState.value = DeleteCustomerState.Success
                            
                            // Navigate back after a short delay to show success message
                            kotlinx.coroutines.delay(1500)
                            onNavigateBack()
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            Log.e(TAG, "Failed to delete customer: ${deleteResult.message}")
                            _deleteState.value = DeleteCustomerState.Error(
                                "Không thể xóa khách hàng: ${deleteResult.message}"
                            )
                        }
                        else -> {
                            Log.e(TAG, "Unknown result type for delete customer")
                            _deleteState.value = DeleteCustomerState.Error(
                                "Lỗi không xác định khi xóa khách hàng"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during delete customer", e)
                _deleteState.value = DeleteCustomerState.Error(
                    "Lỗi kết nối: ${e.message}"
                )
            }
        }
    }
    
    fun resetDeleteState() {
        _deleteState.value = DeleteCustomerState.Idle
    }
}

sealed class CustomerDetailUiState {
    object Loading : CustomerDetailUiState()
    data class Success(
        val customer: Customer,
        val vehicles: List<CustomerVehicle>,
        val orders: List<Order>
    ) : CustomerDetailUiState()
    data class Error(val message: String) : CustomerDetailUiState()
}

sealed class DeleteCustomerState {
    object Idle : DeleteCustomerState()
    object ConfirmationRequired : DeleteCustomerState()
    object Deleting : DeleteCustomerState()
    object Success : DeleteCustomerState()
    data class Error(val message: String) : DeleteCustomerState()
}
