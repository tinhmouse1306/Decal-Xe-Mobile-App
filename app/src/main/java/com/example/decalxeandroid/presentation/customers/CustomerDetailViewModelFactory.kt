package com.example.decalxeandroid.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.repository.CustomerRepository
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository

class CustomerDetailViewModelFactory(
    private val customerRepository: CustomerRepository,
    private val customerVehicleRepository: CustomerVehicleRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerDetailViewModel(
                customerRepository = customerRepository,
                customerVehicleRepository = customerVehicleRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
