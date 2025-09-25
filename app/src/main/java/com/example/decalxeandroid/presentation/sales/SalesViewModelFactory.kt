package com.example.decalxeandroid.presentation.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.di.AppContainer

class SalesViewModelFactory : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesViewModel::class.java)) {
            return SalesViewModel(
                getSalesEmployeeInfoUseCase = com.example.decalxeandroid.domain.usecase.sales.GetSalesEmployeeInfoUseCase(AppContainer.employeeRepository),
                getSalesStoreOrdersUseCase = com.example.decalxeandroid.domain.usecase.sales.GetSalesStoreOrdersUseCase(AppContainer.orderRepository),
                createOrderUseCase = com.example.decalxeandroid.domain.usecase.sales.CreateOrderUseCase(AppContainer.orderRepository),
                getDecalServicesUseCase = com.example.decalxeandroid.domain.usecase.sales.GetDecalServicesUseCase(AppContainer.decalServiceRepository),
                getCustomersUseCase = com.example.decalxeandroid.domain.usecase.customer.GetCustomersUseCase(AppContainer.customerRepository),
                createCustomerUseCase = com.example.decalxeandroid.domain.usecase.customer.CreateCustomerUseCase(AppContainer.customerRepository),
                getCustomerVehiclesUseCase = com.example.decalxeandroid.domain.usecase.vehicle.GetCustomerVehiclesUseCase(AppContainer.customerVehicleRepository),
                createCustomerVehicleUseCase = com.example.decalxeandroid.domain.usecase.vehicle.CreateCustomerVehicleUseCase(AppContainer.customerVehicleRepository)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
