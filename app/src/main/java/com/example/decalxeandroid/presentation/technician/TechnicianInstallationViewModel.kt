package com.example.decalxeandroid.presentation.technician

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.OrderStageHistory
import com.example.decalxeandroid.domain.repository.EmployeeRepository
import com.example.decalxeandroid.domain.repository.OrderRepository
import com.example.decalxeandroid.domain.repository.OrderStageHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TechnicianInstallationViewModel : ViewModel() {
    // Use AppContainer to get repositories
    private val orderRepository: OrderRepository =
            com.example.decalxeandroid.di.AppContainer.orderRepository
    private val employeeRepository: EmployeeRepository =
            com.example.decalxeandroid.di.AppContainer.employeeRepository
    private val orderStageHistoryRepository: OrderStageHistoryRepository =
            com.example.decalxeandroid.di.AppContainer.orderStageHistoryRepository

    private val _uiState = MutableStateFlow(TechnicianInstallationUiState())
    val uiState: StateFlow<TechnicianInstallationUiState> = _uiState.asStateFlow()

    fun loadInstallationOrders() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Get current user to find employee info
                val authManager = com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager
                val currentUser = authManager.currentUser.value

                if (currentUser?.accountId == null) {
                    _uiState.value =
                            _uiState.value.copy(
                                    isLoading = false,
                                    error = "Không tìm thấy thông tin người dùng"
                            )
                    return@launch
                }

                // Get employee info to find employeeId
                employeeRepository.getEmployees(1, 100).collect { employeeResult ->
                    when (employeeResult) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            val employee =
                                    employeeResult.data.find {
                                        it.accountId == currentUser.accountId
                                    }

                            if (employee?.employeeId == null) {
                                _uiState.value =
                                        _uiState.value.copy(
                                                isLoading = false,
                                                error = "Không tìm thấy thông tin nhân viên"
                                        )
                                return@collect
                            }

                            // Load orders and filter by assignedEmployeeID and currentStage =
                            // "Survey"
                            orderRepository.getOrders().collect { orderResult ->
                                when (orderResult) {
                                    is com.example.decalxeandroid.domain.model.Result.Success -> {
                                        // Filter orders by assignedEmployeeID and currentStage =
                                        // "Survey"
                                        val installationOrders =
                                                orderResult.data.filter { order ->
                                                    order.assignedEmployeeId ==
                                                            employee.employeeId &&
                                                            order.currentStage == "Survey"
                                                }

                                        // Sort orders by priority and creation time
                                        val sortedOrders =
                                                installationOrders.sortedWith(
                                                        compareByDescending<Order> { order ->
                                                            // Priority: High = 3, Medium = 2, Low =
                                                            // 1, null = 0
                                                            when (order.priority?.lowercase()) {
                                                                "high" -> 3
                                                                "medium" -> 2
                                                                "low" -> 1
                                                                else -> 0
                                                            }
                                                        }
                                                                .thenByDescending { order ->
                                                                    // Sort by creation time (newest
                                                                    // first)
                                                                    try {
                                                                        val format =
                                                                                java.text
                                                                                        .SimpleDateFormat(
                                                                                                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                                                                                                java.util
                                                                                                        .Locale
                                                                                                        .getDefault()
                                                                                        )
                                                                        format.parse(
                                                                                        order.createdAt
                                                                                )
                                                                                ?.time
                                                                                ?: 0L
                                                                    } catch (e: Exception) {
                                                                        try {
                                                                            val format2 =
                                                                                    java.text
                                                                                            .SimpleDateFormat(
                                                                                                    "yyyy-MM-dd'T'HH:mm:ss'Z'",
                                                                                                    java.util
                                                                                                            .Locale
                                                                                                            .getDefault()
                                                                                            )
                                                                            format2.parse(
                                                                                            order.createdAt
                                                                                    )
                                                                                    ?.time
                                                                                    ?: 0L
                                                                        } catch (e2: Exception) {
                                                                            0L
                                                                        }
                                                                    }
                                                                }
                                                )

                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        orders = sortedOrders,
                                                        employeeName =
                                                                "${employee.firstName} ${employee.lastName}"
                                                )
                                    }
                                    is com.example.decalxeandroid.domain.model.Result.Error -> {
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        isLoading = false,
                                                        error = orderResult.message
                                                )
                                    }
                                    is com.example.decalxeandroid.domain.model.Result.Loading -> {
                                        // Handle loading state if needed
                                    }
                                }
                            }
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value =
                                    _uiState.value.copy(
                                            isLoading = false,
                                            error = employeeResult.message
                                    )
                        }
                        is com.example.decalxeandroid.domain.model.Result.Loading -> {
                            // Handle loading state if needed
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value =
                        _uiState.value.copy(
                                isLoading = false,
                                error = e.message
                                                ?: "Có lỗi xảy ra khi tải danh sách đơn hàng lắp đặt"
                        )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun updateOrderStatus(orderId: String, status: String, stage: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Find current order
                val currentOrder = _uiState.value.orders.find { it.orderId == orderId }
                if (currentOrder == null) {
                    _uiState.value =
                            _uiState.value.copy(
                                    isLoading = false,
                                    error = "Không tìm thấy đơn hàng"
                            )
                    return@launch
                }

                // Map status to stage
                val mappedStage = mapOrderStatusToStage(status, currentOrder.isCustomDecal)

                // Update order status
                orderRepository.updateOrderStatus(orderId, status, mappedStage).collect {
                        updateResult ->
                    when (updateResult) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            val updatedOrder = updateResult.data
                            android.util.Log.d(
                                    "TechnicianInstallationViewModel",
                                    "OrderStatus updated successfully: $status"
                            )

                            // Create new OrderStageHistory
                            val newStageHistory =
                                    OrderStageHistory(
                                            stageHistoryId = "", // Will be created by API
                                            orderId = orderId,
                                            stageName = mappedStage,
                                            stageDescription = getStageDescription(mappedStage),
                                            startDate = java.time.LocalDateTime.now().toString(),
                                            endDate = null,
                                            assignedEmployeeId = currentOrder.assignedEmployeeId,
                                            assignedEmployeeFullName =
                                                    currentOrder.assignedEmployeeName,
                                            notes = "Cập nhật trạng thái: $status"
                                    )

                            // Create stage history
                            orderStageHistoryRepository.createOrderStageHistory(newStageHistory)
                                    .collect { stageHistoryResult ->
                                        when (stageHistoryResult) {
                                            is com.example.decalxeandroid.domain.model.Result.Success -> {
                                                android.util.Log.d(
                                                        "TechnicianInstallationViewModel",
                                                        "Stage history created successfully"
                                                )
                                            }
                                            is com.example.decalxeandroid.domain.model.Result.Error -> {
                                                android.util.Log.e(
                                                        "TechnicianInstallationViewModel",
                                                        "Failed to create stage history: ${stageHistoryResult.message}"
                                                )
                                            }
                                            is com.example.decalxeandroid.domain.model.Result.Loading -> {
                                                // Handle loading state if needed
                                            }
                                        }
                                    }

                            // Reload orders to update UI
                            loadInstallationOrders()
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value =
                                    _uiState.value.copy(
                                            isLoading = false,
                                            error = updateResult.message
                                    )
                        }
                        is com.example.decalxeandroid.domain.model.Result.Loading -> {
                            // Handle loading state if needed
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value =
                        _uiState.value.copy(
                                isLoading = false,
                                error = e.message
                                                ?: "Có lỗi xảy ra khi cập nhật trạng thái đơn hàng"
                        )
            }
        }
    }

    private fun mapOrderStatusToStage(status: String, isCustomDecal: Boolean): String {
        return when (status) {
            "Đơn hàng mới" -> "Pending"
            "Khảo sát" -> "Survey"
            "Thiết kế" -> "Designing"
            "Chốt và thi công" -> "ProductionAndInstallation"
            "Thanh toán" -> "Payment"
            "Nghiệm thu và nhận hàng" -> "AcceptanceAndDelivery"
            // Fallback for old values
            "new" -> "Pending"
            "survey" -> "Survey"
            "design" -> if (isCustomDecal) "Designing" else "ProductionAndInstallation"
            "production" -> "ProductionAndInstallation"
            "payment" -> "Payment"
            "delivery" -> "AcceptanceAndDelivery"
            else -> status // Fallback
        }
    }

    private fun getStageDescription(stageName: String): String {
        return when (stageName) {
            "Pending" -> "Đơn hàng mới được tạo"
            "Survey" -> "Đang khảo sát địa điểm và yêu cầu"
            "Designing" -> "Đang thiết kế decal theo yêu cầu"
            "ProductionAndInstallation" -> "Đang sản xuất và lắp đặt"
            "Payment" -> "Chờ thanh toán"
            "AcceptanceAndDelivery" -> "Nghiệm thu và giao hàng"
            else -> "Giai đoạn: $stageName"
        }
    }
}

data class TechnicianInstallationUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val orders: List<Order> = emptyList(),
        val employeeName: String? = null
)
