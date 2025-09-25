package com.example.decalxeandroid.domain.model

data class OrderDetail(
    val orderDetailId: String,
    val orderId: String,
    val serviceId: String,
    val serviceName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val description: String?
)



