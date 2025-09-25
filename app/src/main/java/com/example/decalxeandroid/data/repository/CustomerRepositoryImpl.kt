package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.api.CustomerApi
import com.example.decalxeandroid.data.dto.CreateCustomerDto
import com.example.decalxeandroid.data.dto.UpdateCustomerDto
import com.example.decalxeandroid.data.mapper.CustomerMapper
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CustomerRepositoryImpl(
    private val api: CustomerApi,
    private val mapper: CustomerMapper
) : CustomerRepository {
    
    override fun getCustomers(): Flow<Result<List<Customer>>> = flow {
        try {
            val response = api.getCustomers()
            if (response.isSuccessful) {
                val customers = response.body()?.map { mapper.toDomain(it) } ?: emptyList()
                emit(Result.Success(customers))
            } else {
                emit(Result.Error("Failed to fetch customers: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }
    
    override fun getCustomerById(customerId: String): Flow<Result<Customer>> = flow {
        try {
            val response = api.getCustomerById(customerId)
            if (response.isSuccessful) {
                val customer = response.body()?.let { mapper.toDomain(it) }
                if (customer != null) {
                    emit(Result.Success(customer))
                } else {
                    emit(Result.Error("Customer not found"))
                }
            } else {
                emit(Result.Error("Failed to fetch customer: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }
    
    override fun createCustomer(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        email: String?,
        address: String?
    ): Flow<Result<Customer>> = flow {
        try {
            val createDto = CreateCustomerDto(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                email = email,
                address = address,
                accountID = null
            )
            val response = api.createCustomer(createDto)
            if (response.isSuccessful) {
                val customer = response.body()?.let { mapper.toDomain(it) }
                if (customer != null) {
                    emit(Result.Success(customer))
                } else {
                    emit(Result.Error("Failed to create customer: Invalid response"))
                }
            } else {
                emit(Result.Error("Failed to create customer: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }
    
    override fun updateCustomer(customerId: String, customer: Customer): Flow<Result<Customer>> = flow {
        try {
            val updateDto = mapper.toUpdateDto(customer)
            val response = api.updateCustomer(customerId, updateDto)
            if (response.isSuccessful) {
                val updatedCustomer = response.body()?.let { mapper.toDomain(it) }
                if (updatedCustomer != null) {
                    emit(Result.Success(updatedCustomer))
                } else {
                    emit(Result.Error("Failed to update customer: Invalid response"))
                }
            } else {
                emit(Result.Error("Failed to update customer: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }
    
    override fun deleteCustomer(customerId: String): Flow<Result<Boolean>> = flow {
        try {
            val response = api.deleteCustomer(customerId)
            if (response.isSuccessful) {
                emit(Result.Success(true))
            } else {
                emit(Result.Error("Failed to delete customer: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }
}
