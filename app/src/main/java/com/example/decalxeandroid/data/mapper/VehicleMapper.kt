package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.VehicleDto
import com.example.decalxeandroid.data.dto.CreateVehicleDto
import com.example.decalxeandroid.data.dto.UpdateVehicleDto
import com.example.decalxeandroid.domain.model.Vehicle

object VehicleMapper {
    fun mapVehicleDtoToVehicle(dto: VehicleDto): Vehicle {
        return Vehicle(
            id = dto.vehicleID,
            brand = dto.vehicleBrandName,
            model = dto.vehicleModelName,
            year = dto.year,
            type = "", // VehicleDto doesn't have type
            engineSize = null, // VehicleDto doesn't have engineSize
            fuelType = null, // VehicleDto doesn't have fuelType
            transmission = null, // VehicleDto doesn't have transmission
            color = dto.color,
            imageUrl = null, // VehicleDto doesn't have imageUrl
            isActive = true, // VehicleDto doesn't have isActive
            createdAt = "", // VehicleDto doesn't have createdAt
            updatedAt = "" // VehicleDto doesn't have updatedAt
        )
    }
    
    fun mapVehicleToCreateVehicleDto(vehicle: Vehicle): CreateVehicleDto {
        return CreateVehicleDto(
            licensePlate = "", // Vehicle doesn't have licensePlate in domain model
            vehicleModelID = "", // Vehicle doesn't have vehicleModelID in domain model
            year = vehicle.year,
            color = vehicle.color ?: "",
            vin = null // Vehicle doesn't have vin in domain model
        )
    }
    
    fun mapVehicleToUpdateVehicleDto(vehicle: Vehicle): UpdateVehicleDto {
        return UpdateVehicleDto(
            licensePlate = null, // Vehicle doesn't have licensePlate in domain model
            vehicleModelID = null, // Vehicle doesn't have vehicleModelID in domain model
            year = vehicle.year,
            color = vehicle.color,
            vin = null // Vehicle doesn't have vin in domain model
        )
    }
}

