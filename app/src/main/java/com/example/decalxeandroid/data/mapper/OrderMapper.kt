package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.domain.model.*

class OrderMapper {
    
    fun toDomain(dto: OrderDto): Order {
        return Order(
            orderId = dto.orderID,
            orderNumber = dto.orderID, // Use orderID as orderNumber
            customerId = dto.customerID,
            customerFullName = dto.customerFullName,
            vehicleId = dto.vehicleID,
            vehicleLicensePlate = dto.chassisNumber, // Use chassisNumber as license plate
            assignedEmployeeId = dto.assignedEmployeeID,
            assignedEmployeeName = dto.assignedEmployeeFullName,
            orderStatus = dto.orderStatus,
            currentStage = dto.currentStage,
            totalAmount = dto.totalAmount,
            depositAmount = 0.0, // OrderDto doesn't have depositAmount
            remainingAmount = 0.0, // OrderDto doesn't have remainingAmount
            orderDate = dto.orderDate,
            expectedCompletionDate = dto.expectedArrivalTime, // Use expectedArrivalTime
            actualCompletionDate = null, // OrderDto doesn't have actualCompletionDate
            notes = dto.description,
            isActive = true, // OrderDto doesn't have isActive
            createdAt = "", // OrderDto doesn't have createdAt
            updatedAt = null, // OrderDto doesn't have updatedAt
            // Additional properties from OrderDto
            chassisNumber = dto.chassisNumber,
            vehicleModelName = dto.vehicleModelName,
            vehicleBrandName = dto.vehicleBrandName,
            expectedArrivalTime = dto.expectedArrivalTime,
            priority = dto.priority,
            isCustomDecal = dto.isCustomDecal,
            storeId = dto.storeID,
            description = dto.description,
            customerPhoneNumber = dto.customerPhoneNumber,
            customerEmail = dto.customerEmail,
            customerAddress = dto.customerAddress,
            accountId = dto.accountID,
            accountUsername = dto.accountUsername,
            accountCreated = dto.accountCreated
        )
    }
    
    fun toDto(order: Order): OrderDto {
        return OrderDto(
            orderID = order.orderId,
            orderDate = order.orderDate,
            totalAmount = order.totalAmount,
            orderStatus = order.orderStatus,
            assignedEmployeeID = order.assignedEmployeeId ?: "",
            assignedEmployeeFullName = order.assignedEmployeeName ?: "",
            vehicleID = order.vehicleId ?: "",
            chassisNumber = order.chassisNumber ?: "",
            vehicleModelName = order.vehicleModelName ?: "",
            vehicleBrandName = order.vehicleBrandName ?: "",
            expectedArrivalTime = order.expectedArrivalTime ?: "",
            currentStage = order.currentStage,
            priority = order.priority ?: "",
            isCustomDecal = order.isCustomDecal,
            storeID = order.storeId ?: "",
            description = order.description ?: "",
            customerID = order.customerId,
            customerFullName = order.customerFullName,
            customerPhoneNumber = order.customerPhoneNumber ?: "",
            customerEmail = order.customerEmail ?: "",
            customerAddress = order.customerAddress ?: "",
            accountID = order.accountId ?: "",
            accountUsername = order.accountUsername ?: "",
            accountCreated = order.accountCreated ?: false
        )
    }
    
    fun toCreateDto(order: Order): CreateOrderDto {
        return CreateOrderDto(
            totalAmount = order.totalAmount,
            assignedEmployeeID = order.assignedEmployeeId ?: "",
            vehicleID = order.vehicleId ?: "",
            expectedArrivalTime = order.expectedArrivalTime ?: "",
            priority = order.priority ?: "Normal",
            isCustomDecal = order.isCustomDecal,
            description = order.description
        )
    }
    
    fun toUpdateDto(order: Order): UpdateOrderDto {
        return UpdateOrderDto(
            totalAmount = order.totalAmount,
            assignedEmployeeID = order.assignedEmployeeId,
            vehicleID = order.vehicleId,
            expectedArrivalTime = order.expectedArrivalTime,
            priority = order.priority,
            isCustomDecal = order.isCustomDecal,
            description = order.description
        )
    }
}
