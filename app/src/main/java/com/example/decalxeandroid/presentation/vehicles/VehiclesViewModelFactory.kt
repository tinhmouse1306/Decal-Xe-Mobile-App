package com.example.decalxeandroid.presentation.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.di.AppContainer

class VehiclesViewModelFactory : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehiclesViewModel::class.java)) {
            return VehiclesViewModel(AppContainer.customerVehicleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
