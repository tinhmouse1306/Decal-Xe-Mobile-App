package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class OrderDto(
    @SerializedName("orderID")
    val orderID: String,
    @SerializedName("orderDate")
    val orderDate: String,
    @SerializedName("totalAmount")
    val totalAmount: Double,
    @SerializedName("orderStatus")
    val orderStatus: String,
    @SerializedName("assignedEmployeeID")
    val assignedEmployeeID: String,
    @SerializedName("assignedEmployeeFullName")
    val assignedEmployeeFullName: String,
    @SerializedName("vehicleID")
    val vehicleID: String,
    @SerializedName("chassisNumber")
    val chassisNumber: String,
    @SerializedName("vehicleModelName")
    val vehicleModelName: String,
    @SerializedName("vehicleBrandName")
    val vehicleBrandName: String,
    @SerializedName("expectedArrivalTime")
    val expectedArrivalTime: String,
    @SerializedName("currentStage")
    val currentStage: String,
    @SerializedName("priority")
    val priority: String,
    @SerializedName("isCustomDecal")
    val isCustomDecal: Boolean,
    @SerializedName("storeID")
    val storeID: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("customerID")
    val customerID: String,
    @SerializedName("customerFullName")
    val customerFullName: String,
    @SerializedName("customerPhoneNumber")
    val customerPhoneNumber: String,
    @SerializedName("customerEmail")
    val customerEmail: String?,
    @SerializedName("customerAddress")
    val customerAddress: String?,
    @SerializedName("accountID")
    val accountID: String,
    @SerializedName("accountUsername")
    val accountUsername: String,
    @SerializedName("accountCreated")
    val accountCreated: Boolean
)

data class CreateOrderDto(
    @SerializedName("totalAmount")
    val totalAmount: Double,
    @SerializedName("assignedEmployeeID")
    val assignedEmployeeID: String,
    @SerializedName("vehicleID")
    val vehicleID: String,
    @SerializedName("expectedArrivalTime")
    val expectedArrivalTime: String,
    @SerializedName("priority")
    val priority: String,
    @SerializedName("isCustomDecal")
    val isCustomDecal: Boolean,
    @SerializedName("description")
    val description: String?
)

data class UpdateOrderDto(
    @SerializedName("totalAmount")
    val totalAmount: Double?,
    @SerializedName("assignedEmployeeID")
    val assignedEmployeeID: String?,
    @SerializedName("vehicleID")
    val vehicleID: String?,
    @SerializedName("expectedArrivalTime")
    val expectedArrivalTime: String?,
    @SerializedName("priority")
    val priority: String?,
    @SerializedName("isCustomDecal")
    val isCustomDecal: Boolean?,
    @SerializedName("description")
    val description: String?
)

data class OrderStageHistoryDto(
    @SerializedName("stageHistoryID")
    val stageHistoryID: String,
    @SerializedName("orderID")
    val orderID: String,
    @SerializedName("stageName")
    val stageName: String,
    @SerializedName("stageDescription")
    val stageDescription: String?,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String?,
    @SerializedName("assignedEmployeeID")
    val assignedEmployeeID: String,
    @SerializedName("assignedEmployeeFullName")
    val assignedEmployeeFullName: String,
    @SerializedName("notes")
    val notes: String?
)

data class OrderDetailDto(
    @SerializedName("orderDetailID")
    val orderDetailID: String,
    @SerializedName("orderID")
    val orderID: String,
    @SerializedName("serviceID")
    val serviceID: String,
    @SerializedName("serviceName")
    val serviceName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unitPrice")
    val unitPrice: Double,
    @SerializedName("totalPrice")
    val totalPrice: Double,
    @SerializedName("description")
    val description: String?
)

data class CreateOrderDetailDto(
    @SerializedName("serviceID")
    val serviceID: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unitPrice")
    val unitPrice: Double,
    @SerializedName("description")
    val description: String?
)

data class UpdateOrderDetailDto(
    @SerializedName("serviceID")
    val serviceID: String?,
    @SerializedName("quantity")
    val quantity: Int?,
    @SerializedName("unitPrice")
    val unitPrice: Double?,
    @SerializedName("description")
    val description: String?
)

data class CreateOrderStageHistoryDto(
    @SerializedName("orderID")
    val orderID: String,
    @SerializedName("stageName")
    val stageName: String,
    @SerializedName("stageDescription")
    val stageDescription: String?,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String?,
    @SerializedName("assignedEmployeeID")
    val assignedEmployeeID: String,
    @SerializedName("notes")
    val notes: String?
)

data class UpdateOrderStageHistoryDto(
    @SerializedName("stageName")
    val stageName: String?,
    @SerializedName("stageDescription")
    val stageDescription: String?,
    @SerializedName("startDate")
    val startDate: String?,
    @SerializedName("endDate")
    val endDate: String?,
    @SerializedName("assignedEmployeeID")
    val assignedEmployeeID: String?,
    @SerializedName("notes")
    val notes: String?
)
