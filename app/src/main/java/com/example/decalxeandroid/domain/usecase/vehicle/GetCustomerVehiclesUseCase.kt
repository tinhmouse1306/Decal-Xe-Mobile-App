package com.example.decalxeandroid.domain.usecase.vehicle

import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import kotlinx.coroutines.flow.Flow

class GetCustomerVehiclesUseCase(
    private val customerVehicleRepository: CustomerVehicleRepository
) {
    suspend operator fun invoke(): Flow<Result<List<CustomerVehicle>>> {
        return customerVehicleRepository.getVehicles()
    }
    
    suspend operator fun invoke(customerId: String): Flow<Result<List<CustomerVehicle>>> {
        return customerVehicleRepository.getVehiclesByCustomerId(customerId)
    }
}
