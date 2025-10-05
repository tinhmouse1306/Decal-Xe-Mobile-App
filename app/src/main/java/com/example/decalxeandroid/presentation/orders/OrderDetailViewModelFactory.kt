package com.example.decalxeandroid.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.repository.OrderDetailRepository
import com.example.decalxeandroid.domain.repository.OrderRepository
import com.example.decalxeandroid.domain.repository.OrderStageHistoryRepository

class OrderDetailViewModelFactory(
        private val orderRepository: OrderRepository,
        private val orderDetailRepository: OrderDetailRepository,
        private val orderStageHistoryRepository: OrderStageHistoryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrderDetailViewModel(
                    orderRepository,
                    orderDetailRepository,
                    orderStageHistoryRepository
            ) as
                    T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
