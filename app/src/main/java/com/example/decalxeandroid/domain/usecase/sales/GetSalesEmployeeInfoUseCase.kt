package com.example.decalxeandroid.domain.usecase.sales

import com.example.decalxeandroid.domain.model.Employee
import com.example.decalxeandroid.domain.repository.EmployeeRepository
import com.example.decalxeandroid.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class GetSalesEmployeeInfoUseCase(
    private val employeeRepository: EmployeeRepository
) {
    suspend operator fun invoke(employeeId: String): Flow<Result<Employee>> {
        return employeeRepository.getEmployeeById(employeeId)
    }
    
    suspend fun getStoreIdForEmployee(employeeId: String): String? {
        return try {
            val result = employeeRepository.getEmployeeById(employeeId).first()
            when (result) {
                is Result.Success -> result.data.storeId
                is Result.Error -> null
                is Result.Loading -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}