package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class RegisterResponseDto(
    @SerializedName("message")
    val message: String,
    @SerializedName("accountID")
    val accountID: String? = null
)
