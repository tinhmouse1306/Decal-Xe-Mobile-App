package com.example.decalxeandroid.data.api

import com.example.decalxeandroid.data.dto.CustomerVehicleDto
import com.example.decalxeandroid.data.dto.CreateCustomerVehicleDto
import com.example.decalxeandroid.data.dto.UpdateCustomerVehicleDto
import retrofit2.Response
import retrofit2.http.*

interface CustomerVehicleApi {
    
    @GET("CustomerVehicles")
    suspend fun getCustomerVehicles(): Response<List<CustomerVehicleDto>>
    
    @GET("CustomerVehicles/{id}")
    suspend fun getCustomerVehicleById(@Path("id") id: String): Response<CustomerVehicleDto>
    
    @GET("CustomerVehicles/by-customer/{customerId}")
    suspend fun getCustomerVehiclesByCustomerId(@Path("customerId") customerId: String): Response<List<CustomerVehicleDto>>
    
    @POST("CustomerVehicles")
    suspend fun createCustomerVehicle(@Body vehicle: CreateCustomerVehicleDto): Response<CustomerVehicleDto>
    
    @PUT("CustomerVehicles/{id}")
    suspend fun updateCustomerVehicle(
        @Path("id") id: String, 
        @Body vehicle: UpdateCustomerVehicleDto
    ): Response<CustomerVehicleDto>
    
    @DELETE("CustomerVehicles/{id}")
    suspend fun deleteCustomerVehicle(@Path("id") id: String): Response<Unit>
}
