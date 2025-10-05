package com.example.decalxeandroid.data.api

import com.example.decalxeandroid.data.dto.CreateOrderStageHistoryDto
import com.example.decalxeandroid.data.dto.OrderStageHistoryDto
import com.example.decalxeandroid.data.dto.UpdateOrderStageHistoryDto
import retrofit2.Response
import retrofit2.http.*

interface OrderStageHistoryApi {

    @GET("OrderStageHistories")
    suspend fun getOrderStageHistories(): Response<List<OrderStageHistoryDto>>

    @GET("OrderStageHistories/{id}")
    suspend fun getOrderStageHistoryById(@Path("id") id: String): Response<OrderStageHistoryDto>

    @GET("OrderStageHistories/by-order/{orderId}")
    suspend fun getOrderStageHistoriesByOrderId(
            @Path("orderId") orderId: String
    ): Response<List<OrderStageHistoryDto>>

    @GET("OrderStageHistories/by-stage/{stage}")
    suspend fun getOrderStageHistoriesByStage(
            @Path("stage") stage: String
    ): Response<List<OrderStageHistoryDto>>

    @GET("OrderStageHistories/latest-by-order/{orderId}")
    suspend fun getLatestOrderStageHistoryByOrderId(
            @Path("orderId") orderId: String
    ): Response<OrderStageHistoryDto>

    @POST("OrderStageHistories")
    suspend fun createOrderStageHistory(
            @Body stageHistory: CreateOrderStageHistoryDto
    ): Response<OrderStageHistoryDto>

    @PUT("OrderStageHistories/{id}")
    suspend fun updateOrderStageHistory(
            @Path("id") id: String,
            @Body stageHistory: UpdateOrderStageHistoryDto
    ): Response<OrderStageHistoryDto>

    @DELETE("OrderStageHistories/{id}")
    suspend fun deleteOrderStageHistory(@Path("id") id: String): Response<Unit>
}
