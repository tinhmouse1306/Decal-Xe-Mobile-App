package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class DesignDto(
    @SerializedName("designID")
    val designID: String,
    
    @SerializedName("designURL")
    val designURL: String,
    
    @SerializedName("designerID")
    val designerID: String,
    
    @SerializedName("designerFullName")
    val designerFullName: String,
    
    @SerializedName("version")
    val version: String,
    
    @SerializedName("approvalStatus")
    val approvalStatus: String,
    
    @SerializedName("isAIGenerated")
    val isAIGenerated: Boolean,
    
    @SerializedName("aiModelUsed")
    val aiModelUsed: String?,
    
    @SerializedName("designPrice")
    val designPrice: Double,
    
    @SerializedName("size")
    val size: String,
    
    @SerializedName("templateItems")
    val templateItems: List<DesignTemplateItemDto>
)

data class CreateDesignDto(
    @SerializedName("designURL")
    val designURL: String,
    
    @SerializedName("designerID")
    val designerID: String,
    
    @SerializedName("version")
    val version: String,
    
    @SerializedName("approvalStatus")
    val approvalStatus: String,
    
    @SerializedName("isAIGenerated")
    val isAIGenerated: Boolean,
    
    @SerializedName("aiModelUsed")
    val aiModelUsed: String?,
    
    @SerializedName("designPrice")
    val designPrice: Double,
    
    @SerializedName("size")
    val size: String
)

data class UpdateDesignDto(
    @SerializedName("designURL")
    val designURL: String?,
    
    @SerializedName("designerID")
    val designerID: String?,
    
    @SerializedName("version")
    val version: String?,
    
    @SerializedName("approvalStatus")
    val approvalStatus: String?,
    
    @SerializedName("isAIGenerated")
    val isAIGenerated: Boolean?,
    
    @SerializedName("aiModelUsed")
    val aiModelUsed: String?,
    
    @SerializedName("designPrice")
    val designPrice: Double?,
    
    @SerializedName("size")
    val size: String?
)

data class DesignCommentDto(
    @SerializedName("commentID")
    val commentID: String,
    
    @SerializedName("designID")
    val designID: String,
    
    @SerializedName("commenterID")
    val commenterID: String,
    
    @SerializedName("commenterFullName")
    val commenterFullName: String,
    
    @SerializedName("commentText")
    val commentText: String,
    
    @SerializedName("commentDate")
    val commentDate: String
)

data class CreateDesignCommentDto(
    @SerializedName("designID")
    val designID: String,
    
    @SerializedName("commenterID")
    val commenterID: String,
    
    @SerializedName("commentText")
    val commentText: String
)

data class UpdateDesignCommentDto(
    @SerializedName("commentText")
    val commentText: String?
)

data class DesignTemplateItemDto(
    @SerializedName("templateItemID")
    val templateItemID: String,
    
    @SerializedName("designID")
    val designID: String,
    
    @SerializedName("decalTemplateID")
    val decalTemplateID: String,
    
    @SerializedName("decalTemplateName")
    val decalTemplateName: String,
    
    @SerializedName("position")
    val position: String,
    
    @SerializedName("size")
    val size: String,
    
    @SerializedName("color")
    val color: String,
    
    @SerializedName("quantity")
    val quantity: Int
)

data class CreateDesignTemplateItemDto(
    @SerializedName("designID")
    val designID: String,
    
    @SerializedName("decalTemplateID")
    val decalTemplateID: String,
    
    @SerializedName("position")
    val position: String,
    
    @SerializedName("size")
    val size: String,
    
    @SerializedName("color")
    val color: String,
    
    @SerializedName("quantity")
    val quantity: Int
)

data class UpdateDesignTemplateItemDto(
    @SerializedName("decalTemplateID")
    val decalTemplateID: String?,
    
    @SerializedName("position")
    val position: String?,
    
    @SerializedName("size")
    val size: String?,
    
    @SerializedName("color")
    val color: String?,
    
    @SerializedName("quantity")
    val quantity: Int?
)

data class DesignWorkOrderDto(
    @SerializedName("workOrderID")
    val workOrderID: String,
    
    @SerializedName("designID")
    val designID: String,
    
    @SerializedName("assignedDesignerID")
    val assignedDesignerID: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("dueDate")
    val dueDate: String
)

data class CreateDesignWorkOrderDto(
    @SerializedName("designID")
    val designID: String,
    
    @SerializedName("assignedDesignerID")
    val assignedDesignerID: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("dueDate")
    val dueDate: String
)

data class UpdateDesignWorkOrderDto(
    @SerializedName("assignedDesignerID")
    val assignedDesignerID: String?,
    
    @SerializedName("status")
    val status: String?,
    
    @SerializedName("dueDate")
    val dueDate: String?
)
