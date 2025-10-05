package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.CreateCustomerVehicleDto
import com.example.decalxeandroid.data.dto.CustomerVehicleDto
import com.example.decalxeandroid.data.dto.UpdateCustomerVehicleDto
import com.example.decalxeandroid.domain.model.CustomerVehicle

class CustomerVehicleMapper {

    fun toDomain(dto: CustomerVehicleDto): CustomerVehicle {
        return CustomerVehicle(
                vehicleID = dto.vehicleID,
                chassisNumber = dto.chassisNumber,
                licensePlate = dto.licensePlate ?: "",
                color = dto.color ?: "",
                year = dto.year ?: 0,
                initialKM = dto.initialKM ?: 0.0,
                customerID = dto.customerID,
                customerFullName = dto.customerFullName,
                modelID = dto.modelID,
                vehicleModelName = dto.vehicleModelName,
                vehicleBrandName = dto.vehicleBrandName
        )
    }

    fun toDto(customerVehicle: CustomerVehicle): CustomerVehicleDto {
        return CustomerVehicleDto(
                vehicleID = customerVehicle.vehicleID,
                chassisNumber = customerVehicle.chassisNumber,
                licensePlate = customerVehicle.licensePlate.takeIf { it.isNotEmpty() },
                color = customerVehicle.color.takeIf { it.isNotEmpty() },
                year = customerVehicle.year.takeIf { it > 0 },
                initialKM = customerVehicle.initialKM.takeIf { it > 0.0 },
                customerID = customerVehicle.customerID,
                customerFullName = customerVehicle.customerFullName,
                modelID = customerVehicle.modelID,
                vehicleModelName = customerVehicle.vehicleModelName,
                vehicleBrandName = customerVehicle.vehicleBrandName
        )
    }

    fun toCreateDto(customerVehicle: CustomerVehicle): CreateCustomerVehicleDto {
        return CreateCustomerVehicleDto(
                customerID = customerVehicle.customerID,
                chassisNumber = customerVehicle.chassisNumber,
                licensePlate = customerVehicle.licensePlate.takeIf { it.isNotEmpty() },
                color = customerVehicle.color.takeIf { it.isNotEmpty() },
                year = customerVehicle.year.takeIf { it > 0 },
                initialKM = customerVehicle.initialKM.takeIf { it > 0.0 },
                modelID = customerVehicle.modelID
        )
    }

    fun toUpdateDto(customerVehicle: CustomerVehicle): UpdateCustomerVehicleDto {
        return UpdateCustomerVehicleDto(
                chassisNumber = customerVehicle.chassisNumber,
                licensePlate = customerVehicle.licensePlate.takeIf { it.isNotEmpty() },
                color = customerVehicle.color.takeIf { it.isNotEmpty() },
                year = customerVehicle.year.takeIf { it > 0 },
                initialKM = customerVehicle.initialKM.takeIf { it > 0.0 },
                modelID = customerVehicle.modelID
        )
    }
}
