package com.example.decalxeandroid.domain.usecase.sales

import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.repository.OrderRepository
import com.example.decalxeandroid.domain.model.Result
import kotlinx.coroutines.flow.Flow

class GetSalesStoreOrdersUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(storeId: String): Flow<Result<List<Order>>> {
        return orderRepository.getOrdersByStoreId(storeId)
    }
}
