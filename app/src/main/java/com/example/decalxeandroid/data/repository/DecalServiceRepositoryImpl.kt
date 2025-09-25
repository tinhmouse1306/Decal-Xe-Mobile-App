package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.api.DecalServiceApi
import com.example.decalxeandroid.data.dto.DecalServiceDto
import com.example.decalxeandroid.data.mapper.DecalServiceMapper
import com.example.decalxeandroid.domain.model.DecalService
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.DecalServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class DecalServiceRepositoryImpl(
    private val api: DecalServiceApi,
    private val mapper: DecalServiceMapper
) : DecalServiceRepository {

    override fun getServices(page: Int, pageSize: Int): Flow<Result<List<DecalService>>> = flow {
        try {
            val response = api.getDecalServices()
            if (response.isSuccessful) {
                val services = response.body()?.map { mapper.toDomain(it) } ?: emptyList()
                println("DecalServices API: Successfully loaded ${services.size} services")
                emit(Result.Success(services))
            } else {
                val errorBody = response.errorBody()?.string()
                println("DecalServices API Error: ${response.code()} - $errorBody")
                emit(Result.Error("Failed to fetch services: ${response.code()}"))
            }
        } catch (e: Exception) {
            println("DecalServices API Exception: ${e.message}")
            e.printStackTrace()
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getServiceById(serviceId: String): Flow<Result<DecalService>> = flow {
        try {
            val response = api.getDecalServiceById(serviceId)
            if (response.isSuccessful) {
                val service = response.body()?.let { mapper.toDomain(it) }
                if (service != null) {
                    emit(Result.Success(service))
                } else {
                    emit(Result.Error("Service not found"))
                }
            } else {
                emit(Result.Error("Failed to fetch service: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun createService(service: DecalService): Flow<Result<DecalService>> = flow {
        try {
            val createDto = com.example.decalxeandroid.data.dto.CreateDecalServiceDto(
                serviceName = service.serviceName,
                description = service.description ?: "",
                price = service.price,
                standardWorkUnits = service.standardWorkUnits ?: 0,
                decalTemplateID = service.decalTemplateId ?: ""
            )
            val response = api.createDecalService(createDto)
            if (response.isSuccessful) {
                val createdService = response.body()?.let { mapper.toDomain(it) }
                if (createdService != null) {
                    emit(Result.Success(createdService))
                } else {
                    emit(Result.Error("Failed to create service"))
                }
            } else {
                emit(Result.Error("Failed to create service: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun updateService(serviceId: String, service: DecalService): Flow<Result<DecalService>> = flow {
        try {
            val updateDto = com.example.decalxeandroid.data.dto.UpdateDecalServiceDto(
                serviceName = service.serviceName,
                description = service.description,
                price = service.price,
                standardWorkUnits = service.standardWorkUnits,
                decalTemplateID = service.decalTemplateId
            )
            val response = api.updateDecalService(serviceId, updateDto)
            if (response.isSuccessful) {
                val updatedService = response.body()?.let { mapper.toDomain(it) }
                if (updatedService != null) {
                    emit(Result.Success(updatedService))
                } else {
                    emit(Result.Error("Failed to update service"))
                }
            } else {
                emit(Result.Error("Failed to update service: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun deleteService(serviceId: String): Flow<Result<Boolean>> = flow {
        try {
            val response = api.deleteDecalService(serviceId)
            emit(Result.Success(response.isSuccessful))
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun searchServices(query: String): Flow<Result<List<DecalService>>> = flow {
        try {
            val result = getServices().first()
            when (result) {
                is Result.Success -> {
                    val filteredServices = result.data.filter { 
                        it.serviceName.contains(query, ignoreCase = true) ||
                        it.description?.contains(query, ignoreCase = true) == true
                    }
                    emit(Result.Success(filteredServices))
                }
                is Result.Error -> emit(result)
                is Result.Loading -> emit(result)
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getServicesByType(serviceType: String): Flow<Result<List<DecalService>>> = flow {
        try {
            val result = getServices().first()
            when (result) {
                is Result.Success -> {
                    val filteredServices = result.data.filter { 
                        it.decalTypeName?.equals(serviceType, ignoreCase = true) == true 
                    }
                    emit(Result.Success(filteredServices))
                }
                is Result.Error -> emit(result)
                is Result.Loading -> emit(result)
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }
}
