package com.example.decalxeandroid.presentation.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository

class VehicleEditViewModelFactory(
    private val vehicleId: String,
    private val customerVehicleRepository: CustomerVehicleRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehicleEditViewModel::class.java)) {
            return VehicleEditViewModel(vehicleId, customerVehicleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
