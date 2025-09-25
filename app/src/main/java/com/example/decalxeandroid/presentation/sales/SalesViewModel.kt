package com.example.decalxeandroid.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.*
import com.example.decalxeandroid.domain.usecase.sales.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SalesViewModel(
    private val getSalesEmployeeInfoUseCase: GetSalesEmployeeInfoUseCase,
    private val getSalesStoreOrdersUseCase: GetSalesStoreOrdersUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val getDecalServicesUseCase: GetDecalServicesUseCase,
    private val getCustomersUseCase: com.example.decalxeandroid.domain.usecase.customer.GetCustomersUseCase,
    private val createCustomerUseCase: com.example.decalxeandroid.domain.usecase.customer.CreateCustomerUseCase,
    private val getCustomerVehiclesUseCase: com.example.decalxeandroid.domain.usecase.vehicle.GetCustomerVehiclesUseCase,
    private val createCustomerVehicleUseCase: com.example.decalxeandroid.domain.usecase.vehicle.CreateCustomerVehicleUseCase
) : ViewModel() {
    
    private val _currentEmployee = MutableStateFlow<Employee?>(null)
    val currentEmployee: StateFlow<Employee?> = _currentEmployee.asStateFlow()
    
    private val _storeId = MutableStateFlow<String?>(null)
    val storeId: StateFlow<String?> = _storeId.asStateFlow()
    
    private val _storeOrders = MutableStateFlow<List<Order>>(emptyList())
    val storeOrders: StateFlow<List<Order>> = _storeOrders.asStateFlow()
    
    private val _ordersLoading = MutableStateFlow(false)
    val ordersLoading: StateFlow<Boolean> = _ordersLoading.asStateFlow()
    
    private val _ordersError = MutableStateFlow<String?>(null)
    val ordersError: StateFlow<String?> = _ordersError.asStateFlow()
    
    private val _decalServices = MutableStateFlow<List<DecalService>>(emptyList())
    val decalServices: StateFlow<List<DecalService>> = _decalServices.asStateFlow()
    
    private val _servicesLoading = MutableStateFlow(false)
    val servicesLoading: StateFlow<Boolean> = _servicesLoading.asStateFlow()
    
    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()
    
    private val _customerVehicles = MutableStateFlow<List<CustomerVehicle>>(emptyList())
    val customerVehicles: StateFlow<List<CustomerVehicle>> = _customerVehicles.asStateFlow()
    
    private val _salesStatistics = MutableStateFlow<SalesStatistics?>(null)
    val salesStatistics: StateFlow<SalesStatistics?> = _salesStatistics.asStateFlow()
    
    fun initializeSales(employeeId: String) {
        viewModelScope.launch {
            loadEmployeeInfo(employeeId)
            loadDecalServices()
            loadCustomers()
            loadCustomerVehicles()
        }
    }
    
    private suspend fun loadEmployeeInfo(employeeId: String) {
        getSalesEmployeeInfoUseCase(employeeId).collect { result ->
            when (result) {
                is Result.Success -> {
                    _currentEmployee.value = result.data
                    _storeId.value = result.data.storeId
                    // Load store orders when we have store ID
                    result.data.storeId?.let { storeId ->
                        loadStoreOrders(storeId)
                    }
                }
                is Result.Error -> {
                    _ordersError.value = result.message
                }
                is Result.Loading -> {
                    _ordersLoading.value = true
                }
            }
        }
    }
    
    fun loadStoreOrders(storeId: String) {
        viewModelScope.launch {
            _ordersLoading.value = true
            _ordersError.value = null
            
            getSalesStoreOrdersUseCase(storeId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _storeOrders.value = result.data
                        _ordersLoading.value = false
                        calculateSalesStatistics(result.data)
                    }
                    is Result.Error -> {
                        _ordersError.value = result.message
                        _ordersLoading.value = false
                    }
                    is Result.Loading -> {
                        _ordersLoading.value = true
                    }
                }
            }
        }
    }
    
    private fun calculateSalesStatistics(orders: List<Order>) {
        val currentEmployee = _currentEmployee.value ?: return
        
        val totalOrders = orders.size
        val totalRevenue = orders.sumOf { it.totalAmount }
        val completedOrders = orders.count { it.orderStatus == "Completed" }
        val pendingOrders = orders.count { it.orderStatus == "Pending" }
        val inProgressOrders = orders.count { it.orderStatus == "InProgress" }
        
        _salesStatistics.value = SalesStatistics(
            totalOrders = totalOrders,
            totalRevenue = totalRevenue,
            completedOrders = completedOrders,
            pendingOrders = pendingOrders,
            inProgressOrders = inProgressOrders,
            completionRate = if (totalOrders > 0) (completedOrders.toDouble() / totalOrders) * 100 else 0.0
        )
    }
    
    private suspend fun loadDecalServices() {
        _servicesLoading.value = true
        
        getDecalServicesUseCase().collect { result ->
            when (result) {
                is Result.Success -> {
                    _decalServices.value = result.data
                    _servicesLoading.value = false
                }
                is Result.Error -> {
                    _servicesLoading.value = false
                }
                is Result.Loading -> {
                    _servicesLoading.value = true
                }
            }
        }
    }
    
    private suspend fun loadCustomers() {
        getCustomersUseCase().collect { result ->
            when (result) {
                is Result.Success -> {
                    _customers.value = result.data
                }
                is Result.Error -> {
                    // Handle error
                }
                is Result.Loading -> {
                    // Handle loading
                }
            }
        }
    }
    
    private suspend fun loadCustomerVehicles() {
        getCustomerVehiclesUseCase().collect { result ->
            when (result) {
                is Result.Success -> {
                    _customerVehicles.value = result.data
                }
                is Result.Error -> {
                    // Handle error
                }
                is Result.Loading -> {
                    // Handle loading
                }
            }
        }
    }
    
    fun createNewOrder(order: Order) {
        viewModelScope.launch {
            createOrderUseCase(order).collect { result ->
                when (result) {
                    is Result.Success -> {
                        // Refresh store orders
                        _storeId.value?.let { storeId ->
                            loadStoreOrders(storeId)
                        }
                    }
                    is Result.Error -> {
                        _ordersError.value = result.message
                    }
                    is Result.Loading -> {
                        _ordersLoading.value = true
                    }
                }
            }
        }
    }
    
    fun refreshData() {
        viewModelScope.launch {
            _storeId.value?.let { storeId ->
                loadStoreOrders(storeId)
            }
            loadDecalServices()
            loadCustomers()
            loadCustomerVehicles()
        }
    }
}

data class SalesStatistics(
    val totalOrders: Int,
    val totalRevenue: Double,
    val completedOrders: Int,
    val pendingOrders: Int,
    val inProgressOrders: Int,
    val completionRate: Double
)
