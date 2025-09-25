package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class EmployeeDto(
    @SerializedName("employeeID")
    val employeeID: String,
    
    @SerializedName("firstName")
    val firstName: String,
    
    @SerializedName("lastName")
    val lastName: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("storeID")
    val storeID: String,
    
    @SerializedName("storeName")
    val storeName: String,
    
    @SerializedName("accountID")
    val accountID: String,
    
    @SerializedName("accountUsername")
    val accountUsername: String,
    
    @SerializedName("accountRoleName")
    val accountRoleName: String,
    
    @SerializedName("isActive")
    val isActive: Boolean,
    
    @SerializedName("roles")
    val roles: List<RoleDto>,
    
    @SerializedName("adminDetail")
    val adminDetail: AdminDetailDto?,
    
    @SerializedName("managerDetail")
    val managerDetail: ManagerDetailDto?,
    
    @SerializedName("salesPersonDetail")
    val salesPersonDetail: SalesPersonDetailDto?,
    
    @SerializedName("designerDetail")
    val designerDetail: DesignerDetailDto?,
    
    @SerializedName("technicianDetail")
    val technicianDetail: TechnicianDetailDto?
)

data class CreateEmployeeDto(
    @SerializedName("firstName")
    val firstName: String,
    
    @SerializedName("lastName")
    val lastName: String,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("storeID")
    val storeID: String,
    
    @SerializedName("accountID")
    val accountID: String
)

data class UpdateEmployeeDto(
    @SerializedName("firstName")
    val firstName: String?,
    
    @SerializedName("lastName")
    val lastName: String?,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String?,
    
    @SerializedName("email")
    val email: String?,
    
    @SerializedName("address")
    val address: String?,
    
    @SerializedName("storeID")
    val storeID: String?,
    
    @SerializedName("accountID")
    val accountID: String?
)

data class UpdateEmployeeStatusDto(
    @SerializedName("isActive")
    val isActive: Boolean
)

data class EmployeeStatisticsDto(
    @SerializedName("totalEmployees")
    val totalEmployees: Int,
    
    @SerializedName("activeEmployees")
    val activeEmployees: Int,
    
    @SerializedName("employeesByRole")
    val employeesByRole: Map<String, Int>,
    
    @SerializedName("averageSalary")
    val averageSalary: Double,
    
    @SerializedName("newHiresThisMonth")
    val newHiresThisMonth: Int,
    
    @SerializedName("employeesByStore")
    val employeesByStore: Map<String, Int>
)

