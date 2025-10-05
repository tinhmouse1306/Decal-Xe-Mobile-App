package com.example.decalxeandroid.presentation.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.OrderDetail
import com.example.decalxeandroid.domain.model.OrderStageHistory
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.OrderDetailRepository
import com.example.decalxeandroid.domain.repository.OrderRepository
import com.example.decalxeandroid.domain.repository.OrderStageHistoryRepository
import com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager
import com.example.decalxeandroid.domain.usecase.order.OrderStatusValidator
import com.example.decalxeandroid.domain.usecase.order.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OrderDetailUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val order: Order? = null,
        val orderDetails: List<OrderDetail> = emptyList(),
        val stageHistory: List<OrderStageHistory> = emptyList(),
        val currentStage: OrderStageHistory? = null,
        val isRefreshing: Boolean = false
)

class OrderDetailViewModel(
        private val orderRepository: OrderRepository,
        private val orderDetailRepository: OrderDetailRepository,
        private val orderStageHistoryRepository: OrderStageHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderDetailUiState())
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    private val statusValidator = OrderStatusValidator()

    fun loadOrderDetail(orderId: String) {
        viewModelScope.launch {
            Log.d("OrderDetailViewModel", "=== LOADING ORDER DETAIL for orderId: $orderId ===")
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            orderRepository.getOrderById(orderId).collect { orderResult ->
                when (orderResult) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(order = orderResult.data)
                        loadOrderDetailsAndStageHistory(orderId)
                    }
                    is Result.Error -> {
                        _uiState.value =
                                _uiState.value.copy(isLoading = false, error = orderResult.message)
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }

    private suspend fun loadOrderDetailsAndStageHistory(orderId: String) {
        Log.d(
                "OrderDetailViewModel",
                "=== LOADING ORDER DETAILS AND STAGE HISTORY for orderId: $orderId ==="
        )

        // Load order details
        try {
            orderDetailRepository.getOrderDetailsByOrderId(orderId).collect { result ->
                Log.d(
                        "OrderDetailViewModel",
                        "OrderDetails result for orderId $orderId: ${result.javaClass.simpleName}"
                )
                when (result) {
                    is Result.Success -> {
                        Log.d("OrderDetailViewModel", "Loaded ${result.data.size} order details")
                        // Log chi tiết từng order detail
                        result.data.forEachIndexed { index, detail ->
                            Log.d(
                                    "OrderDetailViewModel",
                                    "OrderDetail $index: ${detail.serviceName} - ${detail.quantity} x ${detail.unitPrice}"
                            )
                        }
                        _uiState.value = _uiState.value.copy(orderDetails = result.data)
                        return@collect // Dừng collect sau khi có kết quả thành công hoặc lỗi
                    }
                    is Result.Error -> {
                        Log.e(
                                "OrderDetailViewModel",
                                "Failed to load order details: ${result.message}"
                        )
                        _uiState.value = _uiState.value.copy(orderDetails = emptyList())
                        return@collect // Dừng collect sau khi có kết quả thành công hoặc lỗi
                    }
                    else -> {
                        // Loading state - tiếp tục chờ
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("OrderDetailViewModel", "Error loading order details", e)
            _uiState.value = _uiState.value.copy(orderDetails = emptyList())
        }

        // Load stage history
        try {
            orderStageHistoryRepository.getOrderStageHistoriesByOrderId(orderId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val sortedHistory = result.data.sortedByDescending { it.startDate }
                        val currentStage = sortedHistory.firstOrNull()

                        // Fallback: Nếu currentStage có assignedEmployeeId null, lấy từ order
                        val fixedCurrentStage =
                                if (currentStage?.assignedEmployeeId == null &&
                                                _uiState.value.order != null
                                ) {
                                    currentStage?.copy(
                                            assignedEmployeeId =
                                                    _uiState.value.order!!.assignedEmployeeId,
                                            assignedEmployeeFullName =
                                                    _uiState.value.order!!.assignedEmployeeName
                                    )
                                } else {
                                    currentStage
                                }

                        // Fallback: Sửa tất cả stage history có assignedEmployeeId null
                        val fixedStageHistory =
                                sortedHistory.map { stageHistory ->
                                    if (stageHistory.assignedEmployeeId == null &&
                                                    _uiState.value.order != null
                                    ) {
                                        stageHistory.copy(
                                                assignedEmployeeId =
                                                        _uiState.value.order!!.assignedEmployeeId,
                                                assignedEmployeeFullName =
                                                        _uiState.value.order!!.assignedEmployeeName
                                        )
                                    } else {
                                        stageHistory
                                    }
                                }

                        _uiState.value =
                                _uiState.value.copy(
                                        stageHistory = fixedStageHistory,
                                        currentStage = fixedCurrentStage
                                )
                        return@collect // Dừng collect sau khi có kết quả thành công hoặc lỗi
                    }
                    is Result.Error -> {
                        Log.e(
                                "OrderDetailViewModel",
                                "Failed to load stage history: ${result.message}"
                        )
                        _uiState.value =
                                _uiState.value.copy(stageHistory = emptyList(), currentStage = null)
                        return@collect // Dừng collect sau khi có kết quả thành công hoặc lỗi
                    }
                    else -> {
                        // Loading state - tiếp tục chờ
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("OrderDetailViewModel", "Error loading stage history", e)
            _uiState.value = _uiState.value.copy(stageHistory = emptyList(), currentStage = null)
        }

        // Set loading = false sau khi cả hai task hoàn thành
        _uiState.value = _uiState.value.copy(isLoading = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun updateOrderStatus(orderId: String, status: String, stage: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            Log.d("OrderDetailViewModel", "=== UPDATE ORDER STATUS ===")
            Log.d("OrderDetailViewModel", "orderId: $orderId")
            Log.d("OrderDetailViewModel", "status: '$status'")
            Log.d("OrderDetailViewModel", "stage: '$stage'")

            // Bước 0: Validation - Kiểm tra quyền và workflow
            val currentOrder = _uiState.value.order
            val currentStageHistory = _uiState.value.stageHistory
            val currentUser = GlobalAuthManager.currentUser.value

            if (currentOrder == null) {
                _uiState.value =
                        _uiState.value.copy(
                                isLoading = false,
                                error = "Không tìm thấy thông tin đơn hàng"
                        )
                return@launch
            }

            if (currentUser?.role == null) {
                _uiState.value =
                        _uiState.value.copy(
                                isLoading = false,
                                error = "Không tìm thấy thông tin người dùng"
                        )
                return@launch
            }

            // Kiểm tra quyền chuyển trạng thái
            val validationResult =
                    statusValidator.canChangeStatus(
                            currentOrder = currentOrder,
                            newStatus = status,
                            userRole = currentUser.role,
                            currentStageHistory = currentStageHistory
                    )

            if (!validationResult.isValid) {
                val errorMessage =
                        when (validationResult) {
                            is ValidationResult.Error -> validationResult.message
                            else -> "Validation failed"
                        }
                Log.w("OrderDetailViewModel", "Status change validation failed: $errorMessage")
                _uiState.value = _uiState.value.copy(isLoading = false, error = errorMessage)
                return@launch
            }

            Log.d("OrderDetailViewModel", "Status change validation passed")

            // Bước 1: Update OrderStatus trong API Order
            orderRepository.updateOrderStatus(orderId, status, stage).collect { result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("OrderDetailViewModel", "OrderStatus updated successfully: $status")

                        // Bước 2: Map OrderStatus sang Stage DB theo logic isCustomDecal
                        val currentOrder = result.data
                        Log.d("OrderDetailViewModel", "=== ORDER AFTER UPDATE ===")
                        Log.d("OrderDetailViewModel", "orderStatus: '${currentOrder.orderStatus}'")
                        Log.d(
                                "OrderDetailViewModel",
                                "currentStage: '${currentOrder.currentStage}'"
                        )
                        Log.d(
                                "OrderDetailViewModel",
                                "isCustomDecal: ${currentOrder.isCustomDecal}"
                        )

                        val mappedStage = mapOrderStatusToStage(status, currentOrder.isCustomDecal)
                        Log.d(
                                "OrderDetailViewModel",
                                "Mapped status '$status' to stage '$mappedStage' for isCustomDecal=${currentOrder.isCustomDecal}"
                        )
                        Log.d(
                                "OrderDetailViewModel",
                                "Current order assignedEmployeeId: '${currentOrder.assignedEmployeeId}', assignedEmployeeName: '${currentOrder.assignedEmployeeName}'"
                        )

                        // Bước 3: Tạo OrderStageHistory mới
                        val newStageHistory =
                                OrderStageHistory(
                                        stageHistoryId = "", // Sẽ được tạo bởi API
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
                        Log.d(
                                "OrderDetailViewModel",
                                "Created OrderStageHistory with assignedEmployeeId: '${newStageHistory.assignedEmployeeId}', assignedEmployeeFullName: '${newStageHistory.assignedEmployeeFullName}'"
                        )

                        // Bước 4: Lưu OrderStageHistory
                        orderStageHistoryRepository.createOrderStageHistory(newStageHistory)
                                .collect { stageResult ->
                                    when (stageResult) {
                                        is Result.Success -> {
                                            Log.d(
                                                    "OrderDetailViewModel",
                                                    "OrderStageHistory created successfully"
                                            )
                                            _uiState.value =
                                                    _uiState.value.copy(
                                                            isLoading = false,
                                                            order = result.data
                                                    )
                                            // Refresh order details and stage history after status
                                            // update
                                            loadOrderDetail(orderId)
                                        }
                                        is Result.Error -> {
                                            Log.e(
                                                    "OrderDetailViewModel",
                                                    "Failed to create OrderStageHistory: ${stageResult.message}"
                                            )
                                            _uiState.value =
                                                    _uiState.value.copy(
                                                            isLoading = false,
                                                            order = result.data
                                                    )
                                            // Vẫn refresh order details dù có lỗi tạo stage history
                                            loadOrderDetail(orderId)
                                        }
                                        else -> {
                                            _uiState.value = _uiState.value.copy(isLoading = true)
                                        }
                                    }
                                }
                    }
                    is Result.Error -> {
                        Log.e(
                                "OrderDetailViewModel",
                                "Failed to update OrderStatus: ${result.message}"
                        )
                        _uiState.value =
                                _uiState.value.copy(isLoading = false, error = result.message)
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
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
            // Fallback cho các giá trị cũ
            "new" -> "Pending"
            "survey" -> "Survey"
            "design" -> if (isCustomDecal) "Designing" else "ProductionAndInstallation"
            "production" -> "ProductionAndInstallation"
            "payment" -> "Payment"
            "delivery" -> "AcceptanceAndDelivery"
            else -> status // Fallback
        }
    }

    private fun getStageDescription(stage: String): String {
        return when (stage) {
            "Pending" -> "Đơn hàng mới được tạo"
            "Survey" -> "Đang khảo sát và đánh giá yêu cầu"
            "Designing" -> "Đang thiết kế decal theo yêu cầu"
            "ProductionAndInstallation" -> "Đang sản xuất và lắp đặt decal"
            "Payment" -> "Đang xử lý thanh toán"
            "AcceptanceAndDelivery" -> "Nghiệm thu và giao hàng hoàn tất"
            else -> "Giai đoạn: $stage"
        }
    }

    fun addOrderDetail(orderDetail: OrderDetail) {
        viewModelScope.launch {
            orderDetailRepository.createOrderDetail(orderDetail).collect { result ->
                when (result) {
                    is Result.Success -> {
                        // Refresh order details
                        orderDetail.orderId.let { orderId ->
                            orderDetailRepository.getOrderDetailsByOrderId(orderId).collect {
                                    refreshResult ->
                                when (refreshResult) {
                                    is Result.Success -> {
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        orderDetails = refreshResult.data
                                                )
                                    }
                                    else -> {
                                        // Handle error
                                    }
                                }
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(error = result.message)
                    }
                    else -> {
                        // Loading state
                    }
                }
            }
        }
    }

    fun updateOrderDetail(orderDetail: OrderDetail) {
        viewModelScope.launch {
            orderDetailRepository.updateOrderDetail(orderDetail.orderDetailId, orderDetail)
                    .collect { result ->
                        when (result) {
                            is Result.Success -> {
                                // Refresh order details
                                orderDetail.orderId.let { orderId ->
                                    orderDetailRepository.getOrderDetailsByOrderId(orderId)
                                            .collect { refreshResult ->
                                                when (refreshResult) {
                                                    is Result.Success -> {
                                                        _uiState.value =
                                                                _uiState.value.copy(
                                                                        orderDetails =
                                                                                refreshResult.data
                                                                )
                                                    }
                                                    else -> {
                                                        // Handle error
                                                    }
                                                }
                                            }
                                }
                            }
                            is Result.Error -> {
                                _uiState.value = _uiState.value.copy(error = result.message)
                            }
                            else -> {
                                // Loading state
                            }
                        }
                    }
        }
    }

    fun deleteOrderDetail(orderDetail: OrderDetail) {
        viewModelScope.launch {
            orderDetailRepository.deleteOrderDetail(orderDetail.orderDetailId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        // Refresh order details
                        orderDetail.orderId.let { orderId ->
                            orderDetailRepository.getOrderDetailsByOrderId(orderId).collect {
                                    refreshResult ->
                                when (refreshResult) {
                                    is Result.Success -> {
                                        _uiState.value =
                                                _uiState.value.copy(
                                                        orderDetails = refreshResult.data
                                                )
                                    }
                                    else -> {
                                        // Handle error
                                    }
                                }
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(error = result.message)
                    }
                    else -> {
                        // Loading state
                    }
                }
            }
        }
    }

    fun refreshOrderDetail(orderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
            loadOrderDetail(orderId)
            _uiState.value = _uiState.value.copy(isRefreshing = false)
        }
    }

    /** Lấy danh sách trạng thái có thể chuyển tiếp từ trạng thái hiện tại */
    fun getAvailableNextStatuses(): List<String> {
        val currentOrder = _uiState.value.order
        val currentStageHistory = _uiState.value.stageHistory
        val currentUser = GlobalAuthManager.currentUser.value

        if (currentOrder == null || currentUser?.role == null) {
            return emptyList()
        }

        return statusValidator.getAvailableNextStatuses(
                currentOrder = currentOrder,
                userRole = currentUser.role,
                currentStageHistory = currentStageHistory
        )
    }

    /** Kiểm tra xem có thể chuyển sang trạng thái cụ thể không */
    fun canChangeToStatus(status: String): Boolean {
        val currentOrder = _uiState.value.order
        val currentStageHistory = _uiState.value.stageHistory
        val currentUser = GlobalAuthManager.currentUser.value

        if (currentOrder == null || currentUser?.role == null) {
            return false
        }

        val validationResult =
                statusValidator.canChangeStatus(
                        currentOrder = currentOrder,
                        newStatus = status,
                        userRole = currentUser.role,
                        currentStageHistory = currentStageHistory
                )

        return validationResult.isValid
    }
}
