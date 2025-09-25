package com.example.decalxeandroid.data.repository

import android.util.Log
import com.example.decalxeandroid.data.api.CustomerVehicleApi
import com.example.decalxeandroid.data.dto.CustomerVehicleDto
import com.example.decalxeandroid.data.dto.UpdateCustomerVehicleDto
import com.example.decalxeandroid.data.mapper.CustomerVehicleMapper
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CustomerVehicleRepositoryImpl(
    private val api: CustomerVehicleApi,
    private val mapper: CustomerVehicleMapper
) : CustomerVehicleRepository {
    
    companion object {
        private const val TAG = "CustomerVehicleRepo"
    }

    override fun getVehicles(): Flow<Result<List<CustomerVehicle>>> = flow {
        try {
            val response = api.getCustomerVehicles()
            if (response.isSuccessful) {
                val vehicles = response.body()?.map { mapper.toDomain(it) } ?: emptyList()
                emit(Result.Success(vehicles))
            } else {
                val errorBody = response.errorBody()?.string()
                emit(Result.Error("Failed to fetch vehicles: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getVehicleById(vehicleId: String): Flow<Result<CustomerVehicle>> = flow {
        try {
            Log.d(TAG, "Fetching vehicle with ID: $vehicleId")
            val response = api.getCustomerVehicleById(vehicleId)
            
            Log.d(TAG, "API Response - Code: ${response.code()}, Success: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d(TAG, "Response body: $responseBody")
                
                val vehicle = responseBody?.let { mapper.toDomain(it) }
                if (vehicle != null) {
                    Log.d(TAG, "Successfully mapped vehicle: ${vehicle.vehicleID}")
                    emit(Result.Success(vehicle))
                } else {
                    Log.e(TAG, "Vehicle not found or mapping failed")
                    emit(Result.Error("Vehicle not found"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "API Error - Code: ${response.code()}, Error: $errorBody")
                emit(Result.Error("Failed to fetch vehicle: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error", e)
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getVehiclesByCustomerId(customerId: String): Flow<Result<List<CustomerVehicle>>> = flow {
        try {
            Log.d(TAG, "Fetching vehicles for customer ID: $customerId")
            val response = api.getCustomerVehiclesByCustomerId(customerId)
            
            Log.d(TAG, "API Response - Code: ${response.code()}, Success: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d(TAG, "Response body: $responseBody")
                
                val vehicles = responseBody?.map { mapper.toDomain(it) } ?: emptyList()
                Log.d(TAG, "Successfully mapped ${vehicles.size} vehicles for customer $customerId")
                emit(Result.Success(vehicles))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "API Error - Code: ${response.code()}, Error: $errorBody")
                emit(Result.Error("Failed to fetch vehicles: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error", e)
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun createVehicle(vehicle: CustomerVehicle): Flow<Result<CustomerVehicle>> = flow {
        try {
            val dto = mapper.toCreateDto(vehicle)
            val response = api.createCustomerVehicle(dto)
            if (response.isSuccessful) {
                val createdVehicle = response.body()?.let { mapper.toDomain(it) }
                if (createdVehicle != null) {
                    emit(Result.Success(createdVehicle))
                } else {
                    emit(Result.Error("Failed to create vehicle: Invalid response"))
                }
            } else {
                emit(Result.Error("Failed to create vehicle: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun updateVehicle(vehicleId: String, updateDto: UpdateCustomerVehicleDto): Flow<Result<CustomerVehicle>> = flow {
        try {
            val response = api.updateCustomerVehicle(vehicleId, updateDto)
            if (response.isSuccessful) {
                val updatedVehicle = response.body()?.let { mapper.toDomain(it) }
                if (updatedVehicle != null) {
                    emit(Result.Success(updatedVehicle))
                } else {
                    emit(Result.Error("Failed to update vehicle: Invalid response"))
                }
            } else {
                emit(Result.Error("Failed to update vehicle: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun deleteVehicle(vehicleId: String): Flow<Result<Boolean>> = flow {
        try {
            val response = api.deleteCustomerVehicle(vehicleId)
            if (response.isSuccessful) {
                emit(Result.Success(true))
            } else {
                emit(Result.Error("Failed to delete vehicle: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }
}
