package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.domain.model.OrderDetail
import com.example.decalxeandroid.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface OrderDetailRepository {

    fun getOrderDetails(): Flow<Result<List<OrderDetail>>>

    fun getOrderDetailById(orderDetailId: String): Flow<Result<OrderDetail>>

    fun getOrderDetailsByOrderId(orderId: String): Flow<Result<List<OrderDetail>>>

    fun createOrderDetail(orderDetail: OrderDetail): Flow<Result<OrderDetail>>

    fun updateOrderDetail(
            orderDetailId: String,
            orderDetail: OrderDetail
    ): Flow<Result<OrderDetail>>

    fun deleteOrderDetail(orderDetailId: String): Flow<Result<Boolean>>
}
