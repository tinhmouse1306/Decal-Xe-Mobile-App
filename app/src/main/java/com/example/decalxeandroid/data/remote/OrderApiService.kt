package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.*
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface OrderApiService {
        @GET("Orders")
        suspend fun getOrders(
                @Query("pageNumber") page: Int = 1,
                @Query("pageSize") pageSize: Int = 20,
                @Query("status") status: String? = null
        ): List<OrderDto>

        @GET("Orders/{id}")
        suspend fun getOrderById(@Path("id") orderId: String): retrofit2.Response<OrderDto>

        @GET("Orders/by-customer/{customerId}")
        suspend fun getOrdersByCustomer(@Path("customerId") customerId: String): List<OrderDto>

        @GET("Orders/by-vehicle/{vehicleId}")
        suspend fun getOrdersByVehicle(@Path("vehicleId") vehicleId: String): List<OrderDto>

        @GET("Orders/by-store/{storeId}")
        suspend fun getOrdersByStore(@Path("storeId") storeId: String): List<OrderDto>

        @POST("Orders") suspend fun createOrder(@Body createDto: CreateOrderDto): OrderDto

        @POST("Orders/with-customer")
        suspend fun createOrderWithCustomer(
                @Body createDto: CreateOrderWithCustomerDto
        ): OrderWithCustomerResponseDto

        @PUT("Orders/{id}")
        suspend fun updateOrder(
                @Path("id") orderId: String,
                @Body order: UpdateOrderDto
        ): Response<Unit>

        @DELETE("Orders/{id}") suspend fun deleteOrder(@Path("id") orderId: String): String

        @PUT("Orders/{id}/status")
        suspend fun updateOrderStatus(
                @Path("id") orderId: String,
                @Body newStatus: String
        ): Response<Unit>

        // Order Details
        @GET("OrderDetails/by-order/{orderId}")
        suspend fun getOrderDetails(@Path("orderId") orderId: String): List<OrderDetailDto>

        @POST("OrderDetails")
        suspend fun addOrderDetail(@Body orderDetail: CreateOrderDetailDto): OrderDetailDto

        @PUT("OrderDetails/{id}")
        suspend fun updateOrderDetail(
                @Path("id") detailId: String,
                @Body orderDetail: UpdateOrderDetailDto
        ): OrderDetailDto

        @DELETE("OrderDetails/{id}")
        suspend fun deleteOrderDetail(@Path("id") detailId: String): String

        // Order Stage History
        @GET("OrderStageHistories/by-order/{orderId}")
        suspend fun getOrderStageHistory(
                @Path("orderId") orderId: String
        ): List<OrderStageHistoryDto>

        @POST("OrderStageHistories")
        suspend fun addStageHistory(
                @Body stageHistory: CreateOrderStageHistoryDto
        ): OrderStageHistoryDto

        // Search and Filter
        @GET("Orders/search")
        suspend fun searchOrders(@Query("query") query: String): List<OrderDto>

        @GET("Orders/by-status/{status}")
        suspend fun getOrdersByStatus(@Path("status") status: String): List<OrderDto>

        // Employees
        @GET("Employees") suspend fun getEmployees(): List<EmployeeDto>
}

data class OrderStatusUpdateDto(
        @SerializedName("status") val status: String,
        @SerializedName("currentStage") val currentStage: String
)

data class CreateOrderDetailDto(
        @SerializedName("orderID") val orderId: String,
        @SerializedName("serviceID") val serviceId: String,
        @SerializedName("quantity") val quantity: Int,
        @SerializedName("unitPrice") val unitPrice: Double,
        @SerializedName("notes") val notes: String?
)

data class UpdateOrderDetailDto(
        @SerializedName("serviceID") val serviceId: String?,
        @SerializedName("quantity") val quantity: Int?,
        @SerializedName("unitPrice") val unitPrice: Double?,
        @SerializedName("notes") val notes: String?
)

data class CreateOrderStageHistoryDto(
        @SerializedName("orderID") val orderId: String,
        @SerializedName("stageName") val stageName: String,
        @SerializedName("stageDescription") val stageDescription: String?,
        @SerializedName("notes") val notes: String?
)
