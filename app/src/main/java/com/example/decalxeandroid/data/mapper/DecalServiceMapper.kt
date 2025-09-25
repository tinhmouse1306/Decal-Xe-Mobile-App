package com.example.decalxeandroid.data.mapper

import com.example.decalxeandroid.data.dto.DecalServiceDto
import com.example.decalxeandroid.domain.model.DecalService

class DecalServiceMapper {
    
    fun toDomain(dto: DecalServiceDto): DecalService {
        return DecalService(
            serviceId = dto.serviceID,
            serviceName = dto.serviceName ?: "Dịch vụ",
            description = dto.description,
            price = dto.price ?: 0.0,
            standardWorkUnits = dto.standardWorkUnits,
            decalTemplateId = dto.decalTemplateID,
            decalTemplateName = dto.decalTemplateName,
            decalTypeName = dto.decalTypeName
        )
    }
    
    fun toDto(service: DecalService): DecalServiceDto {
        return DecalServiceDto(
            serviceID = service.serviceId,
            serviceName = service.serviceName,
            description = service.description,
            price = service.price,
            standardWorkUnits = service.standardWorkUnits,
            decalTemplateID = service.decalTemplateId,
            decalTemplateName = service.decalTemplateName,
            decalTypeName = service.decalTypeName
        )
    }
}
