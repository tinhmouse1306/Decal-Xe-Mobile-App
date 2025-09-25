package com.example.decalxeandroid.presentation.customers

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.CustomerRepository
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import kotlinx.coroutines.launch

class CustomerDetailViewModel(
    private val customerRepository: CustomerRepository,
    private val customerVehicleRepository: CustomerVehicleRepository
) : ViewModel() {
    
    private val _uiState = mutableStateOf<CustomerDetailUiState>(CustomerDetailUiState.Loading)
    val uiState: State<CustomerDetailUiState> = _uiState
    
    fun loadCustomerData(customerId: String) {
        viewModelScope.launch {
            _uiState.value = CustomerDetailUiState.Loading
            
            try {
                // Load customer data
                customerRepository.getCustomerById(customerId).collect { customerResult ->
                    when (customerResult) {
                        is Result.Success -> {
                            val customer = customerResult.data
                            
                            // Load customer vehicles
                            customerVehicleRepository.getVehiclesByCustomerId(customerId).collect { vehiclesResult ->
                                when (vehiclesResult) {
                                    is Result.Success -> {
                                        _uiState.value = CustomerDetailUiState.Success(
                                            customer = customer,
                                            customerVehicles = vehiclesResult.data
                                        )
                                    }
                                    is Result.Error -> {
                                        _uiState.value = CustomerDetailUiState.Error(
                                            message = vehiclesResult.message ?: "Lỗi không thể tải danh sách xe"
                                        )
                                    }
                                    is Result.Loading -> {
                                        _uiState.value = CustomerDetailUiState.Loading
                                    }
                                }
                            }
                        }
                        is Result.Error -> {
                            _uiState.value = CustomerDetailUiState.Error(
                                message = customerResult.message ?: "Lỗi không thể tải thông tin khách hàng"
                            )
                        }
                        is Result.Loading -> {
                            _uiState.value = CustomerDetailUiState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CustomerDetailUiState.Error(
                    message = "Lỗi không xác định: ${e.message}"
                )
            }
        }
    }
}

sealed class CustomerDetailUiState {
    object Loading : CustomerDetailUiState()
    data class Success(
        val customer: Customer,
        val customerVehicles: List<CustomerVehicle>
    ) : CustomerDetailUiState()
    data class Error(val message: String) : CustomerDetailUiState()
}
