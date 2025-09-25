package com.example.decalxeandroid.presentation.vehicles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class VehiclesViewModel(
    private val vehicleRepository: CustomerVehicleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehiclesUiState())
    val uiState: StateFlow<VehiclesUiState> = _uiState.asStateFlow()

    fun loadVehicles() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                vehicleRepository.getVehicles().collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                vehicles = result.data,
                                error = null
                            )
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                        else -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Kết quả không xác định"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Có lỗi xảy ra khi tải danh sách xe"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class VehiclesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val vehicles: List<CustomerVehicle> = emptyList()
)
