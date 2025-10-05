package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.CreateOrderDetailDto
import com.example.decalxeandroid.data.dto.OrderDetailDto
import com.example.decalxeandroid.data.dto.UpdateOrderDetailDto
import com.example.decalxeandroid.domain.model.OrderDetail

object OrderDetailMapper {

    fun toDomain(dto: OrderDetailDto): OrderDetail {
        android.util.Log.d(
                "OrderDetailMapper",
                "Mapping OrderDetailDto: serviceName=${dto.serviceName}, price=${dto.price}, finalCalculatedPrice=${dto.finalCalculatedPrice}"
        )
        return OrderDetail(
                orderDetailId = dto.orderDetailID,
                orderId = dto.orderID,
                serviceId = dto.serviceID,
                serviceName = dto.serviceName,
                quantity = dto.quantity,
                unitPrice = dto.price, // Map từ "price" sang "unitPrice"
                totalPrice =
                        dto.finalCalculatedPrice, // Map từ "finalCalculatedPrice" sang "totalPrice"
                description = dto.description
        )
    }

    fun toCreateDto(orderDetail: OrderDetail): CreateOrderDetailDto {
        return CreateOrderDetailDto(
                orderId = orderDetail.orderId,
                serviceId = orderDetail.serviceId,
                quantity = orderDetail.quantity,
                actualAreaUsed = null, // TODO: Add actual area used field to OrderDetail model
                actualLengthUsed = null, // TODO: Add actual length used field to OrderDetail model
                actualWidthUsed = null // TODO: Add actual width used field to OrderDetail model
        )
    }

    fun toUpdateDto(orderDetail: OrderDetail): UpdateOrderDetailDto {
        return UpdateOrderDetailDto(
                quantity = orderDetail.quantity,
                actualAreaUsed = null, // TODO: Add actual area used field to OrderDetail model
                actualLengthUsed = null, // TODO: Add actual length used field to OrderDetail model
                actualWidthUsed = null // TODO: Add actual width used field to OrderDetail model
        )
    }
}
