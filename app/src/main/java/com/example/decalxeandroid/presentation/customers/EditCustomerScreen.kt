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
import com.example.decalxeandroid.domain.model.Customer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun EditCustomerScreen(
        customerId: String,
        onNavigateBack: () -> Unit,
        onCustomerUpdated: (Customer) -> Unit,
        viewModel: EditCustomerViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
                        factory =
                                EditCustomerViewModelFactory(
                                        customerRepository =
                                                com.example.decalxeandroid.di.AppContainer
                                                        .customerRepository
                                )
                )
) {
    val uiState by viewModel.uiState

    // Load customer data and initialize form
    LaunchedEffect(customerId) { viewModel.loadAndInitializeCustomer(customerId) }

    LaunchedEffect(uiState) {
        if (uiState is EditCustomerUiState.Success) {
            onCustomerUpdated((uiState as EditCustomerUiState.Success).customer)
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
        CustomerModernTopAppBar(
                title = "Chỉnh sửa khách hàng",
                subtitle = "Cập nhật thông tin khách hàng",
                onNavigateBack = onNavigateBack
        )

        Column(
                modifier =
                        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Card
            when (uiState) {
                is EditCustomerUiState.Editing -> {
                    val editingState = uiState as EditCustomerUiState.Editing
                    ModernWelcomeCard(
                            title = "Chỉnh sửa thông tin",
                            subtitle =
                                    "Cập nhật thông tin khách hàng ${editingState.formData.firstName} ${editingState.formData.lastName}"
                    )
                }
                else -> {
                    ModernWelcomeCard(
                            title = "Chỉnh sửa thông tin",
                            subtitle = "Đang tải thông tin khách hàng..."
                    )
                }
            }

            // Edit Form
            when (uiState) {
                is EditCustomerUiState.Editing -> {
                    EditCustomerForm(
                            uiState = uiState,
                            onFirstNameChange = { viewModel.updateFirstName(it) },
                            onLastNameChange = { viewModel.updateLastName(it) },
                            onPhoneNumberChange = { viewModel.updatePhoneNumber(it) },
                            onEmailChange = { viewModel.updateEmail(it) },
                            onAddressChange = { viewModel.updateAddress(it) },
                            onSubmit = { viewModel.updateCustomer(customerId) }
                    )
                }
                is EditCustomerUiState.Loading -> {
                    Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = androidx.compose.ui.Alignment.Center
                    ) { CircularProgressIndicator(color = Color.White) }
                }
                is EditCustomerUiState.Error -> {
                    CustomerModernErrorCard(
                            message = (uiState as EditCustomerUiState.Error).message
                    )
                }
                is EditCustomerUiState.Success -> {
                    // This will trigger navigation via LaunchedEffect
                }
            }
        }
    }
}

@Composable
fun ModernWelcomeCard(title: String, subtitle: String) {
    Card(
            modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    ) {
        Box(
                modifier =
                        androidx.compose.ui.Modifier.fillMaxWidth()
                                .background(
                                        brush =
                                                Brush.linearGradient(
                                                        colors =
                                                                listOf(
                                                                        Color(0xFF667eea),
                                                                        Color(0xFF764ba2),
                                                                        Color(0xFFf093fb)
                                                                )
                                                )
                                )
        ) {
            Column(modifier = androidx.compose.ui.Modifier.padding(24.dp)) {
                Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = Color.White
                )
                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun EditCustomerForm(
        uiState: EditCustomerUiState,
        onFirstNameChange: (String) -> Unit,
        onLastNameChange: (String) -> Unit,
        onPhoneNumberChange: (String) -> Unit,
        onEmailChange: (String) -> Unit,
        onAddressChange: (String) -> Unit,
        onSubmit: () -> Unit
) {
    val formData =
            when (uiState) {
                is EditCustomerUiState.Editing -> uiState.formData
                else -> CustomerFormData()
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
                    text = "Thông tin khách hàng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )

            // First Name
            CustomerModernOutlinedTextField(
                    value = formData.firstName,
                    onValueChange = onFirstNameChange,
                    label = "Họ *",
                    leadingIcon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text,
                    isError = formData.firstNameError != null,
                    errorMessage = formData.firstNameError
            )

            // Last Name
            CustomerModernOutlinedTextField(
                    value = formData.lastName,
                    onValueChange = onLastNameChange,
                    label = "Tên *",
                    leadingIcon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text,
                    isError = formData.lastNameError != null,
                    errorMessage = formData.lastNameError
            )

            // Phone Number
            CustomerModernOutlinedTextField(
                    value = formData.phoneNumber,
                    onValueChange = onPhoneNumberChange,
                    label = "Số điện thoại *",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone,
                    isError = formData.phoneNumberError != null,
                    errorMessage = formData.phoneNumberError
            )

            // Email
            CustomerModernOutlinedTextField(
                    value = formData.email,
                    onValueChange = onEmailChange,
                    label = "Email",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    isError = formData.emailError != null,
                    errorMessage = formData.emailError
            )

            // Address
            CustomerModernOutlinedTextField(
                    value = formData.address,
                    onValueChange = onAddressChange,
                    label = "Địa chỉ",
                    leadingIcon = Icons.Default.LocationOn,
                    keyboardType = KeyboardType.Text,
                    isError = formData.addressError != null,
                    errorMessage = formData.addressError,
                    maxLines = 3
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
                        text = "Cập nhật khách hàng",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ViewModel for Edit Customer
class EditCustomerViewModel(
        private val customerRepository:
                com.example.decalxeandroid.domain.repository.CustomerRepository
) : androidx.lifecycle.ViewModel() {

    private val _uiState =
            androidx.compose.runtime.mutableStateOf<EditCustomerUiState>(
                    EditCustomerUiState.Editing(CustomerFormData())
            )
    val uiState: androidx.compose.runtime.State<EditCustomerUiState> = _uiState

    fun loadAndInitializeCustomer(customerId: String) {
        viewModelScope.launch {
            _uiState.value = EditCustomerUiState.Loading

            try {
                customerRepository.getCustomerById(customerId).collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            val customer = result.data
                            val formData =
                                    CustomerFormData(
                                            firstName = customer.fullName.split(" ").firstOrNull()
                                                            ?: "",
                                            lastName =
                                                    customer.fullName
                                                            .split(" ")
                                                            .drop(1)
                                                            .joinToString(" "),
                                            phoneNumber = customer.phoneNumber ?: "",
                                            email = customer.email ?: "",
                                            address = customer.address ?: ""
                                    )
                            _uiState.value = EditCustomerUiState.Editing(formData)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value =
                                    EditCustomerUiState.Error(
                                            result.message
                                                    ?: "Lỗi không thể tải thông tin khách hàng"
                                    )
                        }
                        is com.example.decalxeandroid.domain.model.Result.Loading -> {
                            _uiState.value = EditCustomerUiState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = EditCustomerUiState.Error("Lỗi không xác định: ${e.message}")
            }
        }
    }

    fun updateFirstName(firstName: String) {
        val currentFormData =
                (uiState.value as? EditCustomerUiState.Editing)?.formData ?: CustomerFormData()
        val updatedFormData =
                currentFormData.copy(
                        firstName = firstName,
                        firstNameError = validateFirstName(firstName)
                )
        _uiState.value = EditCustomerUiState.Editing(updatedFormData)
    }

    fun updateLastName(lastName: String) {
        val currentFormData =
                (uiState.value as? EditCustomerUiState.Editing)?.formData ?: CustomerFormData()
        val updatedFormData =
                currentFormData.copy(
                        lastName = lastName,
                        lastNameError = validateLastName(lastName)
                )
        _uiState.value = EditCustomerUiState.Editing(updatedFormData)
    }

    fun updatePhoneNumber(phoneNumber: String) {
        val cleanedPhone = VietnameseInputUtils.cleanVietnamesePhone(phoneNumber)
        val currentFormData =
                (uiState.value as? EditCustomerUiState.Editing)?.formData ?: CustomerFormData()
        val updatedFormData =
                currentFormData.copy(
                        phoneNumber = cleanedPhone,
                        phoneNumberError = validatePhoneNumber(cleanedPhone)
                )
        _uiState.value = EditCustomerUiState.Editing(updatedFormData)
    }

    fun updateEmail(email: String) {
        val currentFormData =
                (uiState.value as? EditCustomerUiState.Editing)?.formData ?: CustomerFormData()
        val updatedFormData = currentFormData.copy(email = email, emailError = validateEmail(email))
        _uiState.value = EditCustomerUiState.Editing(updatedFormData)
    }

    fun updateAddress(address: String) {
        val currentFormData =
                (uiState.value as? EditCustomerUiState.Editing)?.formData ?: CustomerFormData()
        val updatedFormData =
                currentFormData.copy(address = address, addressError = validateAddress(address))
        _uiState.value = EditCustomerUiState.Editing(updatedFormData)
    }

    fun updateCustomer(customerId: String) {
        val currentFormData = (uiState.value as? EditCustomerUiState.Editing)?.formData ?: return

        if (!currentFormData.isValid) {
            return
        }

        viewModelScope.launch {
            _uiState.value = EditCustomerUiState.Loading

            try {
                // Create updated customer object
                val updatedCustomer =
                        Customer(
                                customerId = customerId,
                                fullName =
                                        "${currentFormData.firstName} ${currentFormData.lastName}".trim(),
                                phoneNumber = currentFormData.phoneNumber,
                                email = currentFormData.email,
                                address = currentFormData.address,
                                dateOfBirth = null,
                                gender = null,
                                isActive = true,
                                createdAt = "",
                                updatedAt = ""
                        )

                // Update customer via repository
                customerRepository.updateCustomer(customerId, updatedCustomer).collect { result ->
                    when (result) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            _uiState.value = EditCustomerUiState.Success(updatedCustomer)
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value =
                                    EditCustomerUiState.Error(
                                            result.message ?: "Lỗi không xác định"
                                    )
                        }
                        is com.example.decalxeandroid.domain.model.Result.Loading -> {
                            _uiState.value = EditCustomerUiState.Loading
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = EditCustomerUiState.Error("Lỗi không xác định: ${e.message}")
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

// UI State for Edit Customer
sealed class EditCustomerUiState {
    data class Editing(val formData: CustomerFormData) : EditCustomerUiState()
    object Loading : EditCustomerUiState()
    data class Success(val customer: Customer) : EditCustomerUiState()
    data class Error(val message: String) : EditCustomerUiState()
}

// ViewModel Factory
class EditCustomerViewModelFactory(
        private val customerRepository:
                com.example.decalxeandroid.domain.repository.CustomerRepository
) : androidx.lifecycle.ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditCustomerViewModel::class.java)) {
            return EditCustomerViewModel(customerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
