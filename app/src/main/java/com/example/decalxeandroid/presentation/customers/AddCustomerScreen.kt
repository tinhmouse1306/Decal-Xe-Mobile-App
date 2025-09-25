package com.example.decalxeandroid.presentation.customers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(
    onNavigateBack: () -> Unit,
    onCustomerCreated: (Customer) -> Unit,
    viewModel: AddCustomerViewModel = viewModel(
        factory = AddCustomerViewModelFactory(
            customerRepository = AppContainer.customerRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState) {
        if (uiState is AddCustomerUiState.Success) {
            onCustomerCreated((uiState as AddCustomerUiState.Success).customer)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm khách hàng") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Form Fields
            CustomerForm(
                uiState = uiState,
                onFirstNameChange = { viewModel.updateFirstName(it) },
                onLastNameChange = { viewModel.updateLastName(it) },
                onPhoneNumberChange = { viewModel.updatePhoneNumber(it) },
                onEmailChange = { viewModel.updateEmail(it) },
                onAddressChange = { viewModel.updateAddress(it) },
                onSubmit = { viewModel.createCustomer() }
            )
            
            // Error Message
            if (uiState is AddCustomerUiState.Error) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = (uiState as AddCustomerUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerForm(
    uiState: AddCustomerUiState,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val formData = when (uiState) {
        is AddCustomerUiState.Editing -> uiState.formData
        else -> CustomerFormData()
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Thông tin khách hàng",
                style = MaterialTheme.typography.titleLarge
            )
            
            // First Name
            OutlinedTextField(
                value = formData.firstName,
                onValueChange = onFirstNameChange,
                label = { Text("Họ *") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                isError = formData.firstNameError != null,
                supportingText = {
                    if (formData.firstNameError != null) {
                        Text(
                            text = formData.firstNameError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            
            // Last Name
            OutlinedTextField(
                value = formData.lastName,
                onValueChange = onLastNameChange,
                label = { Text("Tên *") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                isError = formData.lastNameError != null,
                supportingText = {
                    if (formData.lastNameError != null) {
                        Text(
                            text = formData.lastNameError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            
            // Phone Number
            OutlinedTextField(
                value = formData.phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text("Số điện thoại *") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                isError = formData.phoneNumberError != null,
                supportingText = {
                    if (formData.phoneNumberError != null) {
                        Text(
                            text = formData.phoneNumberError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            
            // Email
            OutlinedTextField(
                value = formData.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                isError = formData.emailError != null,
                supportingText = {
                    if (formData.emailError != null) {
                        Text(
                            text = formData.emailError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            
            // Address
            OutlinedTextField(
                value = formData.address,
                onValueChange = onAddressChange,
                label = { Text("Địa chỉ") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                isError = formData.addressError != null,
                supportingText = {
                    if (formData.addressError != null) {
                        Text(
                            text = formData.addressError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Submit Button
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState !is AddCustomerUiState.Loading && formData.isValid
            ) {
                if (uiState is AddCustomerUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Tạo khách hàng")
            }
        }
    }
}

data class CustomerFormData(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val address: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneNumberError: String? = null,
    val emailError: String? = null,
    val addressError: String? = null
) {
    val isValid: Boolean
        get() = firstName.isNotBlank() && 
                lastName.isNotBlank() && 
                phoneNumber.isNotBlank() &&
                firstNameError == null &&
                lastNameError == null &&
                phoneNumberError == null &&
                emailError == null &&
                addressError == null
}
