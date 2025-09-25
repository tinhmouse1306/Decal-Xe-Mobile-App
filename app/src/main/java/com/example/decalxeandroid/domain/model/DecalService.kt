package com.example.decalxeandroid.domain.model

data class DecalService(
    val serviceId: String,
    val serviceName: String,
    val description: String?,
    val price: Double,
    val standardWorkUnits: Int?,
    val decalTemplateId: String?,
    val decalTemplateName: String?,
    val decalTypeName: String?
)
