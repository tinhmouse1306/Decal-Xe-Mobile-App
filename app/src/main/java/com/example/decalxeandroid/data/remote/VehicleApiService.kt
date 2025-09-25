package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.VehicleDto
import com.example.decalxeandroid.data.dto.CreateVehicleDto
import com.example.decalxeandroid.data.dto.UpdateVehicleDto
import retrofit2.http.*

interface VehicleApiService {
    @GET(ApiConstants.VEHICLES_ENDPOINT)
    suspend fun getVehicles(
        @Query("pageNumber") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): List<VehicleDto>
    
    @GET(ApiConstants.VEHICLE_BY_ID_ENDPOINT)
    suspend fun getVehicleById(@Path("id") vehicleId: String): VehicleDto
    
    @POST(ApiConstants.VEHICLES_ENDPOINT)
    suspend fun createVehicle(@Body vehicle: CreateVehicleDto): VehicleDto
    
    @PUT(ApiConstants.VEHICLE_BY_ID_ENDPOINT)
    suspend fun updateVehicle(
        @Path("id") vehicleId: String,
        @Body vehicle: UpdateVehicleDto
    ): VehicleDto
    
    @DELETE(ApiConstants.VEHICLE_BY_ID_ENDPOINT)
    suspend fun deleteVehicle(@Path("id") vehicleId: String): String
    
    @GET(ApiConstants.VEHICLE_BY_BRAND_ENDPOINT)
    suspend fun getVehiclesByBrand(@Path("brand") brand: String): List<VehicleDto>
    
    @GET(ApiConstants.VEHICLE_BY_MODEL_ENDPOINT)
    suspend fun getVehiclesByModel(@Path("model") model: String): List<VehicleDto>
    
    @GET("${ApiConstants.VEHICLES_ENDPOINT}/search")
    suspend fun searchVehicles(@Query("query") query: String): List<VehicleDto>
    
    @GET("${ApiConstants.VEHICLES_ENDPOINT}/brands")
    suspend fun getVehicleBrands(): List<String>
    
    @GET("${ApiConstants.VEHICLES_ENDPOINT}/models")
    suspend fun getVehicleModels(): List<String>
    
    @GET("${ApiConstants.VEHICLES_ENDPOINT}/by-year/{year}")
    suspend fun getVehiclesByYear(@Path("year") year: Int): List<VehicleDto>
    
    @GET("${ApiConstants.VEHICLES_ENDPOINT}/by-type/{type}")
    suspend fun getVehiclesByType(@Path("type") type: String): List<VehicleDto>
}



