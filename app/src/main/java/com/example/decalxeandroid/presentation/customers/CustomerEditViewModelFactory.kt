package com.example.decalxeandroid.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.repository.CustomerRepository

class CustomerEditViewModelFactory(
    private val customerId: String,
    private val customerRepository: CustomerRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerEditViewModel::class.java)) {
            return CustomerEditViewModel(customerId, customerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
