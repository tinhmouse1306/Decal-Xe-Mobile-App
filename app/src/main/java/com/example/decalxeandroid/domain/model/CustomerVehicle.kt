package com.example.decalxeandroid.domain.model

data class CustomerVehicle(
    val vehicleID: String,
    val chassisNumber: String,
    val licensePlate: String,
    val color: String,
    val year: Int,
    val initialKM: Double,
    val customerID: String,
    val customerFullName: String,
    val modelID: String,
    val vehicleModelName: String?,
    val vehicleBrandName: String?
)
