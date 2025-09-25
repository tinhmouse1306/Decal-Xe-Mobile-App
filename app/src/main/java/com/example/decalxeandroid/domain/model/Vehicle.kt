package com.example.decalxeandroid.domain.model

data class Vehicle(
    val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val type: String,
    val engineSize: String?,
    val fuelType: String?,
    val transmission: String?,
    val color: String?,
    val imageUrl: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)



