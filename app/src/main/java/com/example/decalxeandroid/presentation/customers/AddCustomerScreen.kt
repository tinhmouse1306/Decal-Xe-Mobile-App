package com.example.decalxeandroid.presentation.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
            CustomerModernTopAppBar(
                title = "Thêm khách hàng",
                subtitle = "Bước 1/2",
                onNavigateBack = onNavigateBack,
                showProgress = true,
                progress = 0.5f
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Welcome Card
                ModernWelcomeCard()
                
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
                    CustomerModernErrorCard(
                        message = (uiState as AddCustomerUiState.Error).message
                    )
                }
            }
        }
    }
}


@Composable
fun ModernWelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2),
                            Color(0xFFf093fb)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PersonAdd,
                        contentDescription = "Add Customer",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "👋 Thêm khách hàng mới",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Điền thông tin khách hàng để bắt đầu",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
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
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFf093fb),
                                    Color(0xFFf5576c)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Customer",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Text(
                    text = "Thông tin khách hàng",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
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
                enabled = uiState !is AddCustomerUiState.Loading && formData.isValid,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState is AddCustomerUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = "Tiếp tục →",
                    fontWeight = FontWeight.Bold
                )
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
