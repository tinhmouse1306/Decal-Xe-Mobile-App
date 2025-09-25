package com.example.decalxeandroid.domain.model

data class DecalTemplate(
    val id: String,
    val name: String,
    val description: String?,
    val category: String,
    val imageUrl: String?,
    val price: Double,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)



