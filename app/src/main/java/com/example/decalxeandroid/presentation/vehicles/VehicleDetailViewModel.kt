package com.example.decalxeandroid.presentation.vehicles

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import com.example.decalxeandroid.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class VehicleDetailViewModel(
    private val vehicleId: String,
    private val customerVehicleRepository: CustomerVehicleRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "VehicleDetailViewModel"
    }
    
    private val _uiState = MutableStateFlow<VehicleDetailUiState>(VehicleDetailUiState.Loading)
    val uiState: StateFlow<VehicleDetailUiState> = _uiState.asStateFlow()
    
    private val _showDeleteConfirmDialog = MutableStateFlow(false)
    val showDeleteConfirmDialog: StateFlow<Boolean> = _showDeleteConfirmDialog.asStateFlow()
    
    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()
    
    init {
        loadVehicle()
    }
    
    fun loadVehicle() {
        viewModelScope.launch {
            Log.d(TAG, "Loading vehicle with ID: $vehicleId")
            _uiState.value = VehicleDetailUiState.Loading
            
            try {
                // Validate vehicleId first
                if (vehicleId.isBlank()) {
                    Log.e(TAG, "Vehicle ID is blank")
                    _uiState.value = VehicleDetailUiState.Error("ID xe không hợp lệ")
                    return@launch
                }
                
                // Load vehicle details
                Log.d(TAG, "Fetching vehicle details from repository")
                val vehicleResult = customerVehicleRepository.getVehicleById(vehicleId)
                vehicleResult.collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            val vehicle = result.data
                            Log.d(TAG, "Successfully loaded vehicle: ${vehicle.vehicleID} - ${vehicle.licensePlate}")
                            
                            // Load vehicle orders (but don't fail if orders can't be loaded)
                            try {
                                Log.d(TAG, "Loading orders for vehicle: ${vehicle.vehicleID}")
                                val ordersFlow = orderRepository.getOrdersByVehicleId(vehicleId)
                                ordersFlow.collect { ordersResult ->
                                    when (ordersResult) {
                                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                                            val orders = ordersResult.data
                                            _uiState.value = VehicleDetailUiState.Success(
                                                vehicle = vehicle,
                                                orders = orders
                                            )
                                        }
                                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                                            // Still show vehicle info even if orders fail to load
                                            _uiState.value = VehicleDetailUiState.Success(
                                                vehicle = vehicle,
                                                orders = emptyList()
                                            )
                                        }
                                        else -> {
                                            // Still show vehicle info even if orders fail to load
                                            _uiState.value = VehicleDetailUiState.Success(
                                                vehicle = vehicle,
                                                orders = emptyList()
                                            )
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                // Still show vehicle info even if orders fail to load
                                _uiState.value = VehicleDetailUiState.Success(
                                    vehicle = vehicle,
                                    orders = emptyList()
                                )
                            }
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            Log.e(TAG, "Error loading vehicle: ${result.message}")
                            val errorMessage = when {
                                result.message.contains("404") -> "Xe không tồn tại hoặc đã bị xóa"
                                result.message.contains("Network") -> "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet"
                                result.message.contains("timeout") -> "Kết nối bị timeout. Vui lòng thử lại"
                                else -> "Không thể tải thông tin xe: ${result.message}"
                            }
                            _uiState.value = VehicleDetailUiState.Error(errorMessage)
                        }
                        else -> {
                            _uiState.value = VehicleDetailUiState.Error(
                                "Có lỗi không xác định xảy ra. Vui lòng thử lại"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng"
                    e.message?.contains("timeout") == true -> "Kết nối bị timeout. Vui lòng thử lại"
                    else -> "Lỗi không xác định: ${e.message}"
                }
                _uiState.value = VehicleDetailUiState.Error(errorMessage)
            }
        }
    }
    
    fun editVehicle(onNavigateToEdit: (String) -> Unit) {
        Log.d(TAG, "Navigating to edit vehicle: $vehicleId")
        onNavigateToEdit(vehicleId)
    }
    
    fun showDeleteConfirmDialog() {
        _showDeleteConfirmDialog.value = true
    }
    
    fun hideDeleteConfirmDialog() {
        _showDeleteConfirmDialog.value = false
    }
    
    fun deleteVehicle(onNavigateBack: () -> Unit) {
        viewModelScope.launch {
            try {
                _deleteState.value = DeleteState.Loading
                _showDeleteConfirmDialog.value = false
                
                Log.d(TAG, "Deleting vehicle with ID: $vehicleId")
                val result = customerVehicleRepository.deleteVehicle(vehicleId)
                result.collect { deleteResult ->
                    when (deleteResult) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            Log.d(TAG, "Successfully deleted vehicle: $vehicleId")
                            _deleteState.value = DeleteState.Success("Đã xóa xe thành công")
                            // Navigate back after successful deletion
                            onNavigateBack()
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            Log.e(TAG, "Error deleting vehicle: ${deleteResult.message}")
                            val errorMessage = when {
                                deleteResult.message.contains("404") -> "Xe không tồn tại hoặc đã bị xóa"
                                deleteResult.message.contains("Network") -> "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet"
                                deleteResult.message.contains("timeout") -> "Kết nối bị timeout. Vui lòng thử lại"
                                else -> "Không thể xóa xe: ${deleteResult.message}"
                            }
                            _deleteState.value = DeleteState.Error(errorMessage)
                        }
                        else -> {
                            _deleteState.value = DeleteState.Error(
                                "Có lỗi không xác định xảy ra khi xóa xe. Vui lòng thử lại"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while deleting vehicle", e)
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng"
                    e.message?.contains("timeout") == true -> "Kết nối bị timeout. Vui lòng thử lại"
                    else -> "Lỗi không xác định: ${e.message}"
                }
                _deleteState.value = DeleteState.Error(errorMessage)
            }
        }
    }
    
    fun clearDeleteState() {
        _deleteState.value = DeleteState.Idle
    }
}

sealed class VehicleDetailUiState {
    object Loading : VehicleDetailUiState()
    data class Success(
        val vehicle: CustomerVehicle,
        val orders: List<Order>
    ) : VehicleDetailUiState()
    data class Error(val message: String) : VehicleDetailUiState()
}

sealed class DeleteState {
    object Idle : DeleteState()
    object Loading : DeleteState()
    data class Success(val message: String) : DeleteState()
    data class Error(val message: String) : DeleteState()
}
