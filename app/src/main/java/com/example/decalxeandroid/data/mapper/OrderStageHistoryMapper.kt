package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.CreateOrderStageHistoryDto
import com.example.decalxeandroid.data.dto.OrderStageHistoryDto
import com.example.decalxeandroid.data.dto.UpdateOrderStageHistoryDto
import com.example.decalxeandroid.domain.model.OrderStageHistory

object OrderStageHistoryMapper {

    fun toDomain(dto: OrderStageHistoryDto): OrderStageHistory {
        return OrderStageHistory(
                stageHistoryId = dto.stageHistoryID,
                orderId = dto.orderID,
                stageName = dto.stageName,
                stageDescription = dto.stageDescription,
                startDate = dto.changeDate,
                endDate = null, // API không trả về endDate
                assignedEmployeeId = dto.changedByEmployeeID,
                assignedEmployeeFullName = dto.changedByEmployeeFullName,
                notes = dto.notes
        )
    }

    fun toCreateDto(stageHistory: OrderStageHistory): CreateOrderStageHistoryDto {
        val assignedEmployeeID = stageHistory.assignedEmployeeId ?: ""
        android.util.Log.d(
                "OrderStageHistoryMapper",
                "Mapping to CreateOrderStageHistoryDto - assignedEmployeeId: '${stageHistory.assignedEmployeeId}' -> assignedEmployeeID: '$assignedEmployeeID'"
        )

        // Map stageName to stage number
        val stageNumber = mapStageNameToNumber(stageHistory.stageName)
        android.util.Log.d(
                "OrderStageHistoryMapper",
                "Mapping stageName '${stageHistory.stageName}' to stage number: $stageNumber"
        )

        return CreateOrderStageHistoryDto(
                orderID = stageHistory.orderId,
                stageName = stageHistory.stageName,
                stageDescription = stageHistory.stageDescription,
                startDate = stageHistory.startDate,
                endDate = stageHistory.endDate,
                assignedEmployeeID = assignedEmployeeID,
                notes = stageHistory.notes,
                stage = stageNumber
        )
    }

    private fun mapStageNameToNumber(stageName: String): Int {
        return when (stageName) {
            "Survey" -> 1
            "Designing" -> 2
            "ProductionAndInstallation" -> 3
            "AcceptanceAndDelivery" -> 4
            else -> 0 // Default fallback
        }
    }

    fun toUpdateDto(stageHistory: OrderStageHistory): UpdateOrderStageHistoryDto {
        return UpdateOrderStageHistoryDto(
                stageName = stageHistory.stageName,
                stageDescription = stageHistory.stageDescription,
                startDate = stageHistory.startDate,
                endDate = stageHistory.endDate,
                assignedEmployeeID = stageHistory.assignedEmployeeId ?: "",
                notes = stageHistory.notes
        )
    }
}
