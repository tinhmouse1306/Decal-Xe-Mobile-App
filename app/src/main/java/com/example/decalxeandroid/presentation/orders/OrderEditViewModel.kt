package com.example.decalxeandroid.presentation.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class OrderEditViewModel(
    private val orderId: String,
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<OrderEditUiState>(OrderEditUiState.LoadingOrder)
    val uiState: StateFlow<OrderEditUiState> = _uiState.asStateFlow()
    
    companion object {
        private const val TAG = "OrderEditViewModel"
    }
    
    init {
        loadOrder()
    }
    
    private fun loadOrder() {
        viewModelScope.launch {
            _uiState.value = OrderEditUiState.LoadingOrder
            Log.d(TAG, "Loading order for edit: $orderId")
            
            try {
                orderRepository.getOrderById(orderId).collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            val order = result.data
                            Log.d(TAG, "Successfully loaded order: ${order.orderNumber}")
                            
                            val formData = OrderEditFormData(
                                totalAmount = order.totalAmount.toString(),
                                assignedEmployeeId = order.assignedEmployeeId ?: "",
                                vehicleId = order.vehicleId ?: "",
                                expectedArrivalTime = order.expectedArrivalTime ?: "",
                                priority = order.priority ?: "Normal",
                                isCustomDecal = order.isCustomDecal,
                                description = order.description ?: "",
                                
                                // Initial validation
                                totalAmountError = validateTotalAmount(order.totalAmount.toString()),
                                assignedEmployeeIdError = validateAssignedEmployeeId(order.assignedEmployeeId ?: ""),
                                vehicleIdError = validateVehicleId(order.vehicleId ?: ""),
                                expectedArrivalTimeError = validateExpectedArrivalTime(order.expectedArrivalTime ?: ""),
                                priorityError = validatePriority(order.priority ?: "Normal"),
                                descriptionError = validateDescription(order.description ?: "")
                            )
                            
                            _uiState.value = OrderEditUiState.Editing(formData)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            Log.e(TAG, "Failed to load order: ${result.message}")
                            _uiState.value = OrderEditUiState.Error(
                                "Không thể tải thông tin đơn hàng: ${result.message}",
                                OrderEditFormData()
                            )
                        }
                        else -> {
                            Log.e(TAG, "Unknown result type")
                            _uiState.value = OrderEditUiState.Error(
                                "Lỗi không xác định khi tải thông tin đơn hàng",
                                OrderEditFormData()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading order", e)
                _uiState.value = OrderEditUiState.Error(
                    "Lỗi kết nối: ${e.message}",
                    OrderEditFormData()
                )
            }
        }
    }
    
    fun updateTotalAmount(totalAmount: String) {
        val currentState = uiState.value as? OrderEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            totalAmount = totalAmount,
            totalAmountError = validateTotalAmount(totalAmount)
        )
        _uiState.value = OrderEditUiState.Editing(updatedFormData)
    }
    
    fun updateAssignedEmployee(assignedEmployeeId: String) {
        val currentState = uiState.value as? OrderEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            assignedEmployeeId = assignedEmployeeId,
            assignedEmployeeIdError = validateAssignedEmployeeId(assignedEmployeeId)
        )
        _uiState.value = OrderEditUiState.Editing(updatedFormData)
    }
    
    fun updateVehicleId(vehicleId: String) {
        val currentState = uiState.value as? OrderEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            vehicleId = vehicleId,
            vehicleIdError = validateVehicleId(vehicleId)
        )
        _uiState.value = OrderEditUiState.Editing(updatedFormData)
    }
    
    fun updateExpectedArrivalTime(expectedArrivalTime: String) {
        val currentState = uiState.value as? OrderEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            expectedArrivalTime = expectedArrivalTime,
            expectedArrivalTimeError = validateExpectedArrivalTime(expectedArrivalTime)
        )
        _uiState.value = OrderEditUiState.Editing(updatedFormData)
    }
    
    fun updatePriority(priority: String) {
        val currentState = uiState.value as? OrderEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            priority = priority,
            priorityError = validatePriority(priority)
        )
        _uiState.value = OrderEditUiState.Editing(updatedFormData)
    }
    
    fun updateIsCustomDecal(isCustomDecal: Boolean) {
        val currentState = uiState.value as? OrderEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            isCustomDecal = isCustomDecal
        )
        _uiState.value = OrderEditUiState.Editing(updatedFormData)
    }
    
    fun updateDescription(description: String) {
        val currentState = uiState.value as? OrderEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            description = description,
            descriptionError = validateDescription(description)
        )
        _uiState.value = OrderEditUiState.Editing(updatedFormData)
    }
    
    fun updateOrder() {
        val currentState = uiState.value as? OrderEditUiState.Editing ?: return
        val formData = currentState.formData
        
        if (!formData.isValid) {
            Log.w(TAG, "Form is not valid, cannot update order")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = OrderEditUiState.Loading(formData)
            Log.d(TAG, "Updating order: $orderId")
            
            try {
                // Create Order object with updated data
                val updatedOrder = Order(
                    orderId = orderId,
                    orderNumber = "", // Will be preserved by backend
                    customerId = "", // Will be preserved by backend
                    customerFullName = "", // Will be preserved by backend
                    vehicleId = formData.vehicleId.trim().takeIf { it.isNotEmpty() },
                    vehicleLicensePlate = null, // Will be populated by backend
                    assignedEmployeeId = formData.assignedEmployeeId.trim().takeIf { it.isNotEmpty() },
                    assignedEmployeeName = null, // Will be populated by backend
                    orderStatus = "", // Will be preserved by backend
                    currentStage = "", // Will be preserved by backend
                    totalAmount = formData.totalAmount.toDoubleOrNull() ?: 0.0,
                    depositAmount = 0.0, // Will be preserved by backend
                    remainingAmount = 0.0, // Will be calculated by backend
                    orderDate = "", // Will be preserved by backend
                    expectedCompletionDate = null, // Will be preserved by backend
                    actualCompletionDate = null, // Will be preserved by backend
                    notes = null, // Will be preserved by backend
                    isActive = true, // Will be preserved by backend
                    createdAt = "", // Will be preserved by backend
                    updatedAt = null, // Will be set by backend
                    
                    // UpdateOrderDto fields
                    chassisNumber = null,
                    vehicleModelName = null,
                    vehicleBrandName = null,
                    expectedArrivalTime = formData.expectedArrivalTime.trim().takeIf { it.isNotEmpty() },
                    priority = formData.priority.trim().takeIf { it.isNotEmpty() },
                    isCustomDecal = formData.isCustomDecal,
                    storeId = null,
                    description = formData.description.trim().takeIf { it.isNotEmpty() },
                    customerPhoneNumber = null,
                    customerEmail = null,
                    customerAddress = null,
                    accountId = null,
                    accountUsername = null,
                    accountCreated = null
                )
                
                orderRepository.updateOrder(orderId, updatedOrder).collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            Log.d(TAG, "Successfully updated order: ${result.data.orderNumber}")
                            _uiState.value = OrderEditUiState.Success(result.data)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            Log.e(TAG, "Failed to update order: ${result.message}")
                            _uiState.value = OrderEditUiState.Error(
                                "Không thể cập nhật đơn hàng: ${result.message}",
                                formData
                            )
                        }
                        else -> {
                            Log.e(TAG, "Unknown result type")
                            _uiState.value = OrderEditUiState.Error(
                                "Lỗi không xác định khi cập nhật đơn hàng",
                                formData
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception updating order", e)
                _uiState.value = OrderEditUiState.Error(
                    "Lỗi kết nối: ${e.message}",
                    formData
                )
            }
        }
    }
    
    fun resetError() {
        val currentState = uiState.value as? OrderEditUiState.Error ?: return
        _uiState.value = OrderEditUiState.Editing(currentState.formData)
    }
    
    // Validation functions
    private fun validateTotalAmount(totalAmount: String): String? {
        return when {
            totalAmount.isBlank() -> "Tổng tiền không được để trống"
            totalAmount.toDoubleOrNull() == null -> "Tổng tiền phải là số hợp lệ"
            totalAmount.toDoubleOrNull()!! < 0 -> "Tổng tiền phải lớn hơn hoặc bằng 0"
            else -> null
        }
    }
    
    private fun validateAssignedEmployeeId(assignedEmployeeId: String): String? {
        if (assignedEmployeeId.isBlank()) return null // Optional field
        return if (assignedEmployeeId.length < 3) {
            "ID nhân viên phải có ít nhất 3 ký tự"
        } else null
    }
    
    private fun validateVehicleId(vehicleId: String): String? {
        if (vehicleId.isBlank()) return null // Optional field
        return if (vehicleId.length < 3) {
            "ID xe phải có ít nhất 3 ký tự"
        } else null
    }
    
    private fun validateExpectedArrivalTime(expectedArrivalTime: String): String? {
        if (expectedArrivalTime.isBlank()) return null // Optional field
        return if (expectedArrivalTime.length < 5) {
            "Thời gian dự kiến phải có ít nhất 5 ký tự"
        } else null
    }
    
    private fun validatePriority(priority: String): String? {
        if (priority.isBlank()) return null // Optional field
        val validPriorities = listOf("Low", "Normal", "High", "Urgent")
        return if (priority !in validPriorities) {
            "Độ ưu tiên phải là: ${validPriorities.joinToString(", ")}"
        } else null
    }
    
    private fun validateDescription(description: String): String? {
        if (description.isBlank()) return null // Optional field
        return if (description.length < 10) {
            "Mô tả phải có ít nhất 10 ký tự"
        } else null
    }
}

data class OrderEditFormData(
    val totalAmount: String = "",
    val assignedEmployeeId: String = "",
    val vehicleId: String = "",
    val expectedArrivalTime: String = "",
    val priority: String = "Normal",
    val isCustomDecal: Boolean = false,
    val description: String = "",
    
    // Error states
    val totalAmountError: String? = null,
    val assignedEmployeeIdError: String? = null,
    val vehicleIdError: String? = null,
    val expectedArrivalTimeError: String? = null,
    val priorityError: String? = null,
    val descriptionError: String? = null
) {
    val isValid: Boolean
        get() = totalAmountError == null &&
                assignedEmployeeIdError == null &&
                vehicleIdError == null &&
                expectedArrivalTimeError == null &&
                priorityError == null &&
                descriptionError == null &&
                totalAmount.isNotBlank()
}

sealed class OrderEditUiState {
    object LoadingOrder : OrderEditUiState()
    data class Editing(val formData: OrderEditFormData) : OrderEditUiState()
    data class Loading(val formData: OrderEditFormData) : OrderEditUiState()
    data class Error(val message: String, val formData: OrderEditFormData) : OrderEditUiState()
    data class Success(val order: Order) : OrderEditUiState()
}
