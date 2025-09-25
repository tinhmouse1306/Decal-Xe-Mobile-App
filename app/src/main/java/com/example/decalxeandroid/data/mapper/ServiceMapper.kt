package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.ServiceDto
import com.example.decalxeandroid.data.dto.CreateServiceDto
import com.example.decalxeandroid.data.dto.UpdateServiceDto
import com.example.decalxeandroid.domain.model.Service

object ServiceMapper {
    fun mapServiceDtoToService(dto: ServiceDto): Service {
        return Service(
            id = dto.serviceID,
            name = dto.serviceName,
            description = dto.description,
            price = dto.price,
            category = "", // ServiceDto doesn't have category
            duration = dto.standardWorkUnits, // Use standardWorkUnits as duration
            isActive = true, // ServiceDto doesn't have isActive
            imageUrl = null, // ServiceDto doesn't have imageUrl
            requirements = null, // ServiceDto doesn't have requirements
            warrantyPeriod = null, // ServiceDto doesn't have warrantyPeriod
            createdAt = "", // ServiceDto doesn't have createdAt
            updatedAt = "" // ServiceDto doesn't have updatedAt
        )
    }
    
    fun mapServiceToCreateServiceDto(service: Service): CreateServiceDto {
        return CreateServiceDto(
            serviceName = service.name,
            description = service.description,
            price = service.price,
            standardWorkUnits = service.duration,
            decalTemplateID = "" // Service doesn't have decalTemplateID in domain model
        )
    }
    
    fun mapServiceToUpdateServiceDto(service: Service): UpdateServiceDto {
        return UpdateServiceDto(
            serviceName = service.name,
            description = service.description,
            price = service.price,
            standardWorkUnits = service.duration,
            decalTemplateID = null // Service doesn't have decalTemplateID in domain model
        )
    }
}

