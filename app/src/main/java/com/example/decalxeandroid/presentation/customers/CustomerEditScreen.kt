package com.example.decalxeandroid.presentation.customers

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decalxeandroid.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerEditScreen(
        customerId: String,
        onNavigateBack: () -> Unit,
        onNavigateToDetail: (String) -> Unit,
        viewModel: CustomerEditViewModel =
                viewModel(
                        factory =
                                CustomerEditViewModelFactory(
                                        customerId = customerId,
                                        customerRepository = AppContainer.customerRepository
                                )
                )
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation on success
    LaunchedEffect(uiState) {
        if (uiState is CustomerEditUiState.Success) {
            onNavigateToDetail(customerId)
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Chỉnh sửa khách hàng") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                            }
                        },
                        actions = {
                            when (val state = uiState) {
                                is CustomerEditUiState.Editing -> {
                                    if (state.formData.isValid) {
                                        IconButton(onClick = { viewModel.updateCustomer() }) {
                                            Icon(Icons.Default.Check, contentDescription = "Lưu")
                                        }
                                    }
                                }
                                is CustomerEditUiState.Loading -> {
                                    CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            strokeWidth = 2.dp
                                    )
                                }
                                else -> {}
                            }
                        }
                )
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is CustomerEditUiState.LoadingCustomer -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Đang tải thông tin khách hàng...")
                        }
                    }
                }
                is CustomerEditUiState.Editing -> {
                    EditCustomerForm(
                            uiState = state,
                            onFirstNameChange = viewModel::updateFirstName,
                            onLastNameChange = viewModel::updateLastName,
                            onPhoneNumberChange = viewModel::updatePhoneNumber,
                            onEmailChange = viewModel::updateEmail,
                            onAddressChange = viewModel::updateAddress,
                            onSubmit = viewModel::updateCustomer
                    )
                }
                is CustomerEditUiState.Loading -> {
                    EditCustomerForm(
                            uiState = state,
                            onFirstNameChange = {},
                            onLastNameChange = {},
                            onPhoneNumberChange = {},
                            onEmailChange = {},
                            onAddressChange = {},
                            onSubmit = {}
                    )

                    Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        Card(elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                            Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator()
                                Text(
                                        text = "Đang cập nhật thông tin...",
                                        style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                is CustomerEditUiState.Error -> {
                    EditCustomerForm(
                            uiState = state,
                            onFirstNameChange = viewModel::updateFirstName,
                            onLastNameChange = viewModel::updateLastName,
                            onPhoneNumberChange = viewModel::updatePhoneNumber,
                            onEmailChange = viewModel::updateEmail,
                            onAddressChange = viewModel::updateAddress,
                            onSubmit = viewModel::updateCustomer
                    )

                    // Error overlay
                    Box(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        Card(
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor =
                                                        MaterialTheme.colorScheme.errorContainer
                                        ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                        Icons.Default.Error,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(48.dp)
                                )
                                Text(
                                        text = state.message,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(onClick = { viewModel.resetError() }) {
                                        Text("Đóng")
                                    }
                                    Button(onClick = { viewModel.updateCustomer() }) {
                                        Text("Thử lại")
                                    }
                                }
                            }
                        }
                    }
                }
                is CustomerEditUiState.Success -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Card(
                                colors =
                                        CardDefaults.cardColors(
                                                containerColor =
                                                        MaterialTheme.colorScheme.primaryContainer
                                        ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(48.dp)
                                )
                                Text(
                                        text = "Cập nhật thành công!",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                        text = "Đang chuyển về trang chi tiết...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditCustomerForm(
        uiState: CustomerEditUiState,
        onFirstNameChange: (String) -> Unit,
        onLastNameChange: (String) -> Unit,
        onPhoneNumberChange: (String) -> Unit,
        onEmailChange: (String) -> Unit,
        onAddressChange: (String) -> Unit,
        onSubmit: () -> Unit
) {
    val formData =
            when (val state = uiState) {
                is CustomerEditUiState.Editing -> state.formData
                is CustomerEditUiState.Loading -> state.formData
                is CustomerEditUiState.Error -> state.formData
                else -> CustomerFormData()
            }

    val enabled =
            when (uiState) {
                is CustomerEditUiState.Editing -> true
                else -> false
            }

    Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                        text = "Chỉnh sửa thông tin khách hàng",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                )

                // First Name
                OutlinedTextField(
                        value = formData.firstName,
                        onValueChange = onFirstNameChange,
                        label = { Text("Họ *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        isError = formData.firstNameError != null,
                        supportingText = {
                            if (formData.firstNameError != null) {
                                Text(
                                        text = formData.firstNameError,
                                        color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        enabled = enabled
                )

                // Last Name
                OutlinedTextField(
                        value = formData.lastName,
                        onValueChange = onLastNameChange,
                        label = { Text("Tên *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        isError = formData.lastNameError != null,
                        supportingText = {
                            if (formData.lastNameError != null) {
                                Text(
                                        text = formData.lastNameError,
                                        color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        enabled = enabled
                )

                // Phone Number
                OutlinedTextField(
                        value = formData.phoneNumber,
                        onValueChange = onPhoneNumberChange,
                        label = { Text("Số điện thoại *") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        isError = formData.phoneNumberError != null,
                        supportingText = {
                            if (formData.phoneNumberError != null) {
                                Text(
                                        text = formData.phoneNumberError,
                                        color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        enabled = enabled
                )

                // Email
                OutlinedTextField(
                        value = formData.email,
                        onValueChange = onEmailChange,
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        isError = formData.emailError != null,
                        supportingText = {
                            if (formData.emailError != null) {
                                Text(
                                        text = formData.emailError,
                                        color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        enabled = enabled
                )

                // Address
                OutlinedTextField(
                        value = formData.address,
                        onValueChange = onAddressChange,
                        label = { Text("Địa chỉ") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        isError = formData.addressError != null,
                        supportingText = {
                            if (formData.addressError != null) {
                                Text(
                                        text = formData.addressError,
                                        color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        enabled = enabled,
                        minLines = 2
                )

                if (enabled) {
                    Button(
                            onClick = onSubmit,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = formData.isValid
                    ) { Text("Cập nhật thông tin") }
                }
            }
        }
    }
}
