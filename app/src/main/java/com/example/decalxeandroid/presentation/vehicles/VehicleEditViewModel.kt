package com.example.decalxeandroid.presentation.vehicles

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.data.dto.UpdateCustomerVehicleDto
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import java.util.*

class VehicleEditViewModel(
    private val vehicleId: String,
    private val customerVehicleRepository: CustomerVehicleRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<VehicleEditUiState>(VehicleEditUiState.LoadingVehicle)
    val uiState: StateFlow<VehicleEditUiState> = _uiState.asStateFlow()
    
    companion object {
        private const val TAG = "VehicleEditViewModel"
    }
    
    init {
        loadVehicle()
    }
    
    private fun loadVehicle() {
        viewModelScope.launch {
            _uiState.value = VehicleEditUiState.LoadingVehicle
            Log.d(TAG, "Loading vehicle for edit: $vehicleId")
            
            try {
                customerVehicleRepository.getVehicleById(vehicleId).collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            val vehicle = result.data
                            Log.d(TAG, "Successfully loaded vehicle: ${vehicle.licensePlate}")
                            
                            val formData = VehicleEditFormData(
                                chassisNumber = vehicle.chassisNumber ?: "",
                                licensePlate = vehicle.licensePlate,
                                color = vehicle.color,
                                year = if (vehicle.year > 0) vehicle.year.toString() else "",
                                initialKM = if (vehicle.initialKM > 0.0) vehicle.initialKM.toString() else "",
                                modelId = vehicle.modelID ?: "",
                                
                                // Initial validation
                                chassisNumberError = validateChassisNumber(vehicle.chassisNumber ?: ""),
                                licensePlateError = validateLicensePlate(vehicle.licensePlate),
                                colorError = validateColor(vehicle.color),
                                yearError = validateYear(if (vehicle.year > 0) vehicle.year.toString() else ""),
                                initialKMError = validateInitialKM(if (vehicle.initialKM > 0.0) vehicle.initialKM.toString() else ""),
                                modelIdError = validateModelId(vehicle.modelID ?: "")
                            )
                            
                            _uiState.value = VehicleEditUiState.Editing(formData)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            Log.e(TAG, "Failed to load vehicle: ${result.message}")
                            _uiState.value = VehicleEditUiState.Error(
                                "Không thể tải thông tin xe: ${result.message}",
                                VehicleEditFormData()
                            )
                        }
                        else -> {
                            Log.e(TAG, "Unknown result type")
                            _uiState.value = VehicleEditUiState.Error(
                                "Lỗi không xác định khi tải thông tin xe",
                                VehicleEditFormData()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading vehicle", e)
                _uiState.value = VehicleEditUiState.Error(
                    "Lỗi kết nối: ${e.message}",
                    VehicleEditFormData()
                )
            }
        }
    }
    
    fun updateChassisNumber(chassisNumber: String) {
        val currentState = uiState.value as? VehicleEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            chassisNumber = chassisNumber,
            chassisNumberError = validateChassisNumber(chassisNumber)
        )
        _uiState.value = VehicleEditUiState.Editing(updatedFormData)
    }
    
    fun updateLicensePlate(licensePlate: String) {
        val currentState = uiState.value as? VehicleEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            licensePlate = licensePlate,
            licensePlateError = validateLicensePlate(licensePlate)
        )
        _uiState.value = VehicleEditUiState.Editing(updatedFormData)
    }
    
    fun updateColor(color: String) {
        val currentState = uiState.value as? VehicleEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            color = color,
            colorError = validateColor(color)
        )
        _uiState.value = VehicleEditUiState.Editing(updatedFormData)
    }
    
    fun updateYear(year: String) {
        val currentState = uiState.value as? VehicleEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            year = year,
            yearError = validateYear(year)
        )
        _uiState.value = VehicleEditUiState.Editing(updatedFormData)
    }
    
    fun updateInitialKM(initialKM: String) {
        val currentState = uiState.value as? VehicleEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            initialKM = initialKM,
            initialKMError = validateInitialKM(initialKM)
        )
        _uiState.value = VehicleEditUiState.Editing(updatedFormData)
    }
    
    fun updateModelId(modelId: String) {
        val currentState = uiState.value as? VehicleEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            modelId = modelId,
            modelIdError = validateModelId(modelId)
        )
        _uiState.value = VehicleEditUiState.Editing(updatedFormData)
    }
    
    fun updateVehicle() {
        val currentState = uiState.value as? VehicleEditUiState.Editing ?: return
        val formData = currentState.formData
        
        if (!formData.isValid) {
            Log.w(TAG, "Form is not valid, cannot update vehicle")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = VehicleEditUiState.Loading(formData)
            Log.d(TAG, "Updating vehicle: $vehicleId")
            
            try {
                // Create UpdateCustomerVehicleDto with updated data
                val updateDto = UpdateCustomerVehicleDto(
                    customerID = null, // Will be preserved by backend
                    chassisNumber = formData.chassisNumber.trim().takeIf { it.isNotEmpty() },
                    licensePlate = formData.licensePlate.trim().takeIf { it.isNotEmpty() },
                    color = formData.color.trim().takeIf { it.isNotEmpty() },
                    year = formData.year.trim().toIntOrNull(),
                    initialKM = formData.initialKM.trim().toDoubleOrNull(),
                    modelID = formData.modelId.trim().takeIf { it.isNotEmpty() }
                )
                
                customerVehicleRepository.updateVehicle(vehicleId, updateDto).collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            Log.d(TAG, "Successfully updated vehicle: ${result.data.licensePlate}")
                            _uiState.value = VehicleEditUiState.Success(result.data)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            Log.e(TAG, "Failed to update vehicle: ${result.message}")
                            _uiState.value = VehicleEditUiState.Error(
                                "Không thể cập nhật thông tin xe: ${result.message}",
                                formData
                            )
                        }
                        else -> {
                            Log.e(TAG, "Unknown result type")
                            _uiState.value = VehicleEditUiState.Error(
                                "Lỗi không xác định khi cập nhật thông tin xe",
                                formData
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception updating vehicle", e)
                _uiState.value = VehicleEditUiState.Error(
                    "Lỗi kết nối: ${e.message}",
                    formData
                )
            }
        }
    }
    
    fun resetError() {
        val currentState = uiState.value as? VehicleEditUiState.Error ?: return
        _uiState.value = VehicleEditUiState.Editing(currentState.formData)
    }
    
    // Validation functions
    private fun validateChassisNumber(chassisNumber: String): String? {
        if (chassisNumber.isBlank()) return null // Optional field
        return if (chassisNumber.length < 10) {
            "Số khung xe phải có ít nhất 10 ký tự"
        } else null
    }
    
    private fun validateLicensePlate(licensePlate: String): String? {
        return when {
            licensePlate.isBlank() -> "Biển số xe không được để trống"
            licensePlate.length < 6 -> "Biển số xe phải có ít nhất 6 ký tự"
            else -> null
        }
    }
    
    private fun validateColor(color: String): String? {
        return when {
            color.isBlank() -> "Màu xe không được để trống"
            color.length < 2 -> "Màu xe phải có ít nhất 2 ký tự"
            else -> null
        }
    }
    
    private fun validateYear(year: String): String? {
        if (year.isBlank()) return null // Optional field
        val yearInt = year.toIntOrNull()
        return when {
            yearInt == null -> "Năm sản xuất phải là số hợp lệ"
            yearInt < 1900 -> "Năm sản xuất phải từ 1900 trở lên"
            yearInt > Calendar.getInstance().get(Calendar.YEAR) + 1 -> "Năm sản xuất không thể lớn hơn năm hiện tại"
            else -> null
        }
    }
    
    private fun validateInitialKM(initialKM: String): String? {
        if (initialKM.isBlank()) return null // Optional field
        val kmDouble = initialKM.toDoubleOrNull()
        return when {
            kmDouble == null -> "Số km phải là số hợp lệ"
            kmDouble < 0 -> "Số km phải lớn hơn hoặc bằng 0"
            kmDouble > 1000000 -> "Số km quá lớn (tối đa 1,000,000 km)"
            else -> null
        }
    }
    
    private fun validateModelId(modelId: String): String? {
        if (modelId.isBlank()) return null // Optional field
        return if (modelId.length < 3) {
            "ID Model xe phải có ít nhất 3 ký tự"
        } else null
    }
}

data class VehicleEditFormData(
    val chassisNumber: String = "",
    val licensePlate: String = "",
    val color: String = "",
    val year: String = "",
    val initialKM: String = "",
    val modelId: String = "",
    
    // Error states
    val chassisNumberError: String? = null,
    val licensePlateError: String? = null,
    val colorError: String? = null,
    val yearError: String? = null,
    val initialKMError: String? = null,
    val modelIdError: String? = null
) {
    val isValid: Boolean
        get() = chassisNumberError == null &&
                licensePlateError == null &&
                colorError == null &&
                yearError == null &&
                initialKMError == null &&
                modelIdError == null &&
                licensePlate.isNotBlank() &&
                color.isNotBlank()
}

sealed class VehicleEditUiState {
    object LoadingVehicle : VehicleEditUiState()
    data class Editing(val formData: VehicleEditFormData) : VehicleEditUiState()
    data class Loading(val formData: VehicleEditFormData) : VehicleEditUiState()
    data class Error(val message: String, val formData: VehicleEditFormData) : VehicleEditUiState()
    data class Success(val vehicle: CustomerVehicle) : VehicleEditUiState()
}
