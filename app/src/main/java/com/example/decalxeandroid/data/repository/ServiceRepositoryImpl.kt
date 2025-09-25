package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.data.mapper.ServiceMapper
import com.example.decalxeandroid.data.remote.ServiceApiService
import com.example.decalxeandroid.domain.model.Service
import com.example.decalxeandroid.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ServiceRepositoryImpl(
    private val serviceApiService: ServiceApiService
) : ServiceRepository {
    
    override fun getServices(page: Int, pageSize: Int): Flow<Result<List<Service>>> = flow {
        try {
            val serviceDtos = serviceApiService.getServices(page, pageSize)
            val services = serviceDtos.map { ServiceMapper.mapServiceDtoToService(it) }
            emit(Result.success(services))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getServiceById(serviceId: String): Flow<Result<Service>> = flow {
        try {
            val serviceDto = serviceApiService.getServiceById(serviceId)
            val service = ServiceMapper.mapServiceDtoToService(serviceDto)
            emit(Result.success(service))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun createService(service: Service): Flow<Result<Service>> = flow {
        try {
            val createServiceDto = ServiceMapper.mapServiceToCreateServiceDto(service)
            val createdServiceDto = serviceApiService.createService(createServiceDto)
            val createdService = ServiceMapper.mapServiceDtoToService(createdServiceDto)
            emit(Result.success(createdService))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun updateService(serviceId: String, service: Service): Flow<Result<Service>> = flow {
        try {
            val updateServiceDto = ServiceMapper.mapServiceToUpdateServiceDto(service)
            val updatedServiceDto = serviceApiService.updateService(serviceId, updateServiceDto)
            val updatedService = ServiceMapper.mapServiceDtoToService(updatedServiceDto)
            emit(Result.success(updatedService))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun deleteService(serviceId: String): Flow<Result<String>> = flow {
        try {
            val message = serviceApiService.deleteService(serviceId)
            emit(Result.success(message))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun searchServices(query: String): Flow<Result<List<Service>>> = flow {
        try {
            val serviceDtos = serviceApiService.searchServices(query)
            val services = serviceDtos.map { ServiceMapper.mapServiceDtoToService(it) }
            emit(Result.success(services))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getServicesByCategory(category: String): Flow<Result<List<Service>>> = flow {
        try {
            val serviceDtos = serviceApiService.getServicesByCategory(category)
            val services = serviceDtos.map { ServiceMapper.mapServiceDtoToService(it) }
            emit(Result.success(services))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getServiceCategories(): Flow<Result<List<String>>> = flow {
        try {
            val categories = serviceApiService.getServiceCategories()
            emit(Result.success(categories))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getServicesByPriceRange(minPrice: Double, maxPrice: Double): Flow<Result<List<Service>>> = flow {
        try {
            val serviceDtos = serviceApiService.getServicesByPriceRange(minPrice, maxPrice)
            val services = serviceDtos.map { ServiceMapper.mapServiceDtoToService(it) }
            emit(Result.success(services))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}



