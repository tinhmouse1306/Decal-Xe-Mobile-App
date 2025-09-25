package com.example.decalxeandroid.domain.model

data class EmployeeInfo(
    val employeeId: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val phoneNumber: String?,
    val email: String?,
    val storeId: String?,
    val storeName: String?,
    val role: UserRole
) {
    companion object {
        fun fromEmployee(employee: Employee): EmployeeInfo {
            return EmployeeInfo(
                employeeId = employee.employeeId,
                firstName = employee.firstName,
                lastName = employee.lastName,
                fullName = "${employee.firstName} ${employee.lastName}",
                phoneNumber = employee.phoneNumber,
                email = employee.email,
                storeId = employee.storeId,
                storeName = employee.storeName,
                role = when (employee.accountRoleName) {
                    "Sales" -> UserRole.SALES
                    "Technician" -> UserRole.TECHNICIAN
                    "Customer" -> UserRole.CUSTOMER
                    "Designer" -> UserRole.CUSTOMER // Map Designer to Customer for mobile app
                    "Manager" -> UserRole.MANAGER
                    "Admin" -> UserRole.ADMIN
                    else -> UserRole.CUSTOMER
                }
            )
        }
    }
}
