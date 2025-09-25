package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class RegisterRequestDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("roleID")
    val roleID: String
)

data class RegisterMobileRequestDto(
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("confirmPassword")
    val confirmPassword: String
)

data class RefreshTokenRequestDto(
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class LogoutRequestDto(
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class ChangePasswordRequestDto(
    @SerializedName("currentPassword")
    val currentPassword: String,
    @SerializedName("newPassword")
    val newPassword: String,
    @SerializedName("confirmPassword")
    val confirmPassword: String
)

data class ResetPasswordRequestDto(
    @SerializedName("username")
    val username: String,
    @SerializedName("newPassword")
    val newPassword: String,
    @SerializedName("confirmPassword")
    val confirmPassword: String
)
