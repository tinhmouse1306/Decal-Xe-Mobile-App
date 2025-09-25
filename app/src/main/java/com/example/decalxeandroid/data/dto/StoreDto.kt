package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class StoreDto(
    @SerializedName("storeID")
    val storeID: String,
    
    @SerializedName("storeName")
    val storeName: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("managerID")
    val managerID: String,
    
    @SerializedName("managerFullName")
    val managerFullName: String,
    
    @SerializedName("isActive")
    val isActive: Boolean
)

data class CreateStoreDto(
    @SerializedName("storeName")
    val storeName: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("managerID")
    val managerID: String
)

data class UpdateStoreDto(
    @SerializedName("storeName")
    val storeName: String?,
    
    @SerializedName("address")
    val address: String?,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("managerID")
    val managerID: String?
)

