package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class RoleDto(
    @SerializedName("roleID")
    val roleID: String,
    @SerializedName("roleName")
    val roleName: String
)

data class AdminDetailDto(
    @SerializedName("adminDetailID")
    val adminDetailID: String
)

data class ManagerDetailDto(
    @SerializedName("managerDetailID")
    val managerDetailID: String
)

data class SalesPersonDetailDto(
    @SerializedName("salesPersonDetailID")
    val salesPersonDetailID: String
)

data class DesignerDetailDto(
    @SerializedName("designerDetailID")
    val designerDetailID: String
)

data class TechnicianDetailDto(
    @SerializedName("technicianDetailID")
    val technicianDetailID: String
)
