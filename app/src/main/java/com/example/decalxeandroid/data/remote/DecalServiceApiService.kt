package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.DecalServiceDto
import com.example.decalxeandroid.data.dto.CreateDecalServiceDto
import com.example.decalxeandroid.data.dto.UpdateDecalServiceDto
import retrofit2.http.*

interface DecalServiceApiService {
    @GET("DecalServices")
    suspend fun getDecalServices(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): List<DecalServiceDto>
    
    @GET("DecalServices/{id}")
    suspend fun getDecalServiceById(@Path("id") serviceId: String): DecalServiceDto
    
    @POST("DecalServices")
    suspend fun createDecalService(@Body service: CreateDecalServiceDto): DecalServiceDto
    
    @PUT("DecalServices/{id}")
    suspend fun updateDecalService(
        @Path("id") serviceId: String,
        @Body service: UpdateDecalServiceDto
    ): DecalServiceDto
    
    @DELETE("DecalServices/{id}")
    suspend fun deleteDecalService(@Path("id") serviceId: String): Boolean
    
    @GET("DecalServices/search")
    suspend fun searchDecalServices(@Query("query") query: String): List<DecalServiceDto>
    
    @GET("DecalServices/by-type")
    suspend fun getDecalServicesByType(@Query("serviceType") serviceType: String): List<DecalServiceDto>
    
    @GET("DecalServices/statistics")
    suspend fun getDecalServiceStatistics(): Map<String, Any>
}
