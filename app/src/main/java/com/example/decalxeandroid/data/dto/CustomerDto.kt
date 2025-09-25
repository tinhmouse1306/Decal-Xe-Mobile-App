package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class CustomerDto(
    @SerializedName("customerID")
    val customerID: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("customerFullName")
    val customerFullName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("email")
    val email: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("accountID")
    val accountID: String?,
    @SerializedName("accountUsername")
    val accountUsername: String?,
    @SerializedName("accountRoleName")
    val accountRoleName: String?
)

data class CreateCustomerDto(
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("email")
    val email: String?,
    @SerializedName("address")
    val address: String?,
    @SerializedName("accountID")
    val accountID: String?
)

data class UpdateCustomerDto(
    @SerializedName("fullName")
    val fullName: String?,
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("address")
    val address: String?
)
