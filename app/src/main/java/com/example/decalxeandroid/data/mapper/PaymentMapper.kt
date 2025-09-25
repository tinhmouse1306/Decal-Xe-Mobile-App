package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.PaymentDto
import com.example.decalxeandroid.data.dto.CreatePaymentDto
import com.example.decalxeandroid.data.dto.UpdatePaymentDto
import com.example.decalxeandroid.domain.model.Payment

object PaymentMapper {
    fun mapPaymentDtoToPayment(dto: PaymentDto): Payment {
        return Payment(
            id = dto.paymentID,
            orderId = dto.orderID,
            amount = dto.paymentAmount,
            paymentMethod = dto.paymentMethod,
            status = dto.paymentStatus,
            transactionId = dto.transactionID,
            paymentDate = dto.paymentDate,
            notes = dto.notes,
            customerId = "", // PaymentDto doesn't have customerId
            customerName = "", // PaymentDto doesn't have customerName
            createdAt = "", // PaymentDto doesn't have createdAt
            updatedAt = "" // PaymentDto doesn't have updatedAt
        )
    }
    
    fun mapPaymentToCreatePaymentDto(payment: Payment): CreatePaymentDto {
        return CreatePaymentDto(
            orderID = payment.orderId,
            paymentDate = payment.paymentDate,
            paymentAmount = payment.amount,
            paymentMethod = payment.paymentMethod,
            paymentStatus = payment.status,
            transactionID = payment.transactionId,
            notes = payment.notes
        )
    }
    
    fun mapPaymentToUpdatePaymentDto(payment: Payment): UpdatePaymentDto {
        return UpdatePaymentDto(
            paymentStatus = payment.status
        )
    }
}

