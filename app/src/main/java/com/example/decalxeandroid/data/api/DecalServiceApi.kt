package com.example.decalxeandroid.data.api

import com.example.decalxeandroid.data.dto.DecalServiceDto
import com.example.decalxeandroid.data.dto.CreateDecalServiceDto
import com.example.decalxeandroid.data.dto.UpdateDecalServiceDto
import retrofit2.Response
import retrofit2.http.*

interface DecalServiceApi {
    
    @GET("DecalServices")
    suspend fun getDecalServices(): Response<List<DecalServiceDto>>
    
    @GET("DecalServices/{id}")
    suspend fun getDecalServiceById(@Path("id") id: String): Response<DecalServiceDto>
    
    @POST("DecalServices")
    suspend fun createDecalService(@Body service: CreateDecalServiceDto): Response<DecalServiceDto>
    
    @PUT("DecalServices/{id}")
    suspend fun updateDecalService(
        @Path("id") id: String, 
        @Body service: UpdateDecalServiceDto
    ): Response<DecalServiceDto>
    
    @DELETE("DecalServices/{id}")
    suspend fun deleteDecalService(@Path("id") id: String): Response<Unit>
}
