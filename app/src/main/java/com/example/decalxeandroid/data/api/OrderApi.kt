package com.example.decalxeandroid.data.api

import com.example.decalxeandroid.data.dto.OrderDto
import com.example.decalxeandroid.data.dto.CreateOrderDto
import com.example.decalxeandroid.data.dto.UpdateOrderDto
import retrofit2.Response
import retrofit2.http.*

interface OrderApi {
    
    @GET("Orders")
    suspend fun getOrders(): Response<List<OrderDto>>
    
    @GET("Orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): Response<OrderDto>
    
    @GET("Orders/by-customer/{customerId}")
    suspend fun getOrdersByCustomerId(@Path("customerId") customerId: String): Response<List<OrderDto>>
    
    @GET("Orders/by-store/{storeId}")
    suspend fun getOrdersByStoreId(@Path("storeId") storeId: String): Response<List<OrderDto>>
    
    @POST("Orders")
    suspend fun createOrder(@Body order: CreateOrderDto): Response<OrderDto>
    
    @PUT("Orders/{id}")
    suspend fun updateOrder(
        @Path("id") id: String, 
        @Body order: UpdateOrderDto
    ): Response<OrderDto>
    
    @DELETE("Orders/{id}")
    suspend fun deleteOrder(@Path("id") id: String): Response<Unit>
}
