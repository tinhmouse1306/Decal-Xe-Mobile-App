package com.example.decalxeandroid.domain.usecase.vehicle

import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import kotlinx.coroutines.flow.Flow

class CreateCustomerVehicleUseCase(
    private val customerVehicleRepository: CustomerVehicleRepository
) {
    suspend operator fun invoke(vehicle: CustomerVehicle): Flow<Result<CustomerVehicle>> {
        return customerVehicleRepository.createVehicle(vehicle)
    }
}
