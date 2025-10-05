package com.example.decalxeandroid.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.repository.CustomerRepository
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import com.example.decalxeandroid.domain.repository.DecalServiceRepository
import com.example.decalxeandroid.domain.repository.EmployeeRepository
import com.example.decalxeandroid.domain.repository.OrderDetailRepository
import com.example.decalxeandroid.domain.repository.OrderRepository

class CreateOrderViewModelFactory(
        private val orderRepository: OrderRepository,
        private val customerRepository: CustomerRepository,
        private val customerVehicleRepository: CustomerVehicleRepository,
        private val employeeRepository: EmployeeRepository,
        private val decalServiceRepository: DecalServiceRepository,
        private val orderDetailRepository: OrderDetailRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateOrderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateOrderViewModel(
                    orderRepository,
                    customerRepository,
                    customerVehicleRepository,
                    employeeRepository,
                    decalServiceRepository,
                    orderDetailRepository
            ) as
                    T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
