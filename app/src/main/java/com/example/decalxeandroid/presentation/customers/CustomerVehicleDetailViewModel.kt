package com.example.decalxeandroid.presentation.customers

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import kotlinx.coroutines.launch

class CustomerVehicleDetailViewModel(
    private val customerVehicleRepository: CustomerVehicleRepository
) : ViewModel() {
    
    private val _uiState = mutableStateOf<CustomerVehicleDetailUiState>(CustomerVehicleDetailUiState.Loading)
    val uiState: State<CustomerVehicleDetailUiState> = _uiState
    
    fun loadVehicleData(vehicleId: String) {
        viewModelScope.launch {
            _uiState.value = CustomerVehicleDetailUiState.Loading
            
            try {
                customerVehicleRepository.getVehicleById(vehicleId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.value = CustomerVehicleDetailUiState.Success(
                                vehicle = result.data
                            )
                        }
                        is Result.Error -> {
                            _uiState.value = CustomerVehicleDetailUiState.Error(
                                message = result.message ?: "Lỗi không thể tải thông tin xe"
                            )
                        }
                        is Result.Loading -> {
                            _uiState.value = CustomerVehicleDetailUiState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CustomerVehicleDetailUiState.Error(
                    message = "Lỗi không xác định: ${e.message}"
                )
            }
        }
    }
    
    fun deleteVehicle(vehicleId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                customerVehicleRepository.deleteVehicle(vehicleId).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            onSuccess()
                        }
                        is Result.Error -> {
                            _uiState.value = CustomerVehicleDetailUiState.Error(
                                message = result.message ?: "Lỗi không thể xóa xe"
                            )
                        }
                        is Result.Loading -> {
                            _uiState.value = CustomerVehicleDetailUiState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CustomerVehicleDetailUiState.Error(
                    message = "Lỗi không xác định: ${e.message}"
                )
            }
        }
    }
}

sealed class CustomerVehicleDetailUiState {
    object Loading : CustomerVehicleDetailUiState()
    data class Success(val vehicle: CustomerVehicle) : CustomerVehicleDetailUiState()
    data class Error(val message: String) : CustomerVehicleDetailUiState()
}

class CustomerVehicleDetailViewModelFactory(
    private val customerVehicleRepository: CustomerVehicleRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerVehicleDetailViewModel::class.java)) {
            return CustomerVehicleDetailViewModel(customerVehicleRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
