package com.example.decalxeandroid.presentation.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.*
import com.example.decalxeandroid.domain.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class AddVehicleViewModel(
    private val customerVehicleRepository: CustomerVehicleRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AddVehicleUiState>(
        AddVehicleUiState.Editing(
            formData = VehicleFormData(),
            customers = emptyList(),
            vehicleModels = emptyList()
        )
    )
    val uiState: StateFlow<AddVehicleUiState> = _uiState.asStateFlow()
    
    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = AddVehicleUiState.Loading
            
            try {
                // Load customers
                customerRepository.getCustomers().collect { customersResult ->
                    when (customersResult) {
                        is Result.Success -> {
                            val customers = customersResult.data
                            
                            // For now, use a simple list of vehicle models
                            // TODO: Replace with actual VehicleModel API when available
                            val mockVehicleModels = listOf(
                                SimpleVehicleModel("model-1", "Toyota Camry", "Toyota"),
                                SimpleVehicleModel("model-2", "Honda Civic", "Honda"),
                                SimpleVehicleModel("model-3", "Mazda CX-5", "Mazda"),
                                SimpleVehicleModel("model-4", "Hyundai Elantra", "Hyundai"),
                                SimpleVehicleModel("model-5", "Ford Focus", "Ford"),
                                SimpleVehicleModel("model-6", "BMW 320i", "BMW"),
                                SimpleVehicleModel("model-7", "Audi A4", "Audi"),
                                SimpleVehicleModel("model-8", "Mercedes C-Class", "Mercedes-Benz")
                            )
                            
                            _uiState.value = AddVehicleUiState.Editing(
                                formData = VehicleFormData(),
                                customers = customers,
                                vehicleModels = mockVehicleModels
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = AddVehicleUiState.Error("Failed to load customers: ${customersResult.message}")
                        }
                        else -> {
                            _uiState.value = AddVehicleUiState.Error("Unknown error loading customers")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AddVehicleUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }
    
    fun updateSelectedCustomer(customerId: String?) {
        val currentState = _uiState.value
        if (currentState is AddVehicleUiState.Editing) {
            val updatedFormData = currentState.formData.copy(selectedCustomerId = customerId)
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateSelectedVehicleModel(modelId: String?) {
        val currentState = _uiState.value
        if (currentState is AddVehicleUiState.Editing) {
            val updatedFormData = currentState.formData.copy(selectedModelId = modelId)
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateChassisNumber(chassisNumber: String) {
        val currentState = _uiState.value
        if (currentState is AddVehicleUiState.Editing) {
            val error = validateChassisNumber(chassisNumber)
            val updatedFormData = currentState.formData.copy(
                chassisNumber = chassisNumber,
                chassisNumberError = error
            )
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateLicensePlate(licensePlate: String) {
        val currentState = _uiState.value
        if (currentState is AddVehicleUiState.Editing) {
            val error = validateLicensePlate(licensePlate)
            val updatedFormData = currentState.formData.copy(
                licensePlate = licensePlate,
                licensePlateError = error
            )
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateColor(color: String) {
        val currentState = _uiState.value
        if (currentState is AddVehicleUiState.Editing) {
            val updatedFormData = currentState.formData.copy(color = color)
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateYear(year: String) {
        val currentState = _uiState.value
        if (currentState is AddVehicleUiState.Editing) {
            val error = validateYear(year)
            val updatedFormData = currentState.formData.copy(
                year = year,
                yearError = error
            )
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun updateInitialKM(initialKM: String) {
        val currentState = _uiState.value
        if (currentState is AddVehicleUiState.Editing) {
            val error = validateInitialKM(initialKM)
            val updatedFormData = currentState.formData.copy(
                initialKM = initialKM,
                initialKMError = error
            )
            _uiState.value = currentState.copy(formData = updatedFormData)
        }
    }
    
    fun createVehicle() {
        val currentState = _uiState.value
        if (currentState !is AddVehicleUiState.Editing || !currentState.formData.isValid) {
            return
        }
        
        viewModelScope.launch {
            _uiState.value = AddVehicleUiState.Loading
            
            try {
                val formData = currentState.formData
                val vehicle = CustomerVehicle(
                    vehicleID = "", // Will be generated by backend
                    chassisNumber = formData.chassisNumber,
                    licensePlate = formData.licensePlate,
                    color = formData.color,
                    year = formData.year.toIntOrNull() ?: 0,
                    initialKM = formData.initialKM.toDoubleOrNull() ?: 0.0,
                    customerID = formData.selectedCustomerId!!,
                    customerFullName = "",
                    modelID = formData.selectedModelId!!,
                    vehicleModelName = null,
                    vehicleBrandName = null
                )
                
                customerVehicleRepository.createVehicle(vehicle).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.value = AddVehicleUiState.Success(result.data)
                        }
                        is Result.Error -> {
                            _uiState.value = AddVehicleUiState.Error(result.message)
                        }
                        else -> {
                            _uiState.value = AddVehicleUiState.Error("Unknown error creating vehicle")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AddVehicleUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }
    
    private fun validateChassisNumber(chassisNumber: String): String? {
        return when {
            chassisNumber.isBlank() -> "Số khung không được để trống"
            chassisNumber.length < 5 -> "Số khung phải có ít nhất 5 ký tự"
            else -> null
        }
    }
    
    private fun validateLicensePlate(licensePlate: String): String? {
        return when {
            licensePlate.isBlank() -> "Biển số không được để trống"
            licensePlate.length < 7 -> "Biển số không hợp lệ"
            else -> null
        }
    }
    
    private fun validateYear(year: String): String? {
        if (year.isBlank()) return null // Optional field
        
        return when {
            year.toIntOrNull() == null -> "Năm sản xuất không hợp lệ"
            year.toInt() < 1900 || year.toInt() > 2030 -> "Năm sản xuất phải từ 1900 đến 2030"
            else -> null
        }
    }
    
    private fun validateInitialKM(initialKM: String): String? {
        if (initialKM.isBlank()) return null // Optional field
        
        return when {
            initialKM.toDoubleOrNull() == null -> "Số km ban đầu không hợp lệ"
            initialKM.toDouble() < 0 -> "Số km ban đầu không thể âm"
            else -> null
        }
    }
}

sealed class AddVehicleUiState {
    data class Editing(
        val formData: VehicleFormData,
        val customers: List<Customer>,
        val vehicleModels: List<SimpleVehicleModel>
    ) : AddVehicleUiState()
    object Loading : AddVehicleUiState()
    data class Success(val vehicle: CustomerVehicle) : AddVehicleUiState()
    data class Error(val message: String) : AddVehicleUiState()
}

data class VehicleFormData(
    val selectedCustomerId: String? = null,
    val selectedModelId: String? = null,
    val chassisNumber: String = "",
    val chassisNumberError: String? = null,
    val licensePlate: String = "",
    val licensePlateError: String? = null,
    val color: String = "",
    val year: String = "",
    val yearError: String? = null,
    val initialKM: String = "",
    val initialKMError: String? = null
) {
    val isValid: Boolean
        get() = selectedCustomerId != null &&
                selectedModelId != null &&
                chassisNumber.isNotBlank() &&
                chassisNumberError == null &&
                licensePlate.isNotBlank() &&
                licensePlateError == null &&
                yearError == null &&
                initialKMError == null
}

// Simple vehicle model for dropdown until we have proper VehicleModel API
data class SimpleVehicleModel(
    val id: String,
    val name: String,
    val brand: String
)
