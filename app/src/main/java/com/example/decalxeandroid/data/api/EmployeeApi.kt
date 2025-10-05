package com.example.decalxeandroid.data.api

import com.example.decalxeandroid.data.dto.CreateEmployeeDto
import com.example.decalxeandroid.data.dto.EmployeeDto
import com.example.decalxeandroid.data.dto.UpdateEmployeeDto
import retrofit2.Response
import retrofit2.http.*

interface EmployeeApi {

    @GET("Employees") suspend fun getEmployees(): Response<List<EmployeeDto>>

    @GET("Employees/{id}")
    suspend fun getEmployeeById(@Path("id") id: String): Response<EmployeeDto>

    @POST("Employees")
    suspend fun createEmployee(@Body employee: CreateEmployeeDto): Response<EmployeeDto>

    @PUT("Employees/{id}")
    suspend fun updateEmployee(
            @Path("id") id: String,
            @Body employee: UpdateEmployeeDto
    ): Response<EmployeeDto>

    @DELETE("Employees/{id}") suspend fun deleteEmployee(@Path("id") id: String): Response<Unit>
}
