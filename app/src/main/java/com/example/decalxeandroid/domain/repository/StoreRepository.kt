package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.data.dto.*
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    fun getStores(page: Int = 1, pageSize: Int = 20): Flow<Result<List<StoreDto>>>
    fun getStoreById(storeId: String): Flow<Result<StoreDto>>
    fun createStore(store: CreateStoreDto): Flow<Result<StoreDto>>
    fun updateStore(storeId: String, store: UpdateStoreDto): Flow<Result<StoreDto>>
    fun deleteStore(storeId: String): Flow<Result<String>>
    fun getStoresByLocation(latitude: Double, longitude: Double, radius: Double = 10.0): Flow<Result<List<StoreDto>>>
    fun searchStores(query: String): Flow<Result<List<StoreDto>>>
    fun getStoresByCity(city: String): Flow<Result<List<StoreDto>>>
    fun getStoresByDistrict(district: String): Flow<Result<List<StoreDto>>>
}



