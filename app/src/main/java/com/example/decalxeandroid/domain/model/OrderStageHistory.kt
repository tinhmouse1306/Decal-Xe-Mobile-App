package com.example.decalxeandroid.domain.model

data class OrderStageHistory(
    val stageHistoryId: String,
    val orderId: String,
    val stageName: String,
    val stageDescription: String,
    val startDate: String,
    val endDate: String?,
    val assignedEmployeeId: String?,
    val assignedEmployeeFullName: String?,
    val notes: String?
)



