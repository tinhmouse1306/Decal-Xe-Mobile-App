package com.example.decalxeandroid.domain.model

data class Service(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val price: Double,
    val duration: Int, // in minutes
    val isActive: Boolean,
    val imageUrl: String?,
    val requirements: String?,
    val warrantyPeriod: Int?, // in days
    val createdAt: String,
    val updatedAt: String
)
