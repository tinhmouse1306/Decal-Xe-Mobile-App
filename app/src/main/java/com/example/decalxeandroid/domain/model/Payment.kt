package com.example.decalxeandroid.domain.model

data class Payment(
    val id: String,
    val orderId: String,
    val amount: Double,
    val paymentMethod: String,
    val status: String,
    val transactionId: String?,
    val paymentDate: String,
    val notes: String?,
    val customerId: String,
    val customerName: String,
    val createdAt: String,
    val updatedAt: String
)



