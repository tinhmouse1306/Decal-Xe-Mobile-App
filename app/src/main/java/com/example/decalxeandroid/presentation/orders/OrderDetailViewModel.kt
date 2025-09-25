package com.example.decalxeandroid.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.OrderDetail
import com.example.decalxeandroid.domain.model.OrderStageHistory
import com.example.decalxeandroid.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class OrderDetailViewModel(
    private val orderId: String,
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<OrderDetailUiState>(OrderDetailUiState.Loading)
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()
    
    init {
        loadOrder()
    }
    
    fun loadOrder() {
        viewModelScope.launch {
            println("OrderDetailViewModel: Starting to load order with ID: '$orderId'")
            println("OrderDetailViewModel: OrderID length: ${orderId.length}")
            println("OrderDetailViewModel: OrderID isEmpty: ${orderId.isEmpty()}")
            println("OrderDetailViewModel: OrderID isBlank: ${orderId.isBlank()}")
            
            if (orderId.isEmpty() || orderId.isBlank()) {
                println("OrderDetailViewModel: Invalid orderId - empty or blank")
                _uiState.value = OrderDetailUiState.Error("ID đơn hàng không hợp lệ")
                return@launch
            }
            
            _uiState.value = OrderDetailUiState.Loading
            
            try {
                // Load order details
                println("OrderDetailViewModel: Calling orderRepository.getOrderById($orderId)")
                val orderResult = orderRepository.getOrderById(orderId)
                orderResult.collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            println("OrderDetailViewModel: Successfully loaded order: ${result.data.orderId}")
                            val order = result.data
                            
                            // Load order details (services)
                            val orderDetailsResult = orderRepository.getOrderDetails(orderId)
                            orderDetailsResult.collect { detailsResult ->
                                when (detailsResult) {
                                    is com.example.decalxeandroid.domain.model.Result.Success -> {
                                        val orderDetails = detailsResult.data
                                        
                                        // Load stage history
                                        val stageHistoryResult = orderRepository.getOrderStageHistory(orderId)
                                        stageHistoryResult.collect { historyResult ->
                                            when (historyResult) {
                                                is com.example.decalxeandroid.domain.model.Result.Success -> {
                                                    val stageHistory = historyResult.data
                                                    _uiState.value = OrderDetailUiState.Success(
                                                        order = order,
                                                        orderDetails = orderDetails,
                                                        stageHistory = stageHistory
                                                    )
                                                }
                                                is com.example.decalxeandroid.domain.model.Result.Error -> {
                                                    _uiState.value = OrderDetailUiState.Error(
                                                        "Không thể tải lịch sử trạng thái: ${historyResult.message}"
                                                    )
                                                }
                                                else -> {
                                                    _uiState.value = OrderDetailUiState.Error(
                                                        "Kết quả lịch sử trạng thái không xác định"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    is com.example.decalxeandroid.domain.model.Result.Error -> {
                                        _uiState.value = OrderDetailUiState.Error(
                                            "Không thể tải chi tiết đơn hàng: ${detailsResult.message}"
                                        )
                                    }
                                    else -> {
                                        _uiState.value = OrderDetailUiState.Error(
                                            "Kết quả chi tiết đơn hàng không xác định"
                                        )
                                    }
                                }
                            }
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value = OrderDetailUiState.Error(
                                "Không thể tải thông tin đơn hàng: ${result.message}"
                            )
                        }
                        else -> {
                            _uiState.value = OrderDetailUiState.Error(
                                "Kết quả đơn hàng không xác định"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = OrderDetailUiState.Error(
                    "Lỗi không xác định: ${e.message}"
                )
            }
        }
    }
    
    fun editOrder(onNavigateToEdit: (String) -> Unit) {
        android.util.Log.d("OrderDetailViewModel", "Navigating to edit order: $orderId")
        onNavigateToEdit(orderId)
    }
    
    fun updateOrderStatus() {
        // TODO: Show status update dialog
    }
}

sealed class OrderDetailUiState {
    object Loading : OrderDetailUiState()
    data class Success(
        val order: Order,
        val orderDetails: List<OrderDetail>,
        val stageHistory: List<OrderStageHistory>
    ) : OrderDetailUiState()
    data class Error(val message: String) : OrderDetailUiState()
}
