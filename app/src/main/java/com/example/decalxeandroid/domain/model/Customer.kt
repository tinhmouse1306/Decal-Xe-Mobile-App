package com.example.decalxeandroid.domain.model

data class Customer(
    val customerId: String,
    val fullName: String,
    val email: String?,
    val phoneNumber: String?,
    val address: String?,
    val dateOfBirth: String?,
    val gender: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String?
)
