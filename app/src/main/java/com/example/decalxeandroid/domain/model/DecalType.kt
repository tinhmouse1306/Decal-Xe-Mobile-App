package com.example.decalxeandroid.domain.model

data class DecalType(
    val id: String,
    val name: String,
    val description: String?,
    val category: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)



