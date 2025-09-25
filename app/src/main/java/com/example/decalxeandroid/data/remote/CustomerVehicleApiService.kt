package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.CustomerVehicleDto
import com.example.decalxeandroid.data.dto.CreateCustomerVehicleDto
import com.example.decalxeandroid.data.dto.UpdateCustomerVehicleDto
import retrofit2.http.*

interface CustomerVehicleApiService {
    @GET("CustomerVehicles")
    suspend fun getVehicles(
        @Query("pageNumber") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): List<CustomerVehicleDto>
    
    @GET("CustomerVehicles/{id}")
    suspend fun getVehicleById(@Path("id") vehicleId: String): CustomerVehicleDto
    
    @GET("CustomerVehicles/by-license-plate/{licensePlate}")
    suspend fun getVehicleByLicensePlate(@Path("licensePlate") licensePlate: String): CustomerVehicleDto
    
    @GET("CustomerVehicles/by-customer/{customerId}")
    suspend fun getVehiclesByCustomer(@Path("customerId") customerId: String): List<CustomerVehicleDto>
    
    @POST("CustomerVehicles")
    suspend fun createVehicle(@Body vehicle: CreateCustomerVehicleDto): CustomerVehicleDto
    
    @PUT("CustomerVehicles/{id}")
    suspend fun updateVehicle(
        @Path("id") vehicleId: String,
        @Body vehicle: UpdateCustomerVehicleDto
    ): CustomerVehicleDto
    
    @DELETE("CustomerVehicles/{id}")
    suspend fun deleteVehicle(@Path("id") vehicleId: String): String
    
    @GET("CustomerVehicles/{id}/exists")
    suspend fun checkVehicleExists(@Path("id") vehicleId: String): Boolean
    
    @GET("CustomerVehicles/license-plate/{licensePlate}/exists")
    suspend fun checkLicensePlateExists(@Path("licensePlate") licensePlate: String): Boolean
    
    @GET("CustomerVehicles/chassis/{chassisNumber}/exists")
    suspend fun checkChassisNumberExists(@Path("chassisNumber") chassisNumber: String): Boolean
}
