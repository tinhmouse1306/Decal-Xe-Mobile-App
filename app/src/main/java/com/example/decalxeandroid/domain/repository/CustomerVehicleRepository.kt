package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.data.dto.UpdateCustomerVehicleDto
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface CustomerVehicleRepository {
    fun getVehicles(): Flow<Result<List<CustomerVehicle>>>
    fun getVehicleById(vehicleId: String): Flow<Result<CustomerVehicle>>
    fun getVehiclesByCustomerId(customerId: String): Flow<Result<List<CustomerVehicle>>>
    fun createVehicle(vehicle: CustomerVehicle): Flow<Result<CustomerVehicle>>
    fun updateVehicle(vehicleId: String, updateDto: UpdateCustomerVehicleDto): Flow<Result<CustomerVehicle>>
    fun deleteVehicle(vehicleId: String): Flow<Result<Boolean>>
}
