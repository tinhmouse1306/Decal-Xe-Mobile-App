package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.UserDataDto
import com.example.decalxeandroid.domain.model.User
import com.example.decalxeandroid.domain.model.UserRole

object AuthMapper {
    fun mapUserDataDtoToUser(dto: UserDataDto): User {
        return User(
                accountId = dto.accountID,
                username = dto.username,
                email = dto.email ?: "",
                fullName = dto.accountRoleName, // Using accountRoleName as fullName for now
                phoneNumber = null, // Not available in current DTO
                role = mapRoleStringToUserRole(dto.role),
                isActive = dto.isActive,
                createdAt = "", // Not available in current DTO
                lastLoginAt = null, // Not available in current DTO
                employeeId = dto.employeeID
        )
    }

    private fun mapRoleStringToUserRole(roleString: String): UserRole {
        return when (roleString.uppercase()) {
            "ADMIN" -> UserRole.ADMIN
            "MANAGER" -> UserRole.MANAGER
            "SALES" -> UserRole.SALES
            "TECHNICIAN" -> UserRole.TECHNICIAN
            "CUSTOMER" -> UserRole.CUSTOMER
            else -> UserRole.CUSTOMER
        }
    }

    fun mapUserRoleToString(userRole: UserRole): String {
        return when (userRole) {
            UserRole.ADMIN -> "Admin"
            UserRole.MANAGER -> "Manager"
            UserRole.SALES -> "Sales"
            UserRole.TECHNICIAN -> "Technician"
            UserRole.CUSTOMER -> "Customer"
        }
    }
}
