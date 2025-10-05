package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class CustomerVehicleDto(
        @SerializedName("vehicleID") val vehicleID: String,
        @SerializedName("chassisNumber") val chassisNumber: String,
        @SerializedName("licensePlate") val licensePlate: String?,
        @SerializedName("color") val color: String?,
        @SerializedName("year") val year: Int?,
        @SerializedName("initialKM") val initialKM: Double?,
        @SerializedName("customerID") val customerID: String,
        @SerializedName("customerFullName") val customerFullName: String,
        @SerializedName("modelID") val modelID: String,
        @SerializedName("vehicleModelName") val vehicleModelName: String?,
        @SerializedName("vehicleBrandName") val vehicleBrandName: String?
)

data class CreateCustomerVehicleDto(
        @SerializedName("customerID") val customerID: String,
        @SerializedName("chassisNumber") val chassisNumber: String,
        @SerializedName("licensePlate") val licensePlate: String?,
        @SerializedName("color") val color: String?,
        @SerializedName("year") val year: Int?,
        @SerializedName("initialKM") val initialKM: Double?,
        @SerializedName("modelID") val modelID: String
)

data class UpdateCustomerVehicleDto(
        @SerializedName("chassisNumber") val chassisNumber: String?,
        @SerializedName("licensePlate") val licensePlate: String?,
        @SerializedName("color") val color: String?,
        @SerializedName("year") val year: Int?,
        @SerializedName("initialKM") val initialKM: Double?,
        @SerializedName("modelID") val modelID: String?
)
