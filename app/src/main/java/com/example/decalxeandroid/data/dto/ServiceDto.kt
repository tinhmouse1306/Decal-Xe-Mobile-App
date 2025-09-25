package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class DecalServiceDto(
    @SerializedName("serviceID")
    val serviceID: String,
    
    @SerializedName("serviceName")
    val serviceName: String?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("price")
    val price: Double?,
    
    @SerializedName("standardWorkUnits")
    val standardWorkUnits: Int?,
    
    @SerializedName("decalTemplateID")
    val decalTemplateID: String?,
    
    @SerializedName("decalTemplateName")
    val decalTemplateName: String?,
    
    @SerializedName("decalTypeName")
    val decalTypeName: String?
)

data class CreateDecalServiceDto(
    @SerializedName("serviceName")
    val serviceName: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("standardWorkUnits")
    val standardWorkUnits: Int,
    
    @SerializedName("decalTemplateID")
    val decalTemplateID: String
)

data class UpdateDecalServiceDto(
    @SerializedName("serviceName")
    val serviceName: String?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("price")
    val price: Double?,
    
    @SerializedName("standardWorkUnits")
    val standardWorkUnits: Int?,
    
    @SerializedName("decalTemplateID")
    val decalTemplateID: String?
)

data class DecalTemplateDto(
    @SerializedName("templateID")
    val templateID: String,
    
    @SerializedName("templateName")
    val templateName: String,
    
    @SerializedName("decalTypeID")
    val decalTypeID: String
)

data class CreateDecalTemplateDto(
    @SerializedName("templateName")
    val templateName: String,
    
    @SerializedName("decalTypeID")
    val decalTypeID: String
)

data class UpdateDecalTemplateDto(
    @SerializedName("templateName")
    val templateName: String?,
    
    @SerializedName("decalTypeID")
    val decalTypeID: String?
)

data class DecalTypeDto(
    @SerializedName("decalTypeID")
    val decalTypeID: String,
    
    @SerializedName("decalTypeName")
    val decalTypeName: String
)

data class CreateDecalTypeDto(
    @SerializedName("decalTypeName")
    val decalTypeName: String,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("basePrice")
    val basePrice: Double,
    
    @SerializedName("materialType")
    val materialType: String
)

data class UpdateDecalTypeDto(
    @SerializedName("decalTypeName")
    val decalTypeName: String?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("basePrice")
    val basePrice: Double?,
    
    @SerializedName("materialType")
    val materialType: String?
)

data class ServiceDto(
    @SerializedName("serviceID")
    val serviceID: String,
    
    @SerializedName("serviceName")
    val serviceName: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("standardWorkUnits")
    val standardWorkUnits: Int,
    
    @SerializedName("decalTemplateID")
    val decalTemplateID: String,
    
    @SerializedName("decalTemplateName")
    val decalTemplateName: String,
    
    @SerializedName("decalTypeName")
    val decalTypeName: String
)

data class CreateServiceDto(
    @SerializedName("serviceName")
    val serviceName: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("standardWorkUnits")
    val standardWorkUnits: Int,
    
    @SerializedName("decalTemplateID")
    val decalTemplateID: String
)

data class UpdateServiceDto(
    @SerializedName("serviceName")
    val serviceName: String?,
    
    @SerializedName("description")
    val description: String?,
    
    @SerializedName("price")
    val price: Double?,
    
    @SerializedName("standardWorkUnits")
    val standardWorkUnits: Int?,
    
    @SerializedName("decalTemplateID")
    val decalTemplateID: String?
)

