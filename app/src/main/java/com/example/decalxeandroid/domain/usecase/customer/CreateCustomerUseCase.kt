package com.example.decalxeandroid.domain.usecase.customer

import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow

class CreateCustomerUseCase(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        phoneNumber: String,
        email: String?,
        address: String?
    ): Flow<Result<Customer>> {
        return customerRepository.createCustomer(firstName, lastName, phoneNumber, email, address)
    }
}
