package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class LoginResponseDto(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("user")
    val user: UserDataDto
)

data class UserDataDto(
    @SerializedName("accountID")
    val accountID: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String?,
    @SerializedName("role")
    val role: String,
    @SerializedName("accountRoleName")
    val accountRoleName: String,
    @SerializedName("isActive")
    val isActive: Boolean,
    @SerializedName("employeeID")
    val employeeID: String?
)
