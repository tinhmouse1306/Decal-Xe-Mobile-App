package com.example.decalxeandroid.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.repository.CustomerRepository
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import com.example.decalxeandroid.domain.repository.OrderRepository

class CustomerDetailViewModelFactory(
    private val customerId: String,
    private val customerRepository: CustomerRepository,
    private val customerVehicleRepository: CustomerVehicleRepository,
    private val orderRepository: OrderRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerDetailViewModel(
                customerId = customerId,
                customerRepository = customerRepository,
                customerVehicleRepository = customerVehicleRepository,
                orderRepository = orderRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
