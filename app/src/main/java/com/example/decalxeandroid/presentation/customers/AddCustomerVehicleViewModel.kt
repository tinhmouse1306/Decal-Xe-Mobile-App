package com.example.decalxeandroid.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.data.dto.VehicleModelDto
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import com.example.decalxeandroid.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddCustomerVehicleViewModel(
        private val customerVehicleRepository: CustomerVehicleRepository,
        private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val _uiState =
            MutableStateFlow<AddCustomerVehicleUiState>(
                    AddCustomerVehicleUiState.Editing(VehicleFormData())
            )
    val uiState: StateFlow<AddCustomerVehicleUiState> = _uiState.asStateFlow()

    private val _vehicleModels = MutableStateFlow<List<VehicleModelDto>>(emptyList())
    val vehicleModels: StateFlow<List<VehicleModelDto>> = _vehicleModels.asStateFlow()

    private var _formData = VehicleFormData()

    fun updateChassisNumber(chassisNumber: String) {
        _formData =
                _formData.copy(
                        chassisNumber = chassisNumber,
                        chassisNumberError = validateChassisNumber(chassisNumber)
                )
        _uiState.value = AddCustomerVehicleUiState.Editing(_formData)
    }

    fun updateLicensePlate(licensePlate: String) {
        _formData =
                _formData.copy(
                        licensePlate = licensePlate,
                        licensePlateError = validateLicensePlate(licensePlate)
                )
        _uiState.value = AddCustomerVehicleUiState.Editing(_formData)
    }

    fun updateColor(color: String) {
        _formData = _formData.copy(color = color, colorError = validateColor(color))
        _uiState.value = AddCustomerVehicleUiState.Editing(_formData)
    }

    fun updateYear(year: String) {
        _formData = _formData.copy(year = year, yearError = validateYear(year))
        _uiState.value = AddCustomerVehicleUiState.Editing(_formData)
    }

    fun updateInitialKM(initialKM: String) {
        _formData =
                _formData.copy(initialKM = initialKM, initialKMError = validateInitialKM(initialKM))
        _uiState.value = AddCustomerVehicleUiState.Editing(_formData)
    }

    fun updateModelId(modelId: String) {
        _formData = _formData.copy(modelId = modelId, modelIdError = validateModelId(modelId))
        _uiState.value = AddCustomerVehicleUiState.Editing(_formData)
    }

    fun updateModelSelection(vehicleModel: VehicleModelDto) {
        _formData =
                _formData.copy(
                        modelId = vehicleModel.modelID,
                        modelName = vehicleModel.modelName,
                        modelIdError = validateModelId(vehicleModel.modelID)
                )
        _uiState.value = AddCustomerVehicleUiState.Editing(_formData)
    }

    fun createVehicle(customerId: String) {
        android.util.Log.d(
                "AddCustomerVehicleVM",
                "createVehicle called with customerId: $customerId"
        )
        android.util.Log.d("AddCustomerVehicleVM", "Form data: $_formData")
        android.util.Log.d("AddCustomerVehicleVM", "Form isValid: ${_formData.isValid}")

        if (!_formData.isValid) {
            android.util.Log.e("AddCustomerVehicleVM", "Form validation failed")
            _uiState.value =
                    AddCustomerVehicleUiState.Error("Vui lòng điền đầy đủ thông tin bắt buộc")
            return
        }

        _uiState.value = AddCustomerVehicleUiState.Loading
        android.util.Log.d("AddCustomerVehicleVM", "Starting vehicle creation...")

        viewModelScope.launch {
            try {
                val vehicle =
                        CustomerVehicle(
                                vehicleID = "", // Will be set by backend
                                chassisNumber = _formData.chassisNumber,
                                licensePlate = _formData.licensePlate,
                                color = _formData.color,
                                year = _formData.year.toIntOrNull() ?: 2023,
                                initialKM = _formData.initialKM.toDoubleOrNull() ?: 0.0,
                                customerID = customerId,
                                customerFullName = "Khách hàng Mới",
                                modelID = _formData.modelId.ifBlank { "" },
                                vehicleModelName = null,
                                vehicleBrandName = null
                        )

                android.util.Log.d("AddCustomerVehicleVM", "Created vehicle object: $vehicle")

                val result = customerVehicleRepository.createVehicle(vehicle)
                result.collect { result ->
                    android.util.Log.d("AddCustomerVehicleVM", "Repository result: $result")
                    when (result) {
                        is Result.Loading -> {
                            android.util.Log.d("AddCustomerVehicleVM", "Result: Loading")
                            _uiState.value = AddCustomerVehicleUiState.Loading
                        }
                        is Result.Success -> {
                            android.util.Log.d(
                                    "AddCustomerVehicleVM",
                                    "Result: Success - ${result.data}"
                            )
                            _uiState.value = AddCustomerVehicleUiState.Success(result.data)
                        }
                        is Result.Error -> {
                            android.util.Log.e(
                                    "AddCustomerVehicleVM",
                                    "Result: Error - ${result.message}"
                            )
                            _uiState.value = AddCustomerVehicleUiState.Error(result.message)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AddCustomerVehicleVM", "Exception during vehicle creation", e)
                _uiState.value = AddCustomerVehicleUiState.Error("Có lỗi xảy ra: ${e.message}")
            }
        }
    }

    fun loadVehicleData() {
        viewModelScope.launch {
            try {
                vehicleRepository.getVehicleModels().collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success<*> -> {
                            @Suppress("UNCHECKED_CAST")
                            _vehicleModels.value = result.data as List<VehicleModelDto>
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            android.util.Log.e(
                                    "AddCustomerVehicleVM",
                                    "Failed to load vehicle models: ${result.message}"
                            )
                            _vehicleModels.value = emptyList()
                        }
                        is com.example.decalxeandroid.domain.model.Result.Loading -> {
                            // Keep current value
                        }
                    }
                }
            } catch (e: Exception) {
                _vehicleModels.value = emptyList()
            }
        }
        _uiState.value = AddCustomerVehicleUiState.Editing(_formData)
    }

    private fun validateChassisNumber(chassisNumber: String): String? {
        return when {
            chassisNumber.isBlank() -> "Số khung xe không được để trống"
            chassisNumber.length < 10 -> "Số khung xe phải có ít nhất 10 ký tự"
            else -> null
        }
    }

    private fun validateLicensePlate(licensePlate: String): String? {
        return when {
            licensePlate.isBlank() -> "Biển số xe không được để trống"
            licensePlate.length < 8 -> "Biển số xe phải có ít nhất 8 ký tự"
            else -> null
        }
    }

    private fun validateColor(color: String): String? {
        return when {
            color.isBlank() -> "Màu sắc không được để trống"
            else -> null
        }
    }

    private fun validateYear(year: String): String? {
        return when {
            year.isBlank() -> "Năm sản xuất không được để trống"
            else -> {
                val yearInt = year.toIntOrNull()
                when {
                    yearInt == null -> "Năm sản xuất phải là số"
                    yearInt < 1900 -> "Năm sản xuất phải từ 1900 trở lên"
                    yearInt > 2024 -> "Năm sản xuất không được vượt quá năm hiện tại"
                    else -> null
                }
            }
        }
    }

    private fun validateInitialKM(initialKM: String): String? {
        if (initialKM.isBlank()) return null

        val kmInt = initialKM.toIntOrNull()
        return when {
            kmInt == null -> "Số km phải là số"
            kmInt < 0 -> "Số km không được âm"
            kmInt > 1000000 -> "Số km không hợp lý"
            else -> null
        }
    }

    private fun validateModelId(modelId: String): String? {
        // Model ID is optional, so no validation needed
        return null
    }
}

class AddCustomerVehicleViewModelFactory(
        private val customerVehicleRepository: CustomerVehicleRepository,
        private val vehicleRepository: VehicleRepository
) : androidx.lifecycle.ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddCustomerVehicleViewModel::class.java)) {
            return AddCustomerVehicleViewModel(customerVehicleRepository, vehicleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
