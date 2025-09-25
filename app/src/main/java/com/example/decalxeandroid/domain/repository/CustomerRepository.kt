package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getCustomers(): Flow<Result<List<Customer>>>
    fun getCustomerById(customerId: String): Flow<Result<Customer>>
    fun createCustomer(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        email: String?,
        address: String?
    ): Flow<Result<Customer>>
    fun updateCustomer(customerId: String, customer: Customer): Flow<Result<Customer>>
    fun deleteCustomer(customerId: String): Flow<Result<Boolean>>
}
