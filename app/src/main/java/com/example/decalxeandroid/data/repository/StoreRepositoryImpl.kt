package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.data.remote.StoreApiService
import com.example.decalxeandroid.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoreRepositoryImpl(
    private val storeApiService: StoreApiService
) : StoreRepository {
    
    override fun getStores(page: Int, pageSize: Int): Flow<Result<List<StoreDto>>> = flow {
        try {
            val stores = storeApiService.getStores(page, pageSize)
            emit(Result.success(stores))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getStoreById(storeId: String): Flow<Result<StoreDto>> = flow {
        try {
            val store = storeApiService.getStoreById(storeId)
            emit(Result.success(store))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun createStore(store: CreateStoreDto): Flow<Result<StoreDto>> = flow {
        try {
            val createdStore = storeApiService.createStore(store)
            emit(Result.success(createdStore))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun updateStore(storeId: String, store: UpdateStoreDto): Flow<Result<StoreDto>> = flow {
        try {
            val updatedStore = storeApiService.updateStore(storeId, store)
            emit(Result.success(updatedStore))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun deleteStore(storeId: String): Flow<Result<String>> = flow {
        try {
            val message = storeApiService.deleteStore(storeId)
            emit(Result.success(message))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getStoresByLocation(latitude: Double, longitude: Double, radius: Double): Flow<Result<List<StoreDto>>> = flow {
        try {
            val stores = storeApiService.getStoresByLocation(latitude, longitude, radius)
            emit(Result.success(stores))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun searchStores(query: String): Flow<Result<List<StoreDto>>> = flow {
        try {
            val stores = storeApiService.searchStores(query)
            emit(Result.success(stores))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getStoresByCity(city: String): Flow<Result<List<StoreDto>>> = flow {
        try {
            val stores = storeApiService.getStoresByCity(city)
            emit(Result.success(stores))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getStoresByDistrict(district: String): Flow<Result<List<StoreDto>>> = flow {
        try {
            val stores = storeApiService.getStoresByDistrict(district)
            emit(Result.success(stores))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
