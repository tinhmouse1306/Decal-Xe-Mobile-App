package com.example.decalxeandroid.domain.usecase.sales

import com.example.decalxeandroid.domain.model.DecalService
import com.example.decalxeandroid.domain.repository.DecalServiceRepository
import com.example.decalxeandroid.domain.model.Result
import kotlinx.coroutines.flow.Flow

class GetDecalServicesUseCase(
    private val decalServiceRepository: DecalServiceRepository
) {
    suspend operator fun invoke(): Flow<Result<List<DecalService>>> {
        return decalServiceRepository.getServices()
    }
    
    suspend fun getServicesByType(serviceType: String): Flow<Result<List<DecalService>>> {
        return decalServiceRepository.getServicesByType(serviceType)
    }
}
