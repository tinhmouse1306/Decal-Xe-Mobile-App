package com.example.decalxeandroid.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.repository.OrderRepository

class OrderDetailViewModelFactory(
    private val orderId: String,
    private val orderRepository: OrderRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderDetailViewModel(
                orderId = orderId,
                orderRepository = orderRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
