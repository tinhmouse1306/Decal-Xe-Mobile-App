package com.example.decalxeandroid.domain.usecase.customer

import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow

class GetCustomersUseCase(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(): Flow<Result<List<Customer>>> {
        return customerRepository.getCustomers()
    }
}
