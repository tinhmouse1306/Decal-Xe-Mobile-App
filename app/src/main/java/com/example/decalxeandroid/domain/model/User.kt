package com.example.decalxeandroid.domain.model

data class User(
        val accountId: String,
        val username: String,
        val email: String,
        val fullName: String,
        val phoneNumber: String?,
        val role: UserRole,
        val isActive: Boolean,
        val createdAt: String,
        val lastLoginAt: String?,
        val employeeId: String?
)

enum class UserRole {
    ADMIN,
    MANAGER,
    SALES,
    TECHNICIAN,
    CUSTOMER
}
