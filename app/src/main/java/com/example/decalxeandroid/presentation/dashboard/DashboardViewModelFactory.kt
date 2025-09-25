package com.example.decalxeandroid.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.di.AppContainer

class DashboardViewModelFactory : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(
                AppContainer.orderRepository,
                AppContainer.customerRepository,
                AppContainer.customerVehicleRepository,
                AppContainer.decalServiceRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
