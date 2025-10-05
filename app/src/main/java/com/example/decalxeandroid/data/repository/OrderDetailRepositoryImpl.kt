package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.api.OrderDetailApi
import com.example.decalxeandroid.data.mapper.OrderDetailMapper
import com.example.decalxeandroid.domain.model.OrderDetail
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.OrderDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderDetailRepositoryImpl constructor(private val api: OrderDetailApi) :
        OrderDetailRepository {

    override fun getOrderDetails(): Flow<Result<List<OrderDetail>>> = flow {
        try {
            emit(Result.Loading)
            val response = api.getOrderDetails()
            if (response.isSuccessful) {
                val orderDetails =
                        response.body()?.map { OrderDetailMapper.toDomain(it) } ?: emptyList()
                emit(Result.Success(orderDetails))
            } else {
                emit(Result.Error("Failed to get order details: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getOrderDetailById(orderDetailId: String): Flow<Result<OrderDetail>> = flow {
        try {
            emit(Result.Loading)
            val response = api.getOrderDetailById(orderDetailId)
            if (response.isSuccessful) {
                val orderDetail = response.body()?.let { OrderDetailMapper.toDomain(it) }
                if (orderDetail != null) {
                    emit(Result.Success(orderDetail))
                } else {
                    emit(Result.Error("Order detail not found"))
                }
            } else {
                emit(Result.Error("Failed to get order detail: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getOrderDetailsByOrderId(orderId: String): Flow<Result<List<OrderDetail>>> = flow {
        try {
            emit(Result.Loading)
            android.util.Log.d(
                    "OrderDetailRepository",
                    "Getting order details for orderId: $orderId"
            )
            // Thay vì gọi getOrderDetailsByOrderId, gọi getOrderDetails rồi filter
            val response = api.getOrderDetails()
            android.util.Log.d(
                    "OrderDetailRepository",
                    "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}"
            )
            if (response.isSuccessful) {
                val rawOrderDetails = response.body()
                android.util.Log.d(
                        "OrderDetailRepository",
                        "Raw order details count: ${rawOrderDetails?.size ?: 0}"
                )

                // Filter theo orderID
                val filteredOrderDetails =
                        rawOrderDetails?.filter { it.orderID == orderId } ?: emptyList()
                android.util.Log.d(
                        "OrderDetailRepository",
                        "Filtered order details count for orderId $orderId: ${filteredOrderDetails.size}"
                )

                // Log chi tiết các OrderDetails được filter
                filteredOrderDetails.forEachIndexed { index, detail ->
                    android.util.Log.d(
                            "OrderDetailRepository",
                            "Filtered OrderDetail $index: orderID=${detail.orderID}, serviceName=${detail.serviceName}"
                    )
                }

                val orderDetails = filteredOrderDetails.map { OrderDetailMapper.toDomain(it) }
                android.util.Log.d(
                        "OrderDetailRepository",
                        "Mapped order details count: ${orderDetails.size}"
                )
                emit(Result.Success(orderDetails))
            } else {
                android.util.Log.e(
                        "OrderDetailRepository",
                        "API call failed with code: ${response.code()}"
                )
                emit(Result.Error("Failed to get order details: ${response.code()}"))
            }
        } catch (e: Exception) {
            android.util.Log.e("OrderDetailRepository", "Network error", e)
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun createOrderDetail(orderDetail: OrderDetail): Flow<Result<OrderDetail>> = flow {
        try {
            emit(Result.Loading)
            val createDto = OrderDetailMapper.toCreateDto(orderDetail)
            android.util.Log.d(
                    "OrderDetailRepository",
                    "Creating OrderDetail - orderId: ${createDto.orderId}, serviceId: ${createDto.serviceId}, quantity: ${createDto.quantity}"
            )
            val response = api.createOrderDetail(createDto)
            android.util.Log.d(
                    "OrderDetailRepository",
                    "API Response - code: ${response.code()}, isSuccessful: ${response.isSuccessful}"
            )
            if (response.isSuccessful) {
                val createdOrderDetail = response.body()?.let { OrderDetailMapper.toDomain(it) }
                if (createdOrderDetail != null) {
                    android.util.Log.d(
                            "OrderDetailRepository",
                            "Successfully created OrderDetail: ${createdOrderDetail.orderDetailId}"
                    )
                    emit(Result.Success(createdOrderDetail))
                } else {
                    android.util.Log.e("OrderDetailRepository", "Response body is null")
                    emit(Result.Error("Failed to create order detail"))
                }
            } else {
                android.util.Log.e(
                        "OrderDetailRepository",
                        "API call failed with code: ${response.code()}, message: ${response.message()}"
                )
                emit(Result.Error("Failed to create order detail: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun updateOrderDetail(
            orderDetailId: String,
            orderDetail: OrderDetail
    ): Flow<Result<OrderDetail>> = flow {
        try {
            emit(Result.Loading)
            val updateDto = OrderDetailMapper.toUpdateDto(orderDetail)
            val response = api.updateOrderDetail(orderDetailId, updateDto)
            if (response.isSuccessful) {
                val updatedOrderDetail = response.body()?.let { OrderDetailMapper.toDomain(it) }
                if (updatedOrderDetail != null) {
                    emit(Result.Success(updatedOrderDetail))
                } else {
                    emit(Result.Error("Failed to update order detail"))
                }
            } else {
                emit(Result.Error("Failed to update order detail: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun deleteOrderDetail(orderDetailId: String): Flow<Result<Boolean>> = flow {
        try {
            emit(Result.Loading)
            val response = api.deleteOrderDetail(orderDetailId)
            if (response.isSuccessful) {
                emit(Result.Success(true))
            } else {
                emit(Result.Error("Failed to delete order detail: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }
}
