package com.example.decalxeandroid.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.repository.OrderRepository

class OrderEditViewModelFactory(
    private val orderId: String,
    private val orderRepository: OrderRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderEditViewModel::class.java)) {
            return OrderEditViewModel(orderId, orderRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
