package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

// Feedback Models
data class FeedbackDto(
        @SerializedName("feedbackID") val feedbackID: String,
        @SerializedName("orderID") val orderID: String,
        @SerializedName("customerID") val customerID: String,
        @SerializedName("customerFullName") val customerFullName: String,
        @SerializedName("rating") val rating: Int,
        @SerializedName("comment") val comment: String?,
        @SerializedName("feedbackDate") val feedbackDate: String
)

data class CreateFeedbackDto(
        @SerializedName("orderID") val orderID: String,
        @SerializedName("customerID") val customerID: String,
        @SerializedName("rating") val rating: Int,
        @SerializedName("comment") val comment: String?
)

data class UpdateFeedbackDto(
        @SerializedName("rating") val rating: Int?,
        @SerializedName("comment") val comment: String?
)

// Warranty Models
data class WarrantyDto(
        @SerializedName("warrantyID") val warrantyID: String,
        @SerializedName("orderID") val orderID: String,
        @SerializedName("warrantyStartDate") val warrantyStartDate: String,
        @SerializedName("warrantyEndDate") val warrantyEndDate: String,
        @SerializedName("warrantyDuration") val warrantyDuration: Int,
        @SerializedName("warrantyTerms") val warrantyTerms: String,
        @SerializedName("warrantyStatus") val warrantyStatus: String,
        @SerializedName("customerID") val customerID: String,
        @SerializedName("customerFullName") val customerFullName: String,
        @SerializedName("vehicleID") val vehicleID: String,
        @SerializedName("chassisNumber") val chassisNumber: String
)

data class CreateWarrantyDto(
        @SerializedName("orderID") val orderID: String,
        @SerializedName("warrantyStartDate") val warrantyStartDate: String,
        @SerializedName("warrantyDuration") val warrantyDuration: Int,
        @SerializedName("warrantyTerms") val warrantyTerms: String,
        @SerializedName("warrantyStatus") val warrantyStatus: String
)

data class UpdateWarrantyDto(
        @SerializedName("warrantyStartDate") val warrantyStartDate: String?,
        @SerializedName("warrantyDuration") val warrantyDuration: Int?,
        @SerializedName("warrantyTerms") val warrantyTerms: String?,
        @SerializedName("warrantyStatus") val warrantyStatus: String?
)

// Technical Models
data class TechLaborPriceDto(
        @SerializedName("laborPriceID") val laborPriceID: String,
        @SerializedName("serviceID") val serviceID: String,
        @SerializedName("serviceName") val serviceName: String,
        @SerializedName("laborPrice") val laborPrice: Double,
        @SerializedName("effectiveDate") val effectiveDate: String
)

data class CreateTechLaborPriceDto(
        @SerializedName("serviceID") val serviceID: String,
        @SerializedName("laborPrice") val laborPrice: Double,
        @SerializedName("effectiveDate") val effectiveDate: String
)

data class UpdateTechLaborPriceDto(@SerializedName("laborPrice") val laborPrice: Double?)

// Vehicle Decal Models
data class VehicleModelDecalTemplateDto(
        @SerializedName("modelID") val modelID: String,
        @SerializedName("templateID") val templateID: String,
        @SerializedName("templateName") val templateName: String,
        @SerializedName("decalTypeName") val decalTypeName: String,
        @SerializedName("isCompatible") val isCompatible: Boolean
)

data class VehicleModelDecalTypeDto(
        @SerializedName("modelID") val modelID: String,
        @SerializedName("decalTypeID") val decalTypeID: String,
        @SerializedName("decalTypeName") val decalTypeName: String,
        @SerializedName("price") val price: Double,
        @SerializedName("isCompatible") val isCompatible: Boolean
)

data class UpdateVehicleDecalTypePriceDto(@SerializedName("price") val price: Double)

data class AssignDecalTypeToVehicleDto(
        @SerializedName("modelID") val modelID: String,
        @SerializedName("decalTypeID") val decalTypeID: String,
        @SerializedName("price") val price: Double
)

// Order with Customer Models
data class CreateOrderWithCustomerDto(
        @SerializedName("totalAmount") val totalAmount: Double,
        @SerializedName("assignedEmployeeID") val assignedEmployeeID: String?,
        @SerializedName("vehicleID") val vehicleID: String?,
        @SerializedName("expectedArrivalTime") val expectedArrivalTime: String?,
        @SerializedName("priority") val priority: String?,
        @SerializedName("isCustomDecal") val isCustomDecal: Boolean,
        @SerializedName("description") val description: String?,
        @SerializedName("existingCustomerID") val existingCustomerID: String?,
        @SerializedName("newCustomerPayload") val newCustomerPayload: NewCustomerPayloadDto?
)

data class NewCustomerPayloadDto(
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("phoneNumber") val phoneNumber: String,
        @SerializedName("email") val email: String?,
        @SerializedName("address") val address: String?,
        @SerializedName("createAccount") val createAccount: Boolean
)

data class OrderWithCustomerResponseDto(
        @SerializedName("orderID") val orderID: String,
        @SerializedName("orderDate") val orderDate: String,
        @SerializedName("orderStatus") val orderStatus: String,
        @SerializedName("currentStage") val currentStage: String,
        @SerializedName("totalAmount") val totalAmount: Double,
        @SerializedName("assignedEmployeeID") val assignedEmployeeID: String,
        @SerializedName("assignedEmployeeFullName") val assignedEmployeeFullName: String,
        @SerializedName("vehicleID") val vehicleID: String,
        @SerializedName("vehicleModelName") val vehicleModelName: String,
        @SerializedName("vehicleBrandName") val vehicleBrandName: String,
        @SerializedName("chassisNumber") val chassisNumber: String,
        @SerializedName("expectedArrivalTime") val expectedArrivalTime: String,
        @SerializedName("priority") val priority: String,
        @SerializedName("isCustomDecal") val isCustomDecal: Boolean,
        @SerializedName("description") val description: String?,
        @SerializedName("storeID") val storeID: String?, // Thêm StoreID
        @SerializedName("customerID") val customerID: String,
        @SerializedName("customerFullName") val customerFullName: String,
        @SerializedName("customerPhoneNumber") val customerPhoneNumber: String,
        @SerializedName("customerEmail") val customerEmail: String?,
        @SerializedName("customerAddress") val customerAddress: String?,
        @SerializedName("accountID") val accountID: String,
        @SerializedName("accountUsername") val accountUsername: String,
        @SerializedName("accountCreated") val accountCreated: Boolean,
        @SerializedName("generatedPassword") val generatedPassword: String?,
        @SerializedName("message") val message: String?
)

// Order Create Form Data
data class OrderCreateFormDataDto(
        @SerializedName("customers") val customers: List<CustomerFormDto>,
        @SerializedName("services") val services: List<ServiceFormDto>,
        @SerializedName("employees") val employees: List<EmployeeFormDto>
)

data class CustomerFormDto(
        @SerializedName("customerID") val customerID: String,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("phoneNumber") val phoneNumber: String
)

data class ServiceFormDto(
        @SerializedName("serviceID") val serviceID: String,
        @SerializedName("serviceName") val serviceName: String,
        @SerializedName("price") val price: Double
)

data class EmployeeFormDto(
        @SerializedName("employeeID") val employeeID: String,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String
)

// Order Tracking
data class OrderTrackingDto(
        @SerializedName("orderID") val orderID: String,
        @SerializedName("orderDate") val orderDate: String,
        @SerializedName("orderStatus") val orderStatus: String,
        @SerializedName("currentStage") val currentStage: String,
        @SerializedName("totalAmount") val totalAmount: Double,
        @SerializedName("customerFullName") val customerFullName: String,
        @SerializedName("customerPhoneNumber") val customerPhoneNumber: String,
        @SerializedName("assignedEmployeeFullName") val assignedEmployeeFullName: String,
        @SerializedName("stageHistory") val stageHistory: List<OrderStageHistoryDto>
)

// Scheduled Work Unit
data class ScheduledWorkUnitDto(
        @SerializedName("workUnitID") val workUnitID: String,
        @SerializedName("orderID") val orderID: String,
        @SerializedName("serviceID") val serviceID: String,
        @SerializedName("serviceName") val serviceName: String,
        @SerializedName("assignedTechnicianID") val assignedTechnicianID: String,
        @SerializedName("assignedTechnicianName") val assignedTechnicianName: String,
        @SerializedName("scheduledStartTime") val scheduledStartTime: String,
        @SerializedName("scheduledEndTime") val scheduledEndTime: String,
        @SerializedName("workStatus") val workStatus: String
)
