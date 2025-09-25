package com.example.decalxeandroid.domain.model

data class Employee(
    val employeeId: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val email: String?,
    val address: String?,
    val storeId: String?,
    val storeName: String?,
    val accountId: String?,
    val accountUsername: String?,
    val accountRoleName: String?,
    val isActive: Boolean
)



