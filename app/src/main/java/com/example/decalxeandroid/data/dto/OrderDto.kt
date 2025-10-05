package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class OrderDto(
        @SerializedName("orderID") val orderID: String,
        @SerializedName("orderDate") val orderDate: String,
        @SerializedName("totalAmount") val totalAmount: Double,
        @SerializedName("orderStatus") val orderStatus: String,
        @SerializedName("assignedEmployeeID") val assignedEmployeeID: String,
        @SerializedName("assignedEmployeeFullName") val assignedEmployeeFullName: String,
        @SerializedName("vehicleID") val vehicleID: String,
        @SerializedName("chassisNumber") val chassisNumber: String,
        @SerializedName("vehicleModelName") val vehicleModelName: String,
        @SerializedName("vehicleBrandName") val vehicleBrandName: String,
        @SerializedName("expectedArrivalTime") val expectedArrivalTime: String,
        @SerializedName("currentStage") val currentStage: String,
        @SerializedName("priority") val priority: String,
        @SerializedName("isCustomDecal") val isCustomDecal: Boolean,
        @SerializedName("storeID") val storeID: String,
        @SerializedName("description") val description: String?,
        @SerializedName("customerID") val customerID: String,
        @SerializedName("customerFullName") val customerFullName: String,
        @SerializedName("customerPhoneNumber") val customerPhoneNumber: String,
        @SerializedName("customerEmail") val customerEmail: String?,
        @SerializedName("customerAddress") val customerAddress: String?,
        @SerializedName("accountID") val accountID: String,
        @SerializedName("accountUsername") val accountUsername: String,
        @SerializedName("accountCreated") val accountCreated: Boolean
)

data class CreateOrderDto(
        @SerializedName("totalAmount") val totalAmount: Double,
        @SerializedName("customerID") val customerID: String, // ✅ THÊM CustomerID REQUIRED
        @SerializedName("assignedEmployeeID") val assignedEmployeeID: String?,
        @SerializedName("vehicleID") val vehicleID: String?,
        @SerializedName("expectedArrivalTime") val expectedArrivalTime: String?,
        @SerializedName("priority") val priority: String?,
        @SerializedName("isCustomDecal") val isCustomDecal: Boolean,
        @SerializedName("description") val description: String?
)

data class CreateOrderRequest(@SerializedName("createDto") val createDto: CreateOrderDto)

data class UpdateOrderDto(
        @SerializedName("totalAmount") val totalAmount: Double?,
        @SerializedName("assignedEmployeeID") val assignedEmployeeID: String?,
        @SerializedName("vehicleID") val vehicleID: String?,
        @SerializedName("expectedArrivalTime") val expectedArrivalTime: String?,
        @SerializedName("priority") val priority: String?,
        @SerializedName("isCustomDecal") val isCustomDecal: Boolean?,
        @SerializedName("description") val description: String?,
        @SerializedName("orderStatus") val orderStatus: String?,
        @SerializedName("currentStage") val currentStage: String?
)

data class OrderStageHistoryDto(
        @SerializedName("orderStageHistoryID") val stageHistoryID: String,
        @SerializedName("orderID") val orderID: String,
        @SerializedName("stageName") val stageName: String,
        @SerializedName("changeDate") val changeDate: String,
        @SerializedName("changedByEmployeeID") val changedByEmployeeID: String,
        @SerializedName("changedByEmployeeFullName") val changedByEmployeeFullName: String,
        @SerializedName("notes") val notes: String?,
        @SerializedName("stage") val stage: Int,
        @SerializedName("stageDescription") val stageDescription: String?,
        @SerializedName("completionPercentage") val completionPercentage: Int?
)

data class OrderDetailDto(
        @SerializedName("orderDetailID") val orderDetailID: String,
        @SerializedName("orderID") val orderID: String,
        @SerializedName("serviceID") val serviceID: String,
        @SerializedName("serviceName") val serviceName: String,
        @SerializedName("quantity") val quantity: Int,
        @SerializedName("price") val price: Double, // API trả về "price" thay vì "unitPrice"
        @SerializedName("finalCalculatedPrice")
        val finalCalculatedPrice: Double, // API trả về "finalCalculatedPrice" thay vì "totalPrice"
        @SerializedName("description") val description: String?,
        @SerializedName("actualAreaUsed") val actualAreaUsed: Double?,
        @SerializedName("actualLengthUsed") val actualLengthUsed: Double?,
        @SerializedName("actualWidthUsed") val actualWidthUsed: Double?,
        @SerializedName("storeID") val storeID: String?,
        @SerializedName("storeName") val storeName: String?,
        @SerializedName("orderStatus") val orderStatus: String?
)

data class CreateOrderDetailDto(
        @SerializedName("orderID") val orderId: String,
        @SerializedName("serviceID") val serviceId: String,
        @SerializedName("quantity") val quantity: Int,
        @SerializedName("actualAreaUsed") val actualAreaUsed: Double?,
        @SerializedName("actualLengthUsed") val actualLengthUsed: Double?,
        @SerializedName("actualWidthUsed") val actualWidthUsed: Double?
)

data class UpdateOrderDetailDto(
        @SerializedName("quantity") val quantity: Int,
        @SerializedName("actualAreaUsed") val actualAreaUsed: Double?,
        @SerializedName("actualLengthUsed") val actualLengthUsed: Double?,
        @SerializedName("actualWidthUsed") val actualWidthUsed: Double?
)

data class CreateOrderStageHistoryDto(
        @SerializedName("orderID") val orderID: String,
        @SerializedName("stageName") val stageName: String,
        @SerializedName("stageDescription") val stageDescription: String?,
        @SerializedName("startDate") val startDate: String,
        @SerializedName("endDate") val endDate: String?,
        @SerializedName("assignedEmployeeID") val assignedEmployeeID: String,
        @SerializedName("notes") val notes: String?,
        @SerializedName("stage") val stage: Int
)

data class UpdateOrderStageHistoryDto(
        @SerializedName("stageName") val stageName: String?,
        @SerializedName("stageDescription") val stageDescription: String?,
        @SerializedName("startDate") val startDate: String?,
        @SerializedName("endDate") val endDate: String?,
        @SerializedName("assignedEmployeeID") val assignedEmployeeID: String?,
        @SerializedName("notes") val notes: String?
)

// DTOs đã có trong AdditionalDto.kt - không cần duplicate
