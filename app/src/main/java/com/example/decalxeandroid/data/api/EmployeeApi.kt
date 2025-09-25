package com.example.decalxeandroid.data.api

import com.example.decalxeandroid.data.dto.EmployeeDto
import com.example.decalxeandroid.data.dto.CreateEmployeeDto
import com.example.decalxeandroid.data.dto.UpdateEmployeeDto
import retrofit2.Response
import retrofit2.http.*

interface EmployeeApi {
    
    @GET("employees")
    suspend fun getEmployees(): Response<List<EmployeeDto>>
    
    @GET("employees/{id}")
    suspend fun getEmployeeById(@Path("id") id: String): Response<EmployeeDto>
    
    @POST("employees")
    suspend fun createEmployee(@Body employee: CreateEmployeeDto): Response<EmployeeDto>
    
    @PUT("employees/{id}")
    suspend fun updateEmployee(
        @Path("id") id: String, 
        @Body employee: UpdateEmployeeDto
    ): Response<EmployeeDto>
    
    @DELETE("employees/{id}")
    suspend fun deleteEmployee(@Path("id") id: String): Response<Unit>
}
