package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.domain.model.OrderDetail

class OrderDetailMapper {
    
    fun toDomain(dto: OrderDetailDto): OrderDetail {
        return OrderDetail(
            orderDetailId = dto.orderDetailID,
            orderId = dto.orderID,
            serviceId = dto.serviceID,
            serviceName = dto.serviceName,
            quantity = dto.quantity,
            unitPrice = dto.unitPrice,
            totalPrice = dto.totalPrice,
            description = dto.description
        )
    }
    
    fun toDto(orderDetail: OrderDetail): OrderDetailDto {
        return OrderDetailDto(
            orderDetailID = orderDetail.orderDetailId,
            orderID = orderDetail.orderId,
            serviceID = orderDetail.serviceId,
            serviceName = orderDetail.serviceName,
            quantity = orderDetail.quantity,
            unitPrice = orderDetail.unitPrice,
            totalPrice = orderDetail.totalPrice,
            description = orderDetail.description
        )
    }
    
    fun toCreateDto(orderDetail: OrderDetail): CreateOrderDetailDto {
        return CreateOrderDetailDto(
            serviceID = orderDetail.serviceId,
            quantity = orderDetail.quantity,
            unitPrice = orderDetail.unitPrice,
            description = orderDetail.description
        )
    }
    
    fun toUpdateDto(orderDetail: OrderDetail): UpdateOrderDetailDto {
        return UpdateOrderDetailDto(
            serviceID = orderDetail.serviceId,
            quantity = orderDetail.quantity,
            unitPrice = orderDetail.unitPrice,
            description = orderDetail.description
        )
    }
}
