package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.domain.model.Employee
import com.example.decalxeandroid.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {
    fun getEmployees(page: Int = 1, pageSize: Int = 20): Flow<Result<List<Employee>>>
    fun getEmployeeById(employeeId: String): Flow<Result<Employee>>
    fun createEmployee(employee: Employee): Flow<Result<Employee>>
    fun updateEmployee(employeeId: String, employee: Employee): Flow<Result<Employee>>
    fun deleteEmployee(employeeId: String): Flow<Result<Boolean>>
    fun getEmployeesByRole(role: String): Flow<Result<List<Employee>>>
    fun searchEmployees(query: String): Flow<Result<List<Employee>>>
    fun getEmployeeByPhone(phoneNumber: String): Flow<Result<Employee>>
}



