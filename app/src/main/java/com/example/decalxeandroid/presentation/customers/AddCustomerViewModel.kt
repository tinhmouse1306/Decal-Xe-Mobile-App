package com.example.decalxeandroid.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddCustomerViewModel(private val customerRepository: CustomerRepository) : ViewModel() {

    private val _uiState =
            MutableStateFlow<AddCustomerUiState>(AddCustomerUiState.Editing(CustomerFormData()))
    val uiState: StateFlow<AddCustomerUiState> = _uiState.asStateFlow()

    fun updateFirstName(firstName: String) {
        val currentFormData =
                (uiState.value as? AddCustomerUiState.Editing)?.formData ?: CustomerFormData()
        val updatedFormData =
                currentFormData.copy(
                        firstName = firstName,
                        firstNameError = validateFirstName(firstName)
                )
        _uiState.value = AddCustomerUiState.Editing(updatedFormData)
    }

    fun updateLastName(lastName: String) {
        val currentFormData =
                (uiState.value as? AddCustomerUiState.Editing)?.formData ?: CustomerFormData()
        val updatedFormData =
                currentFormData.copy(
                        lastName = lastName,
                        lastNameError = validateLastName(lastName)
                )
        _uiState.value = AddCustomerUiState.Editing(updatedFormData)
    }

    fun updatePhoneNumber(phoneNumber: String) {
        val cleanedPhone = VietnameseInputUtils.cleanVietnamesePhone(phoneNumber)
        val currentFormData =
                (uiState.value as? AddCustomerUiState.Editing)?.formData ?: CustomerFormData()
        val updatedFormData =
                currentFormData.copy(
                        phoneNumber = cleanedPhone,
                        phoneNumberError = validatePhoneNumber(cleanedPhone)
                )
        _uiState.value = AddCustomerUiState.Editing(updatedFormData)
    }

    fun updateEmail(email: String) {
        val currentFormData =
                (uiState.value as? AddCustomerUiState.Editing)?.formData ?: CustomerFormData()
        val updatedFormData = currentFormData.copy(email = email, emailError = validateEmail(email))
        _uiState.value = AddCustomerUiState.Editing(updatedFormData)
    }

    fun updateAddress(address: String) {
        val currentFormData =
                (uiState.value as? AddCustomerUiState.Editing)?.formData ?: CustomerFormData()
        val updatedFormData =
                currentFormData.copy(address = address, addressError = validateAddress(address))
        _uiState.value = AddCustomerUiState.Editing(updatedFormData)
    }

    fun createCustomer() {
        val currentFormData = (uiState.value as? AddCustomerUiState.Editing)?.formData ?: return

        if (!currentFormData.isValid) {
            return
        }

        viewModelScope.launch {
            _uiState.value = AddCustomerUiState.Loading

            try {
                val result =
                        customerRepository.createCustomer(
                                firstName = currentFormData.firstName,
                                lastName = currentFormData.lastName,
                                phoneNumber = currentFormData.phoneNumber,
                                email = currentFormData.email.takeIf { it.isNotBlank() },
                                address = currentFormData.address
                        )

                result.collect { createResult ->
                    when (createResult) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            _uiState.value = AddCustomerUiState.Success(createResult.data)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value = AddCustomerUiState.Error(createResult.message)
                        }
                        else -> {
                            _uiState.value = AddCustomerUiState.Error("Kết quả không xác định")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AddCustomerUiState.Error("Lỗi không xác định: ${e.message}")
            }
        }
    }

    private fun validateFirstName(firstName: String): String? {
        return when {
            firstName.isBlank() -> "Họ không được để trống"
            firstName.length < 2 -> "Họ phải có ít nhất 2 ký tự"
            else -> null
        }
    }

    private fun validateLastName(lastName: String): String? {
        return when {
            lastName.isBlank() -> "Tên không được để trống"
            lastName.length < 2 -> "Tên phải có ít nhất 2 ký tự"
            else -> null
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): String? {
        return when {
            phoneNumber.isBlank() -> "Số điện thoại không được để trống"
            !VietnameseInputUtils.isValidVietnamesePhone(phoneNumber) ->
                    "Số điện thoại không hợp lệ"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return null // Email is optional

        return when {
            !VietnameseInputUtils.isValidEmail(email) -> "Email không hợp lệ"
            else -> null
        }
    }

    private fun validateAddress(address: String): String? {
        if (address.isBlank()) return null // Address is optional

        return when {
            address.length < 10 -> "Địa chỉ phải có ít nhất 10 ký tự"
            else -> null
        }
    }
}

sealed class AddCustomerUiState {
    data class Editing(val formData: CustomerFormData) : AddCustomerUiState()
    object Loading : AddCustomerUiState()
    data class Success(val customer: Customer) : AddCustomerUiState()
    data class Error(val message: String) : AddCustomerUiState()
}
