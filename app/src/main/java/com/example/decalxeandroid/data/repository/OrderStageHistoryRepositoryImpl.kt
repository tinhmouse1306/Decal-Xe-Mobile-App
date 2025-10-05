package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.api.OrderStageHistoryApi
import com.example.decalxeandroid.data.mapper.OrderStageHistoryMapper
import com.example.decalxeandroid.domain.model.OrderStageHistory
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.OrderStageHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class OrderStageHistoryRepositoryImpl constructor(private val api: OrderStageHistoryApi) :
        OrderStageHistoryRepository {

    override fun getOrderStageHistories(): Flow<Result<List<OrderStageHistory>>> = flow {
        try {
            emit(Result.Loading)
            val response = api.getOrderStageHistories()
            if (response.isSuccessful) {
                val stageHistories =
                        response.body()?.map { OrderStageHistoryMapper.toDomain(it) } ?: emptyList()
                emit(Result.Success(stageHistories))
            } else {
                emit(Result.Error("Failed to get order stage histories: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getOrderStageHistoryById(stageHistoryId: String): Flow<Result<OrderStageHistory>> =
            flow {
                try {
                    emit(Result.Loading)
                    val response = api.getOrderStageHistoryById(stageHistoryId)
                    if (response.isSuccessful) {
                        val stageHistory =
                                response.body()?.let { OrderStageHistoryMapper.toDomain(it) }
                        if (stageHistory != null) {
                            emit(Result.Success(stageHistory))
                        } else {
                            emit(Result.Error("Order stage history not found"))
                        }
                    } else {
                        emit(Result.Error("Failed to get order stage history: ${response.code()}"))
                    }
                } catch (e: Exception) {
                    emit(Result.Error("Network error: ${e.message}"))
                }
            }

    override fun getOrderStageHistoriesByOrderId(
            orderId: String
    ): Flow<Result<List<OrderStageHistory>>> = flow {
        try {
            emit(Result.Loading)
            val response = api.getOrderStageHistoriesByOrderId(orderId)
            if (response.isSuccessful) {
                val stageHistories =
                        response.body()?.map { OrderStageHistoryMapper.toDomain(it) } ?: emptyList()
                emit(Result.Success(stageHistories))
            } else {
                emit(Result.Error("Failed to get order stage histories: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getOrderStageHistoriesByStage(
            stage: String
    ): Flow<Result<List<OrderStageHistory>>> = flow {
        try {
            emit(Result.Loading)
            val response = api.getOrderStageHistoriesByStage(stage)
            if (response.isSuccessful) {
                val stageHistories =
                        response.body()?.map { OrderStageHistoryMapper.toDomain(it) } ?: emptyList()
                emit(Result.Success(stageHistories))
            } else {
                emit(Result.Error("Failed to get order stage histories: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getLatestOrderStageHistoryByOrderId(
            orderId: String
    ): Flow<Result<OrderStageHistory>> = flow {
        try {
            emit(Result.Loading)
            val response = api.getLatestOrderStageHistoryByOrderId(orderId)
            if (response.isSuccessful) {
                val stageHistory = response.body()?.let { OrderStageHistoryMapper.toDomain(it) }
                if (stageHistory != null) {
                    emit(Result.Success(stageHistory))
                } else {
                    emit(Result.Error("Latest order stage history not found"))
                }
            } else {
                emit(Result.Error("Failed to get latest order stage history: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun createOrderStageHistory(
            stageHistory: OrderStageHistory
    ): Flow<Result<OrderStageHistory>> = flow {
        try {
            emit(Result.Loading)
            val createDto = OrderStageHistoryMapper.toCreateDto(stageHistory)
            val response = api.createOrderStageHistory(createDto)
            if (response.isSuccessful) {
                val createdStageHistory =
                        response.body()?.let { OrderStageHistoryMapper.toDomain(it) }
                if (createdStageHistory != null) {
                    emit(Result.Success(createdStageHistory))
                } else {
                    emit(Result.Error("Failed to create order stage history"))
                }
            } else {
                emit(Result.Error("Failed to create order stage history: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun updateOrderStageHistory(
            stageHistoryId: String,
            stageHistory: OrderStageHistory
    ): Flow<Result<OrderStageHistory>> = flow {
        try {
            emit(Result.Loading)
            val updateDto = OrderStageHistoryMapper.toUpdateDto(stageHistory)
            val response = api.updateOrderStageHistory(stageHistoryId, updateDto)
            if (response.isSuccessful) {
                val updatedStageHistory =
                        response.body()?.let { OrderStageHistoryMapper.toDomain(it) }
                if (updatedStageHistory != null) {
                    emit(Result.Success(updatedStageHistory))
                } else {
                    emit(Result.Error("Failed to update order stage history"))
                }
            } else {
                emit(Result.Error("Failed to update order stage history: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun deleteOrderStageHistory(stageHistoryId: String): Flow<Result<Boolean>> = flow {
        try {
            emit(Result.Loading)
            val response = api.deleteOrderStageHistory(stageHistoryId)
            if (response.isSuccessful) {
                emit(Result.Success(true))
            } else {
                emit(Result.Error("Failed to delete order stage history: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }
}
