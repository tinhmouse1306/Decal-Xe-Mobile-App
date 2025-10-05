package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.domain.model.OrderStageHistory
import com.example.decalxeandroid.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface OrderStageHistoryRepository {

    fun getOrderStageHistories(): Flow<Result<List<OrderStageHistory>>>

    fun getOrderStageHistoryById(stageHistoryId: String): Flow<Result<OrderStageHistory>>

    fun getOrderStageHistoriesByOrderId(orderId: String): Flow<Result<List<OrderStageHistory>>>

    fun getOrderStageHistoriesByStage(stage: String): Flow<Result<List<OrderStageHistory>>>

    fun getLatestOrderStageHistoryByOrderId(orderId: String): Flow<Result<OrderStageHistory>>

    fun createOrderStageHistory(stageHistory: OrderStageHistory): Flow<Result<OrderStageHistory>>

    fun updateOrderStageHistory(
            stageHistoryId: String,
            stageHistory: OrderStageHistory
    ): Flow<Result<OrderStageHistory>>

    fun deleteOrderStageHistory(stageHistoryId: String): Flow<Result<Boolean>>
}
