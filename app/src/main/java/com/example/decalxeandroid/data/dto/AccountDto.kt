package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class AccountDto(
    @SerializedName("accountID")
    val accountID: String,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("isActive")
    val isActive: Boolean,
    
    @SerializedName("roleID")
    val roleID: String,
    
    @SerializedName("roleName")
    val roleName: String
)

data class CreateAccountDto(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("roleID")
    val roleID: String
)

data class UpdateAccountDto(
    @SerializedName("username")
    val username: String?,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("isActive")
    val isActive: Boolean?,
    
    @SerializedName("roleID")
    val roleID: String?
)

data class AccountStatisticsDto(
    @SerializedName("totalAccounts")
    val totalAccounts: Int,
    
    @SerializedName("activeAccounts")
    val activeAccounts: Int,
    
    @SerializedName("totalBalance")
    val totalBalance: Double,
    
    @SerializedName("accountsByType")
    val accountsByType: Map<String, Int>,
    
    @SerializedName("accountsByStatus")
    val accountsByStatus: Map<String, Int>,
    
    @SerializedName("averageBalance")
    val averageBalance: Double,
    
    @SerializedName("newAccountsThisMonth")
    val newAccountsThisMonth: Int
)

