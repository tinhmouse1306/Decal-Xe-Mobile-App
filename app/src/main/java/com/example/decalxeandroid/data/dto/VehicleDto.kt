package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class VehicleDto(
    @SerializedName("vehicleID")
    val vehicleID: String,
    
    @SerializedName("licensePlate")
    val licensePlate: String,
    
    @SerializedName("vehicleModelID")
    val vehicleModelID: String,
    
    @SerializedName("vehicleModelName")
    val vehicleModelName: String,
    
    @SerializedName("vehicleBrandName")
    val vehicleBrandName: String,
    
    @SerializedName("year")
    val year: Int,
    
    @SerializedName("color")
    val color: String,
    
    @SerializedName("vin")
    val vin: String?
)

data class VehicleBrandDto(
    @SerializedName("brandID")
    val brandID: String,
    
    @SerializedName("brandName")
    val brandName: String
)

data class VehicleModelDto(
    @SerializedName("modelID")
    val modelID: String,
    
    @SerializedName("modelName")
    val modelName: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("brandID")
    val brandID: String,
    
    @SerializedName("brandName")
    val brandName: String
)

data class CreateVehicleModelDto(
    @SerializedName("modelName")
    val modelName: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("chassisNumber")
    val chassisNumber: String,
    
    @SerializedName("vehicleType")
    val vehicleType: String,
    
    @SerializedName("brandID")
    val brandID: String
)

data class UpdateVehicleModelDto(
    @SerializedName("modelName")
    val modelName: String?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("chassisNumber")
    val chassisNumber: String?,
    
    @SerializedName("vehicleType")
    val vehicleType: String?,
    
    @SerializedName("brandID")
    val brandID: String?
)

// Add missing DTOs
data class CreateVehicleDto(
    @SerializedName("licensePlate")
    val licensePlate: String,
    
    @SerializedName("vehicleModelID")
    val vehicleModelID: String,
    
    @SerializedName("year")
    val year: Int,
    
    @SerializedName("color")
    val color: String,
    
    @SerializedName("vin")
    val vin: String?
)

data class UpdateVehicleDto(
    @SerializedName("licensePlate")
    val licensePlate: String?,
    
    @SerializedName("vehicleModelID")
    val vehicleModelID: String?,
    
    @SerializedName("year")
    val year: Int?,
    
    @SerializedName("color")
    val color: String?,
    
    @SerializedName("vin")
    val vin: String?
)

