package com.example.decalxeandroid.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.DecalService
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.repository.CustomerRepository
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import com.example.decalxeandroid.domain.repository.DecalServiceRepository
import com.example.decalxeandroid.domain.repository.EmployeeRepository
import com.example.decalxeandroid.domain.repository.OrderRepository
import com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardViewModel(
        private val orderRepository: OrderRepository,
        private val customerRepository: CustomerRepository,
        private val vehicleRepository: CustomerVehicleRepository,
        private val decalServiceRepository: DecalServiceRepository,
        private val employeeRepository: EmployeeRepository
) : ViewModel() {

        private val _uiState = MutableStateFlow(DashboardUiState())
        val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

        fun loadDashboardData() {
                viewModelScope.launch {
                        try {
                                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                                // Get current user info
                                val currentUser = GlobalAuthManager.currentUser.value
                                if (currentUser?.employeeId == null) {
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        error = "Không tìm thấy thông tin nhân viên"
                                                )
                                        return@launch
                                }

                                // Get employee info to find store ID
                                employeeRepository.getEmployees().collect { employeesResult ->
                                        when (employeesResult) {
                                                is com.example.decalxeandroid.domain.model.Result.Success -> {
                                                        val currentEmployee =
                                                                employeesResult.data.find {
                                                                        it.employeeId ==
                                                                                currentUser
                                                                                        .employeeId
                                                                }

                                                        if (currentEmployee?.storeId == null) {
                                                                _uiState.value =
                                                                        _uiState.value.copy(
                                                                                isLoading = false,
                                                                                error =
                                                                                        "Không tìm thấy thông tin cửa hàng"
                                                                        )
                                                                return@collect
                                                        }

                                                        val currentStoreId = currentEmployee.storeId
                                                        println(
                                                                "DashboardViewModel: Current store ID: $currentStoreId"
                                                        )

                                                        // Load orders and filter by store
                                                        orderRepository.getOrders().collect {
                                                                ordersResult ->
                                                                when (ordersResult) {
                                                                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                                                                                // Filter orders by
                                                                                // store
                                                                                val storeOrders =
                                                                                        ordersResult
                                                                                                .data
                                                                                                .filter {
                                                                                                        order
                                                                                                        ->
                                                                                                        // Check if order's assigned employee is in the same store
                                                                                                        val assignedEmployee =
                                                                                                                employeesResult
                                                                                                                        .data
                                                                                                                        .find {
                                                                                                                                it.employeeId ==
                                                                                                                                        order.assignedEmployeeId
                                                                                                                        }
                                                                                                        assignedEmployee
                                                                                                                ?.storeId ==
                                                                                                                currentStoreId
                                                                                                }

                                                                                // Load customers
                                                                                // and filter by
                                                                                // store
                                                                                customerRepository
                                                                                        .getCustomers()
                                                                                        .collect {
                                                                                                customersResult
                                                                                                ->
                                                                                                when (customersResult
                                                                                                ) {
                                                                                                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                                                                                                                // Filter customers by store (customers who have orders in this store)
                                                                                                                val storeCustomerIds =
                                                                                                                        storeOrders
                                                                                                                                .map {
                                                                                                                                        it.customerId
                                                                                                                                }
                                                                                                                val storeCustomers =
                                                                                                                        customersResult
                                                                                                                                .data
                                                                                                                                .filter {
                                                                                                                                        customer
                                                                                                                                        ->
                                                                                                                                        customer.customerId in
                                                                                                                                                storeCustomerIds
                                                                                                                                }

                                                                                                                // Load services
                                                                                                                decalServiceRepository
                                                                                                                        .getServices()
                                                                                                                        .collect {
                                                                                                                                servicesResult
                                                                                                                                ->
                                                                                                                                when (servicesResult
                                                                                                                                ) {
                                                                                                                                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                                                                                                                                                // Calculate statistics
                                                                                                                                                val totalOrders =
                                                                                                                                                        storeOrders
                                                                                                                                                                .size
                                                                                                                                                val totalCustomers =
                                                                                                                                                        storeCustomers
                                                                                                                                                                .size
                                                                                                                                                val totalServices =
                                                                                                                                                        servicesResult
                                                                                                                                                                .data
                                                                                                                                                                .size

                                                                                                                                                // Calculate total revenue from completed orders (Nghiệm thu và nhận hàng)
                                                                                                                                                val completedOrders =
                                                                                                                                                        storeOrders
                                                                                                                                                                .filter {
                                                                                                                                                                        order
                                                                                                                                                                        ->
                                                                                                                                                                        order.currentStage ==
                                                                                                                                                                                "AcceptanceAndDelivery" ||
                                                                                                                                                                                order.orderStatus ==
                                                                                                                                                                                        "Nghiệm thu và nhận hàng"
                                                                                                                                                                }
                                                                                                                                                val totalRevenue =
                                                                                                                                                        completedOrders
                                                                                                                                                                .sumOf {
                                                                                                                                                                        it.totalAmount
                                                                                                                                                                }

                                                                                                                                                println(
                                                                                                                                                        "DashboardViewModel: Store statistics - Orders: $totalOrders, Customers: $totalCustomers, Services: $totalServices, Revenue: $totalRevenue"
                                                                                                                                                )

                                                                                                                                                _uiState.value =
                                                                                                                                                        _uiState.value
                                                                                                                                                                .copy(
                                                                                                                                                                        isLoading =
                                                                                                                                                                                false,
                                                                                                                                                                        totalOrders =
                                                                                                                                                                                totalOrders,
                                                                                                                                                                        totalCustomers =
                                                                                                                                                                                totalCustomers,
                                                                                                                                                                        totalServices =
                                                                                                                                                                                totalServices,
                                                                                                                                                                        totalRevenue =
                                                                                                                                                                                totalRevenue,
                                                                                                                                                                        recentOrders =
                                                                                                                                                                                storeOrders
                                                                                                                                                                                        .take(
                                                                                                                                                                                                5
                                                                                                                                                                                        ),
                                                                                                                                                                        decalServices =
                                                                                                                                                                                servicesResult
                                                                                                                                                                                        .data
                                                                                                                                                                )
                                                                                                                                        }
                                                                                                                                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                                                                                                                                                _uiState.value =
                                                                                                                                                        _uiState.value
                                                                                                                                                                .copy(
                                                                                                                                                                        isLoading =
                                                                                                                                                                                false,
                                                                                                                                                                        error =
                                                                                                                                                                                servicesResult
                                                                                                                                                                                        .message
                                                                                                                                                                )
                                                                                                                                        }
                                                                                                                                        else -> {
                                                                                                                                                // Keep loading state
                                                                                                                                        }
                                                                                                                                }
                                                                                                                        }
                                                                                                        }
                                                                                                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                                                                                                                _uiState.value =
                                                                                                                        _uiState.value
                                                                                                                                .copy(
                                                                                                                                        isLoading =
                                                                                                                                                false,
                                                                                                                                        error =
                                                                                                                                                customersResult
                                                                                                                                                        .message
                                                                                                                                )
                                                                                                        }
                                                                                                        else -> {
                                                                                                                // Keep loading state
                                                                                                        }
                                                                                                }
                                                                                        }
                                                                        }
                                                                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                                                                                _uiState.value =
                                                                                        _uiState.value
                                                                                                .copy(
                                                                                                        isLoading =
                                                                                                                false,
                                                                                                        error =
                                                                                                                ordersResult
                                                                                                                        .message
                                                                                                )
                                                                        }
                                                                        else -> {
                                                                                // Keep loading
                                                                                // state
                                                                        }
                                                                }
                                                        }
                                                }
                                                is com.example.decalxeandroid.domain.model.Result.Error -> {
                                                        _uiState.value =
                                                                _uiState.value.copy(
                                                                        isLoading = false,
                                                                        error =
                                                                                employeesResult
                                                                                        .message
                                                                )
                                                }
                                                else -> {
                                                        // Keep loading state
                                                }
                                        }
                                }
                        } catch (e: Exception) {
                                _uiState.value =
                                        _uiState.value.copy(
                                                isLoading = false,
                                                error = "Lỗi tải dữ liệu: ${e.message}"
                                        )
                        }
                }
        }

        fun clearError() {
                _uiState.value = _uiState.value.copy(error = null)
        }
}

data class DashboardUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val totalOrders: Int = 0,
        val totalCustomers: Int = 0,
        val totalServices: Int = 0,
        val totalRevenue: Double = 0.0,
        val recentOrders: List<Order> = emptyList(),
        val decalServices: List<DecalService> = emptyList()
)
