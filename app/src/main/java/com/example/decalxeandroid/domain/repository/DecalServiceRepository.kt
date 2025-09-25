package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.domain.model.DecalService
import com.example.decalxeandroid.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface DecalServiceRepository {
    fun getServices(page: Int = 1, pageSize: Int = 20): Flow<Result<List<DecalService>>>
    fun getServiceById(serviceId: String): Flow<Result<DecalService>>
    fun createService(service: DecalService): Flow<Result<DecalService>>
    fun updateService(serviceId: String, service: DecalService): Flow<Result<DecalService>>
    fun deleteService(serviceId: String): Flow<Result<Boolean>>
    fun searchServices(query: String): Flow<Result<List<DecalService>>>
    fun getServicesByType(serviceType: String): Flow<Result<List<DecalService>>>
}
