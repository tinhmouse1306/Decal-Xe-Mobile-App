package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.domain.model.OrderStageHistory

class OrderStageHistoryMapper {
    
    fun toDomain(dto: OrderStageHistoryDto): OrderStageHistory {
        return OrderStageHistory(
            stageHistoryId = dto.stageHistoryID,
            orderId = dto.orderID,
            stageName = dto.stageName,
            stageDescription = dto.stageDescription ?: "",
            startDate = dto.startDate,
            endDate = dto.endDate,
            assignedEmployeeId = dto.assignedEmployeeID,
            assignedEmployeeFullName = dto.assignedEmployeeFullName,
            notes = dto.notes
        )
    }
    
    fun toDto(stageHistory: OrderStageHistory): OrderStageHistoryDto {
        return OrderStageHistoryDto(
            stageHistoryID = stageHistory.stageHistoryId,
            orderID = stageHistory.orderId,
            stageName = stageHistory.stageName,
            stageDescription = stageHistory.stageDescription,
            startDate = stageHistory.startDate,
            endDate = stageHistory.endDate,
            assignedEmployeeID = stageHistory.assignedEmployeeId ?: "",
            assignedEmployeeFullName = stageHistory.assignedEmployeeFullName ?: "",
            notes = stageHistory.notes
        )
    }
    
    fun toCreateDto(stageHistory: OrderStageHistory): CreateOrderStageHistoryDto {
        return CreateOrderStageHistoryDto(
            orderID = stageHistory.orderId,
            stageName = stageHistory.stageName,
            stageDescription = stageHistory.stageDescription,
            startDate = stageHistory.startDate,
            endDate = stageHistory.endDate,
            assignedEmployeeID = stageHistory.assignedEmployeeId ?: "",
            notes = stageHistory.notes
        )
    }
}
