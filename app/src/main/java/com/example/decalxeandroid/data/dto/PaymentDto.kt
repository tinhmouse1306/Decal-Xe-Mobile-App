package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class PaymentDto(
    @SerializedName("paymentID")
    val paymentID: String,
    
    @SerializedName("orderID")
    val orderID: String,
    
    @SerializedName("paymentDate")
    val paymentDate: String,
    
    @SerializedName("paymentAmount")
    val paymentAmount: Double,
    
    @SerializedName("paymentMethod")
    val paymentMethod: String,
    
    @SerializedName("paymentStatus")
    val paymentStatus: String,
    
    @SerializedName("transactionID")
    val transactionID: String?,
    
    @SerializedName("notes")
    val notes: String?
)

data class CreatePaymentDto(
    @SerializedName("orderID")
    val orderID: String,
    
    @SerializedName("paymentDate")
    val paymentDate: String,
    
    @SerializedName("paymentAmount")
    val paymentAmount: Double,
    
    @SerializedName("paymentMethod")
    val paymentMethod: String,
    
    @SerializedName("paymentStatus")
    val paymentStatus: String,
    
    @SerializedName("transactionID")
    val transactionID: String?,
    
    @SerializedName("notes")
    val notes: String?
)

data class UpdatePaymentDto(
    @SerializedName("paymentStatus")
    val paymentStatus: String?
)

data class DepositDto(
    @SerializedName("depositID")
    val depositID: String,
    
    @SerializedName("orderID")
    val orderID: String,
    
    @SerializedName("depositAmount")
    val depositAmount: Double,
    
    @SerializedName("depositDate")
    val depositDate: String,
    
    @SerializedName("depositMethod")
    val depositMethod: String,
    
    @SerializedName("depositStatus")
    val depositStatus: String,
    
    @SerializedName("notes")
    val notes: String?
)

data class CreateDepositDto(
    @SerializedName("orderID")
    val orderID: String,
    
    @SerializedName("depositAmount")
    val depositAmount: Double,
    
    @SerializedName("depositDate")
    val depositDate: String,
    
    @SerializedName("depositMethod")
    val depositMethod: String,
    
    @SerializedName("depositStatus")
    val depositStatus: String,
    
    @SerializedName("notes")
    val notes: String?
)

data class PaymentStatisticsDto(
    @SerializedName("totalPayments")
    val totalPayments: Int,
    
    @SerializedName("totalAmount")
    val totalAmount: Double,
    
    @SerializedName("paymentsByMethod")
    val paymentsByMethod: Map<String, Double>,
    
    @SerializedName("paymentsByStatus")
    val paymentsByStatus: Map<String, Int>,
    
    @SerializedName("averagePaymentAmount")
    val averagePaymentAmount: Double,
    
    @SerializedName("paymentsThisMonth")
    val paymentsThisMonth: Int,
    
    @SerializedName("revenueThisMonth")
    val revenueThisMonth: Double
)

