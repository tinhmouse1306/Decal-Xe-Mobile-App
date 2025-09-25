package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.api.EmployeeApi
import com.example.decalxeandroid.data.dto.EmployeeDto
import com.example.decalxeandroid.data.mapper.EmployeeMapper
import com.example.decalxeandroid.domain.model.Employee
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class EmployeeRepositoryImpl(
    private val api: EmployeeApi,
    private val mapper: EmployeeMapper
) : EmployeeRepository {

    override fun getEmployees(page: Int, pageSize: Int): Flow<Result<List<Employee>>> = flow {
        try {
            val response = api.getEmployees()
            if (response.isSuccessful) {
                val employees = response.body()?.map { mapper.toDomain(it) } ?: emptyList()
                emit(Result.Success(employees))
            } else {
                emit(Result.Error("Failed to fetch employees: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getEmployeeById(employeeId: String): Flow<Result<Employee>> = flow {
        try {
            val response = api.getEmployeeById(employeeId)
            if (response.isSuccessful) {
                val employee = response.body()?.let { mapper.toDomain(it) }
                if (employee != null) {
                    emit(Result.Success(employee))
                } else {
                    emit(Result.Error("Employee not found"))
                }
            } else {
                emit(Result.Error("Failed to fetch employee: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun createEmployee(employee: Employee): Flow<Result<Employee>> = flow {
        try {
            val createDto = com.example.decalxeandroid.data.dto.CreateEmployeeDto(
                firstName = employee.firstName,
                lastName = employee.lastName,
                phoneNumber = employee.phoneNumber ?: "",
                email = employee.email ?: "",
                address = employee.address ?: "",
                storeID = employee.storeId ?: "",
                accountID = employee.accountId ?: ""
            )
            val response = api.createEmployee(createDto)
            if (response.isSuccessful) {
                val createdEmployee = response.body()?.let { mapper.toDomain(it) }
                if (createdEmployee != null) {
                    emit(Result.Success(createdEmployee))
                } else {
                    emit(Result.Error("Failed to create employee"))
                }
            } else {
                emit(Result.Error("Failed to create employee: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun updateEmployee(employeeId: String, employee: Employee): Flow<Result<Employee>> = flow {
        try {
            val updateDto = com.example.decalxeandroid.data.dto.UpdateEmployeeDto(
                firstName = employee.firstName,
                lastName = employee.lastName,
                phoneNumber = employee.phoneNumber,
                email = employee.email,
                address = employee.address,
                storeID = employee.storeId,
                accountID = employee.accountId
            )
            val response = api.updateEmployee(employeeId, updateDto)
            if (response.isSuccessful) {
                val updatedEmployee = response.body()?.let { mapper.toDomain(it) }
                if (updatedEmployee != null) {
                    emit(Result.Success(updatedEmployee))
                } else {
                    emit(Result.Error("Failed to update employee"))
                }
            } else {
                emit(Result.Error("Failed to update employee: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun deleteEmployee(employeeId: String): Flow<Result<Boolean>> = flow {
        try {
            val response = api.deleteEmployee(employeeId)
            emit(Result.Success(response.isSuccessful))
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getEmployeesByRole(role: String): Flow<Result<List<Employee>>> = flow {
        try {
            val result = getEmployees().first()
            when (result) {
                is Result.Success -> {
                    val filteredEmployees = result.data.filter { 
                        it.accountRoleName?.equals(role, ignoreCase = true) == true 
                    }
                    emit(Result.Success(filteredEmployees))
                }
                is Result.Error -> emit(result)
                is Result.Loading -> emit(result)
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun searchEmployees(query: String): Flow<Result<List<Employee>>> = flow {
        try {
            val result = getEmployees().first()
            when (result) {
                is Result.Success -> {
                    val filteredEmployees = result.data.filter { 
                        it.firstName.contains(query, ignoreCase = true) ||
                        it.lastName.contains(query, ignoreCase = true) ||
                        it.email?.contains(query, ignoreCase = true) == true ||
                        it.phoneNumber?.contains(query, ignoreCase = true) == true
                    }
                    emit(Result.Success(filteredEmployees))
                }
                is Result.Error -> emit(result)
                is Result.Loading -> emit(result)
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }

    override fun getEmployeeByPhone(phoneNumber: String): Flow<Result<Employee>> = flow {
        try {
            val result = getEmployees().first()
            when (result) {
                is Result.Success -> {
                    val employee = result.data.find { it.phoneNumber == phoneNumber }
                    if (employee != null) {
                        emit(Result.Success(employee))
                    } else {
                        emit(Result.Error("Employee not found"))
                    }
                }
                is Result.Error -> emit(result)
                is Result.Loading -> emit(result)
            }
        } catch (e: Exception) {
            emit(Result.Error("Network error: ${e.message}"))
        }
    }
}
