package com.example.decalxeandroid.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.repository.CustomerRepository

class AddCustomerViewModelFactory(
    private val customerRepository: CustomerRepository
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCustomerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddCustomerViewModel(customerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
