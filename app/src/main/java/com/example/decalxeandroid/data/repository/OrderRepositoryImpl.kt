package com.example.decalxeandroid.data.repository

import android.util.Log
import com.example.decalxeandroid.data.dto.CreateOrderWithCustomerDto
import com.example.decalxeandroid.data.dto.EmployeeDto
import com.example.decalxeandroid.data.mapper.OrderDetailMapper
import com.example.decalxeandroid.data.mapper.OrderMapper
import com.example.decalxeandroid.data.mapper.OrderStageHistoryMapper
import com.example.decalxeandroid.data.remote.OrderApiService
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.OrderDetail
import com.example.decalxeandroid.domain.model.OrderStageHistory
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.EmployeeRepository
import com.example.decalxeandroid.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class OrderRepositoryImpl(
        private val api: OrderApiService,
        private val mapper: OrderMapper,
        private val orderDetailMapper: OrderDetailMapper,
        private val stageHistoryMapper: OrderStageHistoryMapper,
        private val employeeRepository: EmployeeRepository
) : OrderRepository {

    override fun getOrders(): Flow<Result<List<Order>>> = flow {
        try {
            val orders = api.getOrders(page = 1, pageSize = 100) // Get more orders
            val mappedOrders =
                    orders.map { orderDto ->
                        val order = mapper.toDomain(orderDto)
                        // If storeID is null, try to get it from assigned employee
                        if (order.storeId.isNullOrBlank() &&
                                        !order.assignedEmployeeId.isNullOrBlank()
                        ) {
                            try {
                                // Get employees synchronously for this fallback
                                var updatedStoreId: String? = null
                                employeeRepository.getEmployees(1, 100).collect { employeeResult ->
                                    when (employeeResult) {
                                        is Result.Success -> {
                                            val employee =
                                                    employeeResult.data.find {
                                                        it.employeeId == order.assignedEmployeeId
                                                    }
                                            if (employee != null) {
                                                updatedStoreId = employee.storeId
                                                Log.d(
                                                        "OrderRepository",
                                                        "Fallback: Found storeId from employee ${employee.employeeId}: ${employee.storeId}"
                                                )
                                            }
                                        }
                                        else -> {
                                            Log.w(
                                                    "OrderRepository",
                                                    "Failed to get employees for storeId fallback"
                                            )
                                        }
                                    }
                                }

                                // Create new Order with updated storeId if found
                                if (updatedStoreId != null) {
                                    return@map order.copy(storeId = updatedStoreId)
                                }
                            } catch (e: Exception) {
                                Log.w(
                                        "OrderRepository",
                                        "Error getting employee for storeId fallback: ${e.message}"
                                )
                            }
                        }
                        order
                    }
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
                        println(
                                "OrderRepository: Successfully received OrderDto: ${orderDto.orderID}"
                        )
                        val order = mapper.toDomain(orderDto)
                        // If storeID is null, try to get it from assigned employee
                        if (order.storeId.isNullOrBlank() &&
                                        !order.assignedEmployeeId.isNullOrBlank()
                        ) {
                            try {
                                // Get employees synchronously for this fallback
                                var updatedStoreId: String? = null
                                employeeRepository.getEmployees(1, 100).collect { employeeResult ->
                                    when (employeeResult) {
                                        is Result.Success -> {
                                            val employee =
                                                    employeeResult.data.find {
                                                        it.employeeId == order.assignedEmployeeId
                                                    }
                                            if (employee != null) {
                                                updatedStoreId = employee.storeId
                                                Log.d(
                                                        "OrderRepository",
                                                        "Fallback: Found storeId from employee ${employee.employeeId}: ${employee.storeId}"
                                                )
                                            }
                                        }
                                        else -> {
                                            Log.w(
                                                    "OrderRepository",
                                                    "Failed to get employees for storeId fallback"
                                            )
                                        }
                                    }
                                }

                                // Create new Order with updated storeId if found
                                val finalOrder =
                                        if (updatedStoreId != null) {
                                            order.copy(storeId = updatedStoreId)
                                        } else {
                                            order
                                        }
                                emit(Result.Success(finalOrder))
                            } catch (e: Exception) {
                                Log.w(
                                        "OrderRepository",
                                        "Error getting employee for storeId fallback: ${e.message}"
                                )
                                emit(Result.Success(order))
                            }
                        } else {
                            emit(Result.Success(order))
                        }
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
            println(
                    "OrderRepository: Exception getting order by ID $orderId: ${e.javaClass.simpleName}: ${e.message}"
            )
            e.printStackTrace()
            emit(Result.Error("Lỗi kết nối: ${e.message}"))
        }
    }

    override fun updateOrderStatus(
            orderId: String,
            status: String,
            stage: String
    ): Flow<Result<Order>> = flow {
        try {
            // Lấy order hiện tại để cập nhật cả status và currentStage
            val currentOrderResponse = api.getOrderById(orderId)
            if (currentOrderResponse.isSuccessful) {
                val currentOrder = mapper.toDomain(currentOrderResponse.body()!!)

                // Tạo order mới với status và currentStage đã cập nhật
                val updatedOrder = currentOrder.copy(orderStatus = status, currentStage = stage)

                // Sử dụng updateOrder API thay vì updateOrderStatus
                val updateDto = mapper.toUpdateDto(updatedOrder)
                val response = api.updateOrder(orderId, updateDto)

                if (response.isSuccessful) {
                    // API trả về 204 No Content, cần reload order để lấy thông tin mới
                    val reloadedOrderResponse = api.getOrderById(orderId)
                    if (reloadedOrderResponse.isSuccessful) {
                        val finalOrder = mapper.toDomain(reloadedOrderResponse.body()!!)
                        emit(Result.Success(finalOrder))
                    } else {
                        emit(
                                Result.Error(
                                        "Failed to reload order after update: ${reloadedOrderResponse.code()}"
                                )
                        )
                    }
                } else {
                    emit(Result.Error("Failed to update order: ${response.code()}"))
                }
            } else {
                emit(Result.Error("Failed to get current order: ${currentOrderResponse.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    companion object {
        private const val BASE_URL = "https://decalxesequences-production.up.railway.app/api/"
    }

    // Cache để lưu thông tin nhân viên và tránh gọi API nhiều lần
    private var employeeCache: List<EmployeeDto>? = null
    private var lastCacheTime: Long = 0
    private val CACHE_DURATION = 5 * 60 * 1000L // 5 phút

    // Lấy StoreID từ nhân viên với cache
    private suspend fun getStoreIdFromEmployee(employeeId: String?): String? {
        if (employeeId.isNullOrBlank()) return null

        try {
            // Kiểm tra cache
            val currentTime = System.currentTimeMillis()
            if (employeeCache == null || (currentTime - lastCacheTime) > CACHE_DURATION) {
                println("OrderRepository: Refreshing employee cache...")
                employeeCache = api.getEmployees()
                lastCacheTime = currentTime
            }

            // Tìm nhân viên trong cache
            val employee = employeeCache?.find { it.employeeID == employeeId }
            val storeId = employee?.storeID

            if (storeId != null) {
                println("OrderRepository: Found storeId from employee $employeeId: $storeId")
            } else {
                println("OrderRepository: No storeId found for employee $employeeId")
            }

            return storeId
        } catch (e: Exception) {
            println(
                    "OrderRepository: Error getting storeId from employee $employeeId: ${e.message}"
            )
            return null
        }
    }

    override fun getOrdersByCustomerId(customerId: String): Flow<Result<List<Order>>> = flow {
        try {
            Log.d("OrderRepository", "Fetching orders for customer ID: $customerId")
            // Since backend doesn't have by-customer endpoint, get all orders and filter
            val allOrders = api.getOrders(page = 1, pageSize = 100)
            val orders = allOrders.filter { it.customerID == customerId }
            val mappedOrders = orders.map { mapper.toDomain(it) }
            Log.d(
                    "OrderRepository",
                    "Successfully mapped ${mappedOrders.size} orders for customer $customerId"
            )
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
            Log.d(
                    "OrderRepository",
                    "Successfully mapped ${mappedOrders.size} orders for store $storeId"
            )
            emit(Result.Success(mappedOrders))
        } catch (e: Exception) {
            Log.e("OrderRepository", "Network error fetching orders for store $storeId", e)
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun createOrder(order: Order): Flow<Result<Order>> = flow {
        try {
            println("OrderRepository: Creating order with new API...")

            // Sử dụng API Orders/with-customer với existingCustomerID
            val createDto =
                    CreateOrderWithCustomerDto(
                            totalAmount = order.totalAmount,
                            assignedEmployeeID =
                                    order.assignedEmployeeId?.takeIf { it.isNotBlank() },
                            vehicleID = order.vehicleId?.takeIf { it.isNotBlank() },
                            expectedArrivalTime =
                                    order.expectedArrivalTime?.takeIf { it.isNotBlank() }?.let {
                                            timeString ->
                                        // Convert "2025-10-05 06:00" to ISO format with Z
                                        // "2025-10-05T06:00:00Z"
                                        when {
                                            timeString.contains(" ") && !timeString.contains("T") ->
                                                    timeString.replace(" ", "T") + ":00Z"
                                            !timeString.contains("T") -> timeString + "T00:00:00Z"
                                            !timeString.endsWith("Z") -> timeString + "Z"
                                            else -> timeString
                                        }
                                    },
                            priority = order.priority?.takeIf { it.isNotBlank() } ?: "Medium",
                            isCustomDecal = order.isCustomDecal,
                            description = order.description?.takeIf { it.isNotBlank() },
                            existingCustomerID = order.customerId?.takeIf { it.isNotBlank() },
                            newCustomerPayload = null // Không tạo customer mới
                    )

            println("OrderRepository: CreateOrderWithCustomerDto = $createDto")

            // Debug: In chi tiết từng field
            println("OrderRepository: Debug fields:")
            println("  - totalAmount: ${createDto.totalAmount}")
            println("  - assignedEmployeeID: ${createDto.assignedEmployeeID}")
            println("  - vehicleID: ${createDto.vehicleID}")
            println("  - expectedArrivalTime: ${createDto.expectedArrivalTime}")
            println("  - priority: ${createDto.priority}")
            println("  - isCustomDecal: ${createDto.isCustomDecal}")
            println("  - description: ${createDto.description}")
            println("  - existingCustomerID: ${createDto.existingCustomerID}")
            println("  - newCustomerPayload: ${createDto.newCustomerPayload}")

            val response = api.createOrderWithCustomer(createDto)
            println("OrderRepository: API Response: $response")

            // Map response về Order domain model
            val createdOrder =
                    Order(
                            orderId = response.orderID,
                            orderNumber = "", // Không có trong response
                            customerId = response.customerID,
                            customerFullName = response.customerFullName,
                            vehicleId = response.vehicleID,
                            vehicleLicensePlate = null, // Không có trong response
                            assignedEmployeeId = response.assignedEmployeeID,
                            assignedEmployeeName = response.assignedEmployeeFullName,
                            orderStatus = response.orderStatus,
                            currentStage = response.currentStage,
                            totalAmount = response.totalAmount,
                            depositAmount = 0.0, // Không có trong response
                            remainingAmount = 0.0, // Không có trong response
                            orderDate = response.orderDate,
                            expectedCompletionDate = null, // Không có trong response
                            actualCompletionDate = null, // Không có trong response
                            notes = response.description,
                            isActive = true,
                            createdAt = response.orderDate,
                            updatedAt = null,
                            chassisNumber = response.chassisNumber,
                            vehicleModelName = response.vehicleModelName,
                            vehicleBrandName = response.vehicleBrandName,
                            expectedArrivalTime = response.expectedArrivalTime,
                            priority = response.priority,
                            isCustomDecal = response.isCustomDecal,
                            storeId = response.storeID
                                            ?: getStoreIdFromEmployee(
                                                    response.assignedEmployeeID
                                            ), // Fallback nếu response không có StoreID
                            description = response.description,
                            customerPhoneNumber = response.customerPhoneNumber,
                            customerEmail = response.customerEmail,
                            customerAddress = response.customerAddress,
                            accountId = response.accountID,
                            accountUsername = response.accountUsername,
                            accountCreated = response.accountCreated
                    )

            println("OrderRepository: Successfully created order: ${createdOrder.orderId}")
            emit(Result.Success(createdOrder))
        } catch (e: Exception) {
            println(
                    "OrderRepository: Error creating order: ${e.javaClass.simpleName}: ${e.message}"
            )
            e.printStackTrace()

            // Debug: In thêm thông tin chi tiết về exception
            if (e is retrofit2.HttpException) {
                println("OrderRepository: HTTP Error Code: ${e.code()}")
                println("OrderRepository: HTTP Error Message: ${e.message()}")
                try {
                    val errorBody = e.response()?.errorBody()?.string()
                    println("OrderRepository: HTTP Error Body: $errorBody")
                } catch (ex: Exception) {
                    println("OrderRepository: Could not read error body: ${ex.message}")
                }
            }

            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun updateOrder(orderId: String, order: Order): Flow<Result<Order>> = flow {
        try {
            val updateDto = mapper.toUpdateDto(order)
            val response = api.updateOrder(orderId, updateDto)

            if (response.isSuccessful) {
                // API trả về 204 No Content, cần reload order để lấy thông tin mới
                val reloadedOrderResponse = api.getOrderById(orderId)
                if (reloadedOrderResponse.isSuccessful) {
                    val updatedOrder = mapper.toDomain(reloadedOrderResponse.body()!!)
                    emit(Result.Success(updatedOrder))
                } else {
                    emit(
                            Result.Error(
                                    "Failed to reload order after update: ${reloadedOrderResponse.code()}"
                            )
                    )
                }
            } else {
                emit(Result.Error("Failed to update order: ${response.code()}"))
            }
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

    override fun getOrderStageHistory(orderId: String): Flow<Result<List<OrderStageHistory>>> =
            flow {
                try {
                    println("OrderRepository: Getting stage history for order: $orderId")
                    val stageHistory = api.getOrderStageHistory(orderId)
                    val mappedHistory = stageHistory.map { stageHistoryMapper.toDomain(it) }
                    println(
                            "OrderRepository: Successfully loaded ${mappedHistory.size} stage history records"
                    )
                    emit(Result.Success(mappedHistory))
                } catch (e: Exception) {
                    println(
                            "OrderRepository: Error getting stage history for $orderId: ${e.message}"
                    )
                    // Return empty list instead of error to not break the main flow
                    emit(Result.Success(emptyList()))
                }
            }
}
