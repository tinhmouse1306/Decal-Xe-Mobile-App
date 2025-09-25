package com.example.decalxeandroid.domain.model

data class Store(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val district: String,
    val phoneNumber: String,
    val email: String,
    val latitude: Double?,
    val longitude: Double?,
    val isActive: Boolean,
    val openingHours: String?,
    val managerId: String?,
    val managerName: String?,
    val createdAt: String,
    val updatedAt: String
)



