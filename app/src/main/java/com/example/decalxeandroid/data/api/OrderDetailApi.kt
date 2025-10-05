package com.example.decalxeandroid.data.api

import com.example.decalxeandroid.data.dto.CreateOrderDetailDto
import com.example.decalxeandroid.data.dto.OrderDetailDto
import com.example.decalxeandroid.data.dto.UpdateOrderDetailDto
import retrofit2.Response
import retrofit2.http.*

interface OrderDetailApi {

    @GET("OrderDetails") suspend fun getOrderDetails(): Response<List<OrderDetailDto>>

    @GET("OrderDetails/{id}")
    suspend fun getOrderDetailById(@Path("id") id: String): Response<OrderDetailDto>

    @GET("OrderDetails/by-order/{orderId}")
    suspend fun getOrderDetailsByOrderId(
            @Path("orderId") orderId: String
    ): Response<List<OrderDetailDto>>

    @POST("OrderDetails")
    suspend fun createOrderDetail(@Body orderDetail: CreateOrderDetailDto): Response<OrderDetailDto>

    @PUT("OrderDetails/{id}")
    suspend fun updateOrderDetail(
            @Path("id") id: String,
            @Body orderDetail: UpdateOrderDetailDto
    ): Response<OrderDetailDto>

    @DELETE("OrderDetails/{id}")
    suspend fun deleteOrderDetail(@Path("id") id: String): Response<Unit>
}
