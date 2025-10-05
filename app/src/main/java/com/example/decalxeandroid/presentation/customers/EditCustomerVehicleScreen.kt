package com.example.decalxeandroid.presentation.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.data.dto.UpdateCustomerVehicleDto
import com.example.decalxeandroid.data.dto.VehicleModelDto
import com.example.decalxeandroid.domain.model.CustomerVehicle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun EditCustomerVehicleScreen(
        vehicleId: String,
        onNavigateBack: () -> Unit,
        onVehicleUpdated: (CustomerVehicle) -> Unit,
        viewModel: EditCustomerVehicleViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
                        factory =
                                EditCustomerVehicleViewModelFactory(
                                        customerVehicleRepository =
                                                com.example.decalxeandroid.di.AppContainer
                                                        .customerVehicleRepository,
                                        vehicleRepository =
                                                com.example.decalxeandroid.di.AppContainer
                                                        .vehicleRepository
                                )
                )
) {
    val uiState by viewModel.uiState
    val vehicleModels by viewModel.vehicleModels

    // Load vehicle data and initialize form
    LaunchedEffect(vehicleId) { viewModel.loadAndInitializeVehicle(vehicleId) }

    LaunchedEffect(Unit) { viewModel.loadVehicleData() }

    LaunchedEffect(uiState) {
        if (uiState is EditCustomerVehicleUiState.Success) {
            onVehicleUpdated((uiState as EditCustomerVehicleUiState.Success).vehicle)
        }
    }

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(
                                    brush =
                                            Brush.verticalGradient(
                                                    colors =
                                                            listOf(
                                                                    Color(0xFF667eea),
                                                                    Color(0xFF764ba2),
                                                                    Color(0xFFf093fb)
                                                            )
                                            )
                            )
    ) {
        // Modern Top App Bar
        when (uiState) {
            is EditCustomerVehicleUiState.Editing -> {
                val editingState = uiState as EditCustomerVehicleUiState.Editing
                CustomerModernTopAppBar(
                        title = "Chỉnh sửa thông tin xe",
                        subtitle = "Cập nhật thông tin xe ${editingState.formData.licensePlate}",
                        onNavigateBack = onNavigateBack
                )
            }
            else -> {
                CustomerModernTopAppBar(
                        title = "Chỉnh sửa thông tin xe",
                        subtitle = "Đang tải thông tin xe...",
                        onNavigateBack = onNavigateBack
                )
            }
        }

        Column(
                modifier =
                        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Card
            when (uiState) {
                is EditCustomerVehicleUiState.Editing -> {
                    val editingState = uiState as EditCustomerVehicleUiState.Editing
                    ModernWelcomeCard(
                            title = "Chỉnh sửa thông tin xe",
                            subtitle = "Cập nhật thông tin xe ${editingState.formData.licensePlate}"
                    )
                }
                else -> {
                    ModernWelcomeCard(
                            title = "Chỉnh sửa thông tin xe",
                            subtitle = "Đang tải thông tin xe..."
                    )
                }
            }

            // Edit Form
            when (uiState) {
                is EditCustomerVehicleUiState.Editing -> {
                    EditCustomerVehicleForm(
                            uiState = uiState,
                            vehicleModels = vehicleModels,
                            onChassisNumberChange = { viewModel.updateChassisNumber(it) },
                            onLicensePlateChange = { viewModel.updateLicensePlate(it) },
                            onColorChange = { viewModel.updateColor(it) },
                            onYearChange = { viewModel.updateYear(it) },
                            onInitialKMChange = { viewModel.updateInitialKM(it) },
                            onModelIdChange = { viewModel.updateModelId(it) },
                            onModelSelectionChange = { viewModel.updateModelSelection(it) },
                            onSubmit = { viewModel.updateVehicle(vehicleId) }
                    )
                }
                is EditCustomerVehicleUiState.Loading -> {
                    Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                    ) { CircularProgressIndicator(color = Color.White) }
                }
                is EditCustomerVehicleUiState.Error -> {
                    CustomerModernErrorCard(
                            message = (uiState as EditCustomerVehicleUiState.Error).message
                    )
                }
                is EditCustomerVehicleUiState.Success -> {
                    // This will trigger navigation via LaunchedEffect
                }
            }
        }
    }
}

@Composable
fun EditCustomerVehicleForm(
        uiState: EditCustomerVehicleUiState,
        vehicleModels: List<VehicleModelDto>,
        onChassisNumberChange: (String) -> Unit,
        onLicensePlateChange: (String) -> Unit,
        onColorChange: (String) -> Unit,
        onYearChange: (String) -> Unit,
        onInitialKMChange: (String) -> Unit,
        onModelIdChange: (String) -> Unit,
        onModelSelectionChange: (VehicleModelDto) -> Unit,
        onSubmit: () -> Unit
) {
    val formData =
            when (uiState) {
                is EditCustomerVehicleUiState.Editing -> uiState.formData
                else -> VehicleFormData()
            }

    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp)
    ) {
        Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                    text = "Thông tin xe",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )

            // Chassis Number
            CustomerModernOutlinedTextField(
                    value = formData.chassisNumber,
                    onValueChange = onChassisNumberChange,
                    label = "Số khung xe *",
                    leadingIcon = Icons.Default.ConfirmationNumber,
                    keyboardType = KeyboardType.Text,
                    isError = formData.chassisNumberError != null,
                    errorMessage = formData.chassisNumberError
            )

            // License Plate
            CustomerModernOutlinedTextField(
                    value = formData.licensePlate,
                    onValueChange = onLicensePlateChange,
                    label = "Biển số xe *",
                    leadingIcon = Icons.Default.DirectionsCar,
                    keyboardType = KeyboardType.Text,
                    isError = formData.licensePlateError != null,
                    errorMessage = formData.licensePlateError
            )

            // Color Dropdown
            VietnameseDropdownField(
                    value = formData.color,
                    onValueChange = onColorChange,
                    label = "Màu sắc *",
                    leadingIcon = Icons.Default.Palette,
                    options =
                            listOf(
                                    "Đen",
                                    "Trắng",
                                    "Xám",
                                    "Bạc",
                                    "Đỏ",
                                    "Xanh dương",
                                    "Xanh lá",
                                    "Vàng",
                                    "Cam",
                                    "Tím",
                                    "Nâu"
                            ),
                    isError = formData.colorError != null,
                    errorMessage = formData.colorError
            )

            // Year
            CustomerModernOutlinedTextField(
                    value = formData.year,
                    onValueChange = onYearChange,
                    label = "Năm sản xuất *",
                    leadingIcon = Icons.Default.CalendarToday,
                    keyboardType = KeyboardType.Number,
                    isError = formData.yearError != null,
                    errorMessage = formData.yearError
            )

            // Initial KM
            CustomerModernOutlinedTextField(
                    value = formData.initialKM,
                    onValueChange = onInitialKMChange,
                    label = "Số km ban đầu *",
                    leadingIcon = Icons.Default.Speed,
                    keyboardType = KeyboardType.Number,
                    isError = formData.initialKMError != null,
                    errorMessage = formData.initialKMError
            )

            // Vehicle Model Dropdown
            VehicleModelDropdown(
                    value = formData.modelName,
                    onValueChange = onModelIdChange,
                    onModelSelectionChange = onModelSelectionChange,
                    vehicleModels = vehicleModels,
                    isError = formData.modelIdError != null,
                    errorMessage = formData.modelIdError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formData.isValid,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea)),
                    shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                        text = "Cập nhật thông tin xe",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ViewModel for Edit Customer Vehicle
class EditCustomerVehicleViewModel(
        private val customerVehicleRepository:
                com.example.decalxeandroid.domain.repository.CustomerVehicleRepository,
        private val vehicleRepository:
                com.example.decalxeandroid.domain.repository.VehicleRepository
) : androidx.lifecycle.ViewModel() {

    private val _uiState =
            androidx.compose.runtime.mutableStateOf<EditCustomerVehicleUiState>(
                    EditCustomerVehicleUiState.Editing(VehicleFormData())
            )
    val uiState: androidx.compose.runtime.State<EditCustomerVehicleUiState> = _uiState

    private val _vehicleModels =
            androidx.compose.runtime.mutableStateOf<List<VehicleModelDto>>(emptyList())
    val vehicleModels: androidx.compose.runtime.State<List<VehicleModelDto>> = _vehicleModels

    private var _formData = VehicleFormData()
    private var _customerID = ""

    fun loadAndInitializeVehicle(vehicleId: String) {
        viewModelScope.launch {
            _uiState.value = EditCustomerVehicleUiState.Loading

            try {
                customerVehicleRepository.getVehicleById(vehicleId).collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            val vehicle = result.data
                            _customerID = vehicle.customerID
                            _formData =
                                    VehicleFormData(
                                            chassisNumber = vehicle.chassisNumber,
                                            licensePlate = vehicle.licensePlate ?: "",
                                            color = vehicle.color ?: "",
                                            year = vehicle.year?.toString() ?: "",
                                            initialKM = vehicle.initialKM?.toString() ?: "",
                                            modelId = vehicle.modelID,
                                            modelName = vehicle.vehicleModelName ?: ""
                                    )
                            _uiState.value = EditCustomerVehicleUiState.Editing(_formData)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value =
                                    EditCustomerVehicleUiState.Error(
                                            result.message ?: "Lỗi không thể tải thông tin xe"
                                    )
                        }
                        is com.example.decalxeandroid.domain.model.Result.Loading -> {
                            _uiState.value = EditCustomerVehicleUiState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value =
                        EditCustomerVehicleUiState.Error("Lỗi không xác định: ${e.message}")
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
                                    "EditCustomerVehicleVM",
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
    }

    fun updateChassisNumber(chassisNumber: String) {
        _formData =
                _formData.copy(
                        chassisNumber = chassisNumber,
                        chassisNumberError = validateChassisNumber(chassisNumber)
                )
        _uiState.value = EditCustomerVehicleUiState.Editing(_formData)
    }

    fun updateLicensePlate(licensePlate: String) {
        _formData =
                _formData.copy(
                        licensePlate = licensePlate,
                        licensePlateError = validateLicensePlate(licensePlate)
                )
        _uiState.value = EditCustomerVehicleUiState.Editing(_formData)
    }

    fun updateColor(color: String) {
        _formData = _formData.copy(color = color, colorError = validateColor(color))
        _uiState.value = EditCustomerVehicleUiState.Editing(_formData)
    }

    fun updateYear(year: String) {
        _formData = _formData.copy(year = year, yearError = validateYear(year))
        _uiState.value = EditCustomerVehicleUiState.Editing(_formData)
    }

    fun updateInitialKM(initialKM: String) {
        _formData =
                _formData.copy(initialKM = initialKM, initialKMError = validateInitialKM(initialKM))
        _uiState.value = EditCustomerVehicleUiState.Editing(_formData)
    }

    fun updateModelId(modelId: String) {
        _formData = _formData.copy(modelId = modelId, modelIdError = validateModelId(modelId))
        _uiState.value = EditCustomerVehicleUiState.Editing(_formData)
    }

    fun updateModelSelection(vehicleModel: VehicleModelDto) {
        _formData =
                _formData.copy(
                        modelId = vehicleModel.modelID,
                        modelName = vehicleModel.modelName,
                        modelIdError = validateModelId(vehicleModel.modelID)
                )
        _uiState.value = EditCustomerVehicleUiState.Editing(_formData)
    }

    fun updateVehicle(vehicleId: String) {
        if (!_formData.isValid) {
            return
        }

        viewModelScope.launch {
            _uiState.value = EditCustomerVehicleUiState.Loading

            try {
                // Create update DTO
                val updateDto =
                        UpdateCustomerVehicleDto(
                                chassisNumber = _formData.chassisNumber,
                                licensePlate = _formData.licensePlate,
                                color = _formData.color,
                                year = _formData.year.toIntOrNull(),
                                initialKM = _formData.initialKM.toDoubleOrNull(),
                                modelID = _formData.modelId
                        )

                // Update vehicle via repository
                customerVehicleRepository.updateVehicle(vehicleId, updateDto).collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            _uiState.value = EditCustomerVehicleUiState.Success(result.data)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value =
                                    EditCustomerVehicleUiState.Error(
                                            result.message ?: "Lỗi không xác định"
                                    )
                        }
                        is com.example.decalxeandroid.domain.model.Result.Loading -> {
                            _uiState.value = EditCustomerVehicleUiState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value =
                        EditCustomerVehicleUiState.Error("Lỗi không xác định: ${e.message}")
            }
        }
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
        val yearInt = year.toIntOrNull()
        return when {
            year.isBlank() -> "Năm sản xuất không được để trống"
            yearInt == null -> "Năm sản xuất phải là số"
            yearInt < 1900 || yearInt > 2024 -> "Năm sản xuất phải từ 1900 đến 2024"
            else -> null
        }
    }

    private fun validateInitialKM(initialKM: String): String? {
        val kmDouble = initialKM.toDoubleOrNull()
        return when {
            initialKM.isBlank() -> "Số km ban đầu không được để trống"
            kmDouble == null -> "Số km ban đầu phải là số"
            kmDouble < 0 -> "Số km ban đầu không được âm"
            else -> null
        }
    }

    private fun validateModelId(modelId: String): String? {
        return null // Model ID is optional
    }
}

// UI State for Edit Customer Vehicle
sealed class EditCustomerVehicleUiState {
    data class Editing(val formData: VehicleFormData) : EditCustomerVehicleUiState()
    object Loading : EditCustomerVehicleUiState()
    data class Success(val vehicle: CustomerVehicle) : EditCustomerVehicleUiState()
    data class Error(val message: String) : EditCustomerVehicleUiState()
}

// ViewModel Factory
class EditCustomerVehicleViewModelFactory(
        private val customerVehicleRepository:
                com.example.decalxeandroid.domain.repository.CustomerVehicleRepository,
        private val vehicleRepository:
                com.example.decalxeandroid.domain.repository.VehicleRepository
) : androidx.lifecycle.ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditCustomerVehicleViewModel::class.java)) {
            return EditCustomerVehicleViewModel(customerVehicleRepository, vehicleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
