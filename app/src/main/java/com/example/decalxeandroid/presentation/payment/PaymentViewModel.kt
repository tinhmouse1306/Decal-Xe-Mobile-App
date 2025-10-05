package com.example.decalxeandroid.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.OrderRepository
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PaymentUiState(
        val isLoading: Boolean = false,
        val isProcessing: Boolean = false,
        val paymentData: PaymentData? = null,
        val selectedPaymentMethod: PaymentMethod? = null,
        val error: String? = null,
        val paymentSuccess: Boolean = false
)

class PaymentViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    fun loadPaymentData(orderId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            println("PaymentViewModel: Loading payment data for orderId: $orderId")

            try {
                // Get order details
                orderRepository.getOrderById(orderId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val order = result.data
                            println("PaymentViewModel: Order loaded successfully: ${order.orderId}")
                            println("PaymentViewModel: Customer: ${order.customerFullName}")
                            println("PaymentViewModel: Total: ${order.totalAmount}")

                            val paymentData =
                                    PaymentData(
                                            orderId = order.orderId,
                                            customerName = order.customerFullName,
                                            customerPhone = order.customerPhoneNumber ?: "N/A",
                                            serviceName =
                                                    "Dịch vụ dán decal", // TODO: Get from order
                                            // details
                                            description = order.description
                                                            ?: "Dịch vụ dán decal xe",
                                            totalAmount = order.totalAmount
                                    )
                            println("PaymentViewModel: PaymentData created: $paymentData")

                            _uiState.value =
                                    _uiState.value.copy(
                                            isLoading = false,
                                            paymentData = paymentData
                                    )
                            println("PaymentViewModel: UI state updated with paymentData")
                        }
                        is Result.Error -> {
                            println("PaymentViewModel: Error loading order: ${result.message}")
                            _uiState.value =
                                    _uiState.value.copy(isLoading = false, error = result.message)
                        }
                        else -> {
                            println("PaymentViewModel: Loading state...")
                            // Keep loading state
                        }
                    }
                }
            } catch (e: Exception) {
                println("PaymentViewModel: Exception: ${e.message}")
                _uiState.value =
                        _uiState.value.copy(isLoading = false, error = "Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun selectPaymentMethod(method: PaymentMethod) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun processPayment() {
        val currentState = _uiState.value
        if (currentState.paymentData == null || currentState.selectedPaymentMethod == null) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true, error = null)

            try {
                // Simulate payment processing
                kotlinx.coroutines.delay(2000)

                // TODO: Implement actual payment processing
                when (currentState.selectedPaymentMethod) {
                    PaymentMethod.CASH -> {
                        // For cash payment, just mark as paid
                        processCashPayment(currentState.paymentData.orderId)
                    }
                    PaymentMethod.BANK_TRANSFER -> {
                        // For bank transfer, create payment record
                        processBankTransferPayment(currentState.paymentData.orderId)
                    }
                    PaymentMethod.E_WALLET -> {
                        // For e-wallet, process through payment gateway
                        processEWalletPayment(currentState.paymentData.orderId)
                    }
                }

                _uiState.value = _uiState.value.copy(isProcessing = false, paymentSuccess = true)
            } catch (e: Exception) {
                _uiState.value =
                        _uiState.value.copy(
                                isProcessing = false,
                                error = "Lỗi thanh toán: ${e.message}"
                        )
            }
        }
    }

    private suspend fun processCashPayment(orderId: String) {
        // TODO: Update order status to "Paid" or "Completed"
        // For now, just simulate success
        kotlinx.coroutines.delay(500)
    }

    private suspend fun processBankTransferPayment(orderId: String) {
        // TODO: Create payment record and update order status
        // For now, just simulate success
        kotlinx.coroutines.delay(500)
    }

    private suspend fun processEWalletPayment(orderId: String) {
        // TODO: Process through payment gateway (Momo, ZaloPay, etc.)
        // For now, just simulate success
        kotlinx.coroutines.delay(500)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetPaymentSuccess() {
        _uiState.value = _uiState.value.copy(paymentSuccess = false)
    }
}
