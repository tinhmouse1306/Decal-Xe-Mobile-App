package com.example.decalxeandroid.presentation.customers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class CustomerEditViewModel(
    private val customerId: String,
    private val customerRepository: CustomerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CustomerEditUiState>(CustomerEditUiState.LoadingCustomer)
    val uiState: StateFlow<CustomerEditUiState> = _uiState.asStateFlow()
    
    companion object {
        private const val TAG = "CustomerEditViewModel"
    }
    
    init {
        loadCustomer()
    }
    
    private fun loadCustomer() {
        viewModelScope.launch {
            _uiState.value = CustomerEditUiState.LoadingCustomer
            Log.d(TAG, "Loading customer for edit: $customerId")
            
            try {
                customerRepository.getCustomerById(customerId).collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            val customer = result.data
                            Log.d(TAG, "Successfully loaded customer: ${customer.fullName}")
                            
                            // Parse customer name to first and last name
                            val nameParts = customer.fullName.split(" ", limit = 2)
                            val firstName = nameParts.getOrNull(0) ?: ""
                            val lastName = nameParts.getOrNull(1) ?: ""
                            
                            val formData = CustomerFormData(
                                firstName = firstName,
                                lastName = lastName,
                                phoneNumber = customer.phoneNumber ?: "",
                                email = customer.email ?: "",
                                address = customer.address ?: "",
                                firstNameError = validateFirstName(firstName),
                                lastNameError = validateLastName(lastName),
                                phoneNumberError = validatePhoneNumber(customer.phoneNumber ?: ""),
                                emailError = validateEmail(customer.email ?: ""),
                                addressError = validateAddress(customer.address ?: "")
                            )
                            
                            _uiState.value = CustomerEditUiState.Editing(formData)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            Log.e(TAG, "Failed to load customer: ${result.message}")
                            _uiState.value = CustomerEditUiState.Error(
                                "Không thể tải thông tin khách hàng: ${result.message}",
                                CustomerFormData()
                            )
                        }
                        else -> {
                            Log.e(TAG, "Unknown result type")
                            _uiState.value = CustomerEditUiState.Error(
                                "Lỗi không xác định khi tải thông tin khách hàng",
                                CustomerFormData()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception loading customer", e)
                _uiState.value = CustomerEditUiState.Error(
                    "Lỗi kết nối: ${e.message}",
                    CustomerFormData()
                )
            }
        }
    }
    
    fun updateFirstName(firstName: String) {
        val currentState = uiState.value as? CustomerEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            firstName = firstName,
            firstNameError = validateFirstName(firstName)
        )
        _uiState.value = CustomerEditUiState.Editing(updatedFormData)
    }
    
    fun updateLastName(lastName: String) {
        val currentState = uiState.value as? CustomerEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            lastName = lastName,
            lastNameError = validateLastName(lastName)
        )
        _uiState.value = CustomerEditUiState.Editing(updatedFormData)
    }
    
    fun updatePhoneNumber(phoneNumber: String) {
        val currentState = uiState.value as? CustomerEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            phoneNumber = phoneNumber,
            phoneNumberError = validatePhoneNumber(phoneNumber)
        )
        _uiState.value = CustomerEditUiState.Editing(updatedFormData)
    }
    
    fun updateEmail(email: String) {
        val currentState = uiState.value as? CustomerEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            email = email,
            emailError = validateEmail(email)
        )
        _uiState.value = CustomerEditUiState.Editing(updatedFormData)
    }
    
    fun updateAddress(address: String) {
        val currentState = uiState.value as? CustomerEditUiState.Editing ?: return
        val updatedFormData = currentState.formData.copy(
            address = address,
            addressError = validateAddress(address)
        )
        _uiState.value = CustomerEditUiState.Editing(updatedFormData)
    }
    
    fun updateCustomer() {
        val currentState = uiState.value as? CustomerEditUiState.Editing ?: return
        val formData = currentState.formData
        
        if (!formData.isValid) {
            Log.w(TAG, "Form is not valid, cannot update customer")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = CustomerEditUiState.Loading(formData)
            Log.d(TAG, "Updating customer: $customerId")
            
            try {
                // Create Customer object with updated data
                val updatedCustomer = Customer(
                    customerId = customerId,
                    fullName = "${formData.firstName.trim()} ${formData.lastName.trim()}",
                    phoneNumber = formData.phoneNumber.trim().takeIf { it.isNotEmpty() },
                    email = formData.email.trim().takeIf { it.isNotEmpty() },
                    address = formData.address.trim().takeIf { it.isNotEmpty() },
                    dateOfBirth = null,
                    gender = null,
                    isActive = true,
                    createdAt = "",
                    updatedAt = null
                )
                
                customerRepository.updateCustomer(customerId, updatedCustomer).collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            Log.d(TAG, "Successfully updated customer: ${result.data.fullName}")
                            _uiState.value = CustomerEditUiState.Success(result.data)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            Log.e(TAG, "Failed to update customer: ${result.message}")
                            _uiState.value = CustomerEditUiState.Error(
                                "Không thể cập nhật khách hàng: ${result.message}",
                                formData
                            )
                        }
                        else -> {
                            Log.e(TAG, "Unknown result type")
                            _uiState.value = CustomerEditUiState.Error(
                                "Lỗi không xác định khi cập nhật khách hàng",
                                formData
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception updating customer", e)
                _uiState.value = CustomerEditUiState.Error(
                    "Lỗi kết nối: ${e.message}",
                    formData
                )
            }
        }
    }
    
    fun resetError() {
        val currentState = uiState.value as? CustomerEditUiState.Error ?: return
        _uiState.value = CustomerEditUiState.Editing(currentState.formData)
    }
    
    // Validation functions - reuse from AddCustomerViewModel
    private fun validateFirstName(firstName: String): String? {
        return when {
            firstName.isBlank() -> "Họ không được để trống"
            firstName.length < 2 -> "Họ phải có ít nhất 2 ký tự"
            !firstName.matches(Regex("^[a-zA-ZÀ-ỹ\\s]+$")) -> "Họ chỉ được chứa chữ cái và khoảng trắng"
            else -> null
        }
    }
    
    private fun validateLastName(lastName: String): String? {
        return when {
            lastName.isBlank() -> "Tên không được để trống"
            lastName.length < 2 -> "Tên phải có ít nhất 2 ký tự"
            !lastName.matches(Regex("^[a-zA-ZÀ-ỹ\\s]+$")) -> "Tên chỉ được chứa chữ cái và khoảng trắng"
            else -> null
        }
    }
    
    private fun validatePhoneNumber(phoneNumber: String): String? {
        return when {
            phoneNumber.isBlank() -> "Số điện thoại không được để trống"
            !phoneNumber.matches(Regex("^[0-9]{10,11}$")) -> "Số điện thoại phải có 10-11 chữ số"
            else -> null
        }
    }
    
    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return null // Email is optional
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            null
        } else {
            "Email không hợp lệ"
        }
    }
    
    private fun validateAddress(address: String): String? {
        if (address.isBlank()) return null // Address is optional
        return if (address.length >= 10) {
            null
        } else {
            "Địa chỉ phải có ít nhất 10 ký tự"
        }
    }
}

sealed class CustomerEditUiState {
    object LoadingCustomer : CustomerEditUiState()
    data class Editing(val formData: CustomerFormData) : CustomerEditUiState()
    data class Loading(val formData: CustomerFormData) : CustomerEditUiState()
    data class Error(val message: String, val formData: CustomerFormData) : CustomerEditUiState()
    data class Success(val customer: Customer) : CustomerEditUiState()
}
