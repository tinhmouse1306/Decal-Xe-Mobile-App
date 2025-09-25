package com.example.decalxeandroid.domain.model

data class Account(
    val id: String,
    val customerId: String,
    val customerName: String,
    val accountNumber: String,
    val accountType: String,
    val balance: Double,
    val status: String,
    val openingDate: String,
    val lastTransactionDate: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
)



