package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.data.dto.*
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getVehicles(page: Int = 1, pageSize: Int = 20): Flow<Result<List<VehicleDto>>>
    fun getVehicleById(vehicleId: String): Flow<Result<VehicleDto>>
    fun createVehicle(vehicle: CreateVehicleDto): Flow<Result<VehicleDto>>
    fun updateVehicle(vehicleId: String, vehicle: UpdateVehicleDto): Flow<Result<VehicleDto>>
    fun deleteVehicle(vehicleId: String): Flow<Result<String>>
    fun getVehiclesByBrand(brand: String): Flow<Result<List<VehicleDto>>>
    fun getVehiclesByModel(model: String): Flow<Result<List<VehicleDto>>>
    fun searchVehicles(query: String): Flow<Result<List<VehicleDto>>>
    fun getVehicleBrands(): Flow<Result<List<String>>>
    fun getVehicleModels(): Flow<Result<List<String>>>
    fun getVehiclesByYear(year: Int): Flow<Result<List<VehicleDto>>>
    fun getVehiclesByType(type: String): Flow<Result<List<VehicleDto>>>
}



