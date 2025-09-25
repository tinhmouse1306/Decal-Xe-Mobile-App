package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.StoreDto
import com.example.decalxeandroid.data.dto.CreateStoreDto
import com.example.decalxeandroid.data.dto.UpdateStoreDto
import com.example.decalxeandroid.domain.model.Store

object StoreMapper {
    fun mapStoreDtoToStore(dto: StoreDto): Store {
        return Store(
            id = dto.storeID,
            name = dto.storeName,
            address = dto.address,
            city = "", // StoreDto doesn't have city
            district = "", // StoreDto doesn't have district
            phoneNumber = dto.phoneNumber,
            email = dto.email,
            latitude = null, // StoreDto doesn't have latitude
            longitude = null, // StoreDto doesn't have longitude
            isActive = dto.isActive,
            openingHours = null, // StoreDto doesn't have openingHours
            managerId = dto.managerID,
            managerName = dto.managerFullName,
            createdAt = "", // StoreDto doesn't have createdAt
            updatedAt = "" // StoreDto doesn't have updatedAt
        )
    }
    
    fun mapStoreToCreateStoreDto(store: Store): CreateStoreDto {
        return CreateStoreDto(
            storeName = store.name,
            address = store.address,
            phoneNumber = store.phoneNumber,
            email = store.email,
            managerID = store.managerId ?: ""
        )
    }
    
    fun mapStoreToUpdateStoreDto(store: Store): UpdateStoreDto {
        return UpdateStoreDto(
            storeName = store.name,
            address = store.address,
            phoneNumber = store.phoneNumber,
            email = store.email,
            managerID = store.managerId
        )
    }
}

