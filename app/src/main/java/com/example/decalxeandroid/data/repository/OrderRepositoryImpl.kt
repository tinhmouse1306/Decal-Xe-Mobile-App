package com.example.decalxeandroid.data.repository

import android.util.Log
import com.example.decalxeandroid.data.remote.OrderApiService
import com.example.decalxeandroid.data.dto.OrderDto
import com.example.decalxeandroid.data.mapper.OrderMapper
import com.example.decalxeandroid.data.mapper.OrderDetailMapper
import com.example.decalxeandroid.data.mapper.OrderStageHistoryMapper
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.OrderDetail
import com.example.decalxeandroid.domain.model.OrderStageHistory
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderRepositoryImpl(
    private val api: OrderApiService,
    private val mapper: OrderMapper,
    private val orderDetailMapper: OrderDetailMapper,
    private val stageHistoryMapper: OrderStageHistoryMapper
) : OrderRepository {

    override fun getOrders(): Flow<Result<List<Order>>> = flow {
        try {
            val orders = api.getOrders(page = 1, pageSize = 100) // Get more orders
            val mappedOrders = orders.map { mapper.toDomain(it) }
            emit(Result.Success(mappedOrders))
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getOrderById(orderId: String): Flow<Result<Order>> = flow {
        try {
            println("OrderRepository: Getting order by ID: $orderId")
            println("OrderRepository: API endpoint will be: Orders/$orderId")
            println("OrderRepository: Full URL will be: ${BASE_URL}Orders/$orderId")
            
            val response = api.getOrderById(orderId)
            
            println("OrderRepository: Response code: ${response.code()}")
            println("OrderRepository: Response message: ${response.message()}")
            
            when {
                response.isSuccessful -> {
                    val orderDto = response.body()
                    if (orderDto != null) {
                        println("OrderRepository: Successfully received OrderDto: ${orderDto.orderID}")
                        val order = mapper.toDomain(orderDto)
                        emit(Result.Success(order))
                    } else {
                        println("OrderRepository: Response body is null")
                        emit(Result.Error("Dữ liệu đơn hàng trống"))
                    }
                }
                response.code() == 404 -> {
                    println("OrderRepository: 404 Not Found for order ID: $orderId")
                    emit(Result.Error("Đơn hàng không tồn tại (ID: $orderId)"))
                }
                response.code() == 401 -> {
                    println("OrderRepository: 401 Unauthorized")
                    emit(Result.Error("Không có quyền truy cập đơn hàng"))
                }
                response.code() == 500 -> {
                    println("OrderRepository: 500 Internal Server Error")
                    emit(Result.Error("Lỗi máy chủ, vui lòng thử lại sau"))
                }
                else -> {
                    println("OrderRepository: HTTP ${response.code()}: ${response.message()}")
                    emit(Result.Error("Lỗi khi tải đơn hàng: HTTP ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            println("OrderRepository: Exception getting order by ID $orderId: ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            emit(Result.Error("Lỗi kết nối: ${e.message}"))
        }
    }
    
    companion object {
        private const val BASE_URL = "https://decalxesequences-production.up.railway.app/api/"
    }

    override fun getOrdersByCustomerId(customerId: String): Flow<Result<List<Order>>> = flow {
        try {
            Log.d("OrderRepository", "Fetching orders for customer ID: $customerId")
            // Since backend doesn't have by-customer endpoint, get all orders and filter
            val allOrders = api.getOrders(page = 1, pageSize = 100)
            val orders = allOrders.filter { it.customerID == customerId }
            val mappedOrders = orders.map { mapper.toDomain(it) }
            Log.d("OrderRepository", "Successfully mapped ${mappedOrders.size} orders for customer $customerId")
            emit(Result.Success(mappedOrders))
        } catch (e: Exception) {
            Log.e("OrderRepository", "Network error fetching orders for customer $customerId", e)
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getOrdersByVehicleId(vehicleId: String): Flow<Result<List<Order>>> = flow {
        try {
            val orders = api.getOrdersByVehicle(vehicleId)
            val mappedOrders = orders.map { mapper.toDomain(it) }
            emit(Result.Success(mappedOrders))
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getOrdersByStoreId(storeId: String): Flow<Result<List<Order>>> = flow {
        try {
            Log.d("OrderRepository", "Fetching orders for store ID: $storeId")
            // Since backend doesn't have by-store endpoint, get all orders and filter
            val allOrders = api.getOrders(page = 1, pageSize = 100)
            val orders = allOrders.filter { it.storeID == storeId }
            val mappedOrders = orders.map { mapper.toDomain(it) }
            Log.d("OrderRepository", "Successfully mapped ${mappedOrders.size} orders for store $storeId")
            emit(Result.Success(mappedOrders))
        } catch (e: Exception) {
            Log.e("OrderRepository", "Network error fetching orders for store $storeId", e)
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun createOrder(order: Order): Flow<Result<Order>> = flow {
        try {
            val createDto = mapper.toCreateDto(order)
            val createdOrderDto = api.createOrder(createDto)
            val createdOrder = mapper.toDomain(createdOrderDto)
            emit(Result.Success(createdOrder))
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun updateOrder(orderId: String, order: Order): Flow<Result<Order>> = flow {
        try {
            val updateDto = mapper.toUpdateDto(order)
            val updatedOrderDto = api.updateOrder(orderId, updateDto)
            val updatedOrder = mapper.toDomain(updatedOrderDto)
            emit(Result.Success(updatedOrder))
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun deleteOrder(orderId: String): Flow<Result<Boolean>> = flow {
        try {
            api.deleteOrder(orderId)
            emit(Result.Success(true))
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getOrderDetails(orderId: String): Flow<Result<List<OrderDetail>>> = flow {
        try {
            println("OrderRepository: Getting order details for order: $orderId")
            val orderDetails = api.getOrderDetails(orderId)
            val mappedDetails = orderDetails.map { orderDetailMapper.toDomain(it) }
            println("OrderRepository: Successfully loaded ${mappedDetails.size} order details")
            emit(Result.Success(mappedDetails))
        } catch (e: Exception) {
            println("OrderRepository: Error getting order details for $orderId: ${e.message}")
            // Return empty list instead of error to not break the main flow
            emit(Result.Success(emptyList()))
        }
    }

    override fun getOrderStageHistory(orderId: String): Flow<Result<List<OrderStageHistory>>> = flow {
        try {
            println("OrderRepository: Getting stage history for order: $orderId")
            val stageHistory = api.getOrderStageHistory(orderId)
            val mappedHistory = stageHistory.map { stageHistoryMapper.toDomain(it) }
            println("OrderRepository: Successfully loaded ${mappedHistory.size} stage history records")
            emit(Result.Success(mappedHistory))
        } catch (e: Exception) {
            println("OrderRepository: Error getting stage history for $orderId: ${e.message}")
            // Return empty list instead of error to not break the main flow
            emit(Result.Success(emptyList()))
        }
    }
}
