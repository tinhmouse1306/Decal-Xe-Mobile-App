package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.OrderDetail
import com.example.decalxeandroid.domain.model.OrderStageHistory
import com.example.decalxeandroid.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<Result<List<Order>>>
    fun getOrderById(orderId: String): Flow<Result<Order>>
    fun getOrdersByCustomerId(customerId: String): Flow<Result<List<Order>>>
    fun getOrdersByVehicleId(vehicleId: String): Flow<Result<List<Order>>>
    fun getOrdersByStoreId(storeId: String): Flow<Result<List<Order>>>
    fun createOrder(order: Order): Flow<Result<Order>>
    fun updateOrder(orderId: String, order: Order): Flow<Result<Order>>
    fun deleteOrder(orderId: String): Flow<Result<Boolean>>
    fun getOrderDetails(orderId: String): Flow<Result<List<OrderDetail>>>
    fun getOrderStageHistory(orderId: String): Flow<Result<List<OrderStageHistory>>>
}
