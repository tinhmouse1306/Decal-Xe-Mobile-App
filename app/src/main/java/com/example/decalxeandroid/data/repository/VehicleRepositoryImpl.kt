package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.data.remote.VehicleApiService
import com.example.decalxeandroid.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VehicleRepositoryImpl(
    private val vehicleApiService: VehicleApiService
) : VehicleRepository {
    
    override fun getVehicles(page: Int, pageSize: Int): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.getVehicles(page, pageSize)
            emit(Result.success(vehicles))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getVehicleById(vehicleId: String): Flow<Result<VehicleDto>> = flow {
        try {
            val vehicle = vehicleApiService.getVehicleById(vehicleId)
            emit(Result.success(vehicle))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun createVehicle(vehicle: CreateVehicleDto): Flow<Result<VehicleDto>> = flow {
        try {
            val createdVehicle = vehicleApiService.createVehicle(vehicle)
            emit(Result.success(createdVehicle))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun updateVehicle(vehicleId: String, vehicle: UpdateVehicleDto): Flow<Result<VehicleDto>> = flow {
        try {
            val updatedVehicle = vehicleApiService.updateVehicle(vehicleId, vehicle)
            emit(Result.success(updatedVehicle))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun deleteVehicle(vehicleId: String): Flow<Result<String>> = flow {
        try {
            val message = vehicleApiService.deleteVehicle(vehicleId)
            emit(Result.success(message))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getVehiclesByBrand(brand: String): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.getVehiclesByBrand(brand)
            emit(Result.success(vehicles))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getVehiclesByModel(model: String): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.getVehiclesByModel(model)
            emit(Result.success(vehicles))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun searchVehicles(query: String): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.searchVehicles(query)
            emit(Result.success(vehicles))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getVehicleBrands(): Flow<Result<List<String>>> = flow {
        try {
            val brands = vehicleApiService.getVehicleBrands()
            emit(Result.success(brands))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getVehicleModels(): Flow<Result<List<String>>> = flow {
        try {
            val models = vehicleApiService.getVehicleModels()
            emit(Result.success(models))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getVehiclesByYear(year: Int): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.getVehiclesByYear(year)
            emit(Result.success(vehicles))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getVehiclesByType(type: String): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.getVehiclesByType(type)
            emit(Result.success(vehicles))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}



