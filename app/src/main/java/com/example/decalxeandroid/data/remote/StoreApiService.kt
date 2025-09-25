package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.StoreDto
import com.example.decalxeandroid.data.dto.CreateStoreDto
import com.example.decalxeandroid.data.dto.UpdateStoreDto
import retrofit2.http.*

interface StoreApiService {
    @GET(ApiConstants.STORES_ENDPOINT)
    suspend fun getStores(
        @Query("pageNumber") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): List<StoreDto>
    
    @GET(ApiConstants.STORE_BY_ID_ENDPOINT)
    suspend fun getStoreById(@Path("id") storeId: String): StoreDto
    
    @POST(ApiConstants.STORES_ENDPOINT)
    suspend fun createStore(@Body store: CreateStoreDto): StoreDto
    
    @PUT(ApiConstants.STORE_BY_ID_ENDPOINT)
    suspend fun updateStore(
        @Path("id") storeId: String,
        @Body store: UpdateStoreDto
    ): StoreDto
    
    @DELETE(ApiConstants.STORE_BY_ID_ENDPOINT)
    suspend fun deleteStore(@Path("id") storeId: String): String
    
    @GET(ApiConstants.STORE_BY_LOCATION_ENDPOINT)
    suspend fun getStoresByLocation(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Double = 10.0
    ): List<StoreDto>
    
    @GET("${ApiConstants.STORES_ENDPOINT}/search")
    suspend fun searchStores(@Query("query") query: String): List<StoreDto>
    
    @GET("${ApiConstants.STORES_ENDPOINT}/by-city/{city}")
    suspend fun getStoresByCity(@Path("city") city: String): List<StoreDto>
    
    @GET("${ApiConstants.STORES_ENDPOINT}/by-district/{district}")
    suspend fun getStoresByDistrict(@Path("district") district: String): List<StoreDto>
}



