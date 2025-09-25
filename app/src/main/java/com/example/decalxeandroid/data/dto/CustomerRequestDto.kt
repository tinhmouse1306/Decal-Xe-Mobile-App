package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class CustomerRequestDto(
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("accountID")
    val accountID: String
)
