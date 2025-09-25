package com.example.decalxeandroid.presentation.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import com.example.decalxeandroid.domain.repository.CustomerRepository

class AddVehicleViewModelFactory(
    private val customerVehicleRepository: CustomerVehicleRepository,
    private val customerRepository: CustomerRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddVehicleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddVehicleViewModel(
                customerVehicleRepository,
                customerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
