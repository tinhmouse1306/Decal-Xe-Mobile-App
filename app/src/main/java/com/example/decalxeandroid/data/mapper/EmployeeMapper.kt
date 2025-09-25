package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.EmployeeDto
import com.example.decalxeandroid.domain.model.Employee

class EmployeeMapper {
    
    fun toDomain(dto: EmployeeDto): Employee {
        return Employee(
            employeeId = dto.employeeID,
            firstName = dto.firstName,
            lastName = dto.lastName,
            phoneNumber = dto.phoneNumber,
            email = dto.email,
            address = dto.address,
            storeId = dto.storeID,
            storeName = dto.storeName,
            accountId = dto.accountID,
            accountUsername = dto.accountUsername,
            accountRoleName = dto.accountRoleName,
            isActive = dto.isActive
        )
    }
    
    fun toDto(employee: Employee): EmployeeDto {
        return EmployeeDto(
            employeeID = employee.employeeId,
            firstName = employee.firstName,
            lastName = employee.lastName,
            phoneNumber = employee.phoneNumber ?: "",
            email = employee.email ?: "",
            address = employee.address ?: "",
            storeID = employee.storeId ?: "",
            storeName = employee.storeName ?: "",
            accountID = employee.accountId ?: "",
            accountUsername = employee.accountUsername ?: "",
            accountRoleName = employee.accountRoleName ?: "",
            isActive = employee.isActive,
            roles = emptyList(),
            adminDetail = null,
            managerDetail = null,
            salesPersonDetail = null,
            designerDetail = null,
            technicianDetail = null
        )
    }
}

