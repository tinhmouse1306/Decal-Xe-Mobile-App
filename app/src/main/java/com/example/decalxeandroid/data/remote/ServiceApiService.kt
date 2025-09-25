package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.ServiceDto
import com.example.decalxeandroid.data.dto.CreateServiceDto
import com.example.decalxeandroid.data.dto.UpdateServiceDto
import retrofit2.http.*

interface ServiceApiService {
    @GET(ApiConstants.SERVICES_ENDPOINT)
    suspend fun getServices(
        @Query("pageNumber") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): List<ServiceDto>
    
    @GET(ApiConstants.SERVICE_BY_ID_ENDPOINT)
    suspend fun getServiceById(@Path("id") serviceId: String): ServiceDto
    
    @POST(ApiConstants.SERVICES_ENDPOINT)
    suspend fun createService(@Body service: CreateServiceDto): ServiceDto
    
    @PUT(ApiConstants.SERVICE_BY_ID_ENDPOINT)
    suspend fun updateService(
        @Path("id") serviceId: String,
        @Body service: UpdateServiceDto
    ): ServiceDto
    
    @DELETE(ApiConstants.SERVICE_BY_ID_ENDPOINT)
    suspend fun deleteService(@Path("id") serviceId: String): String
    
    @GET(ApiConstants.SERVICE_BY_CATEGORY_ENDPOINT)
    suspend fun getServicesByCategory(@Path("category") category: String): List<ServiceDto>
    
    @GET("${ApiConstants.SERVICES_ENDPOINT}/search")
    suspend fun searchServices(@Query("query") query: String): List<ServiceDto>
    
    @GET("${ApiConstants.SERVICES_ENDPOINT}/categories")
    suspend fun getServiceCategories(): List<String>
    
    @GET("${ApiConstants.SERVICES_ENDPOINT}/by-price-range")
    suspend fun getServicesByPriceRange(
        @Query("minPrice") minPrice: Double,
        @Query("maxPrice") maxPrice: Double
    ): List<ServiceDto>
}



