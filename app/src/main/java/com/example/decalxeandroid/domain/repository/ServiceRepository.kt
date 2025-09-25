package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.domain.model.Service
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    // Services
    fun getServices(page: Int, pageSize: Int): Flow<Result<List<Service>>>
    fun getServiceById(serviceId: String): Flow<Result<Service>>
    fun createService(service: Service): Flow<Result<Service>>
    fun updateService(serviceId: String, service: Service): Flow<Result<Service>>
    fun deleteService(serviceId: String): Flow<Result<String>>
    fun searchServices(query: String): Flow<Result<List<Service>>>
    fun getServicesByCategory(category: String): Flow<Result<List<Service>>>
    fun getServiceCategories(): Flow<Result<List<String>>>
    fun getServicesByPriceRange(minPrice: Double, maxPrice: Double): Flow<Result<List<Service>>>
}
