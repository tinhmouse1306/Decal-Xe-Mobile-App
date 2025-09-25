package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.EmployeeDto
import com.example.decalxeandroid.data.dto.CreateEmployeeDto
import com.example.decalxeandroid.data.dto.UpdateEmployeeDto
import com.example.decalxeandroid.data.dto.EmployeeStatisticsDto
import retrofit2.http.*

interface EmployeeApiService {
    @GET(ApiConstants.EMPLOYEES_ENDPOINT)
    suspend fun getEmployees(
        @Query("pageNumber") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): List<EmployeeDto>
    
    @GET(ApiConstants.EMPLOYEE_BY_ID_ENDPOINT)
    suspend fun getEmployeeById(@Path("id") employeeId: String): EmployeeDto
    
    @POST(ApiConstants.EMPLOYEES_ENDPOINT)
    suspend fun createEmployee(@Body employee: CreateEmployeeDto): EmployeeDto
    
    @PUT(ApiConstants.EMPLOYEE_BY_ID_ENDPOINT)
    suspend fun updateEmployee(
        @Path("id") employeeId: String,
        @Body employee: UpdateEmployeeDto
    ): EmployeeDto
    
    @DELETE(ApiConstants.EMPLOYEE_BY_ID_ENDPOINT)
    suspend fun deleteEmployee(@Path("id") employeeId: String): String
    
    @GET(ApiConstants.EMPLOYEE_BY_ROLE_ENDPOINT)
    suspend fun getEmployeesByRole(@Path("role") role: String): List<EmployeeDto>
    
    @GET(ApiConstants.EMPLOYEE_STATISTICS_ENDPOINT)
    suspend fun getEmployeeStatistics(): EmployeeStatisticsDto
    
    @GET("${ApiConstants.EMPLOYEES_ENDPOINT}/search")
    suspend fun searchEmployees(@Query("query") query: String): List<EmployeeDto>
    
    @GET("${ApiConstants.EMPLOYEES_ENDPOINT}/by-email/{email}")
    suspend fun getEmployeeByEmail(@Path("email") email: String): EmployeeDto?
    
    @GET("${ApiConstants.EMPLOYEES_ENDPOINT}/by-phone/{phoneNumber}")
    suspend fun getEmployeeByPhone(@Path("phoneNumber") phoneNumber: String): EmployeeDto?
}



