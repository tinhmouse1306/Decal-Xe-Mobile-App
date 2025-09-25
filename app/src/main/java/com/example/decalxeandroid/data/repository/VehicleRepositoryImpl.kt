package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.data.remote.VehicleApiService
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VehicleRepositoryImpl(
    private val vehicleApiService: VehicleApiService
) : VehicleRepository {
    
    override fun getVehicles(page: Int, pageSize: Int): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.getVehicles(page, pageSize)
            emit(Result.Success(vehicles))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch vehicles: ${e.message}"))
        }
    }
    
    override fun getVehicleById(vehicleId: String): Flow<Result<VehicleDto>> = flow {
        try {
            val vehicle = vehicleApiService.getVehicleById(vehicleId)
            emit(Result.Success(vehicle))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch vehicle: ${e.message}"))
        }
    }
    
    override fun createVehicle(vehicle: CreateVehicleDto): Flow<Result<VehicleDto>> = flow {
        try {
            val createdVehicle = vehicleApiService.createVehicle(vehicle)
            emit(Result.Success(createdVehicle))
        } catch (e: Exception) {
            emit(Result.Error("Failed to create vehicle: ${e.message}"))
        }
    }
    
    override fun updateVehicle(vehicleId: String, vehicle: UpdateVehicleDto): Flow<Result<VehicleDto>> = flow {
        try {
            val updatedVehicle = vehicleApiService.updateVehicle(vehicleId, vehicle)
            emit(Result.Success(updatedVehicle))
        } catch (e: Exception) {
            emit(Result.Error("Failed to update vehicle: ${e.message}"))
        }
    }
    
    override fun deleteVehicle(vehicleId: String): Flow<Result<String>> = flow {
        try {
            val message = vehicleApiService.deleteVehicle(vehicleId)
            emit(Result.Success(message))
        } catch (e: Exception) {
            emit(Result.Error("Failed to delete vehicle: ${e.message}"))
        }
    }
    
    override fun getVehiclesByBrand(brand: String): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.getVehiclesByBrand(brand)
            emit(Result.Success(vehicles))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch vehicles by brand: ${e.message}"))
        }
    }
    
    override fun getVehiclesByModel(model: String): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.getVehiclesByModel(model)
            emit(Result.Success(vehicles))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch vehicles by model: ${e.message}"))
        }
    }
    
    override fun searchVehicles(query: String): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.searchVehicles(query)
            emit(Result.Success(vehicles))
        } catch (e: Exception) {
            emit(Result.Error("Failed to search vehicles: ${e.message}"))
        }
    }
    
    override fun getVehicleBrands(): Flow<Result<List<String>>> = flow {
        try {
            val brands = vehicleApiService.getVehicleBrands()
            emit(Result.Success(brands))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch vehicle brands: ${e.message}"))
        }
    }
    
    override fun getVehicleModels(): Flow<Result<List<VehicleModelDto>>> = flow {
        try {
            val models = vehicleApiService.getVehicleModels()
            emit(Result.Success(models))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch vehicle models: ${e.message}"))
        }
    }
    
    override fun getVehiclesByYear(year: Int): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.getVehiclesByYear(year)
            emit(Result.Success(vehicles))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch vehicles by year: ${e.message}"))
        }
    }
    
    override fun getVehiclesByType(type: String): Flow<Result<List<VehicleDto>>> = flow {
        try {
            val vehicles = vehicleApiService.getVehiclesByType(type)
            emit(Result.Success(vehicles))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch vehicles by type: ${e.message}"))
        }
    }
}



