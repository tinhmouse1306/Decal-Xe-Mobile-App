package com.example.decalxeandroid.presentation.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.CustomerVehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: String,
    onNavigateBack: () -> Unit,
    onEditCustomer: (Customer) -> Unit,
    onViewVehicleDetail: (CustomerVehicle) -> Unit,
    onAddVehicle: (Customer) -> Unit,
    viewModel: CustomerDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = CustomerDetailViewModelFactory(
            customerRepository = com.example.decalxeandroid.di.AppContainer.customerRepository,
            customerVehicleRepository = com.example.decalxeandroid.di.AppContainer.customerVehicleRepository
        )
    )
) {
    val uiState by viewModel.uiState
    
    // Load customer data when screen is created
    LaunchedEffect(customerId) {
        viewModel.loadCustomerData(customerId)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFFf093fb)
                    )
                )
            )
    ) {
        // Modern Top App Bar
        CustomerModernTopAppBar(
            title = "Chi tiết khách hàng",
            subtitle = "Thông tin và danh sách xe",
            onNavigateBack = onNavigateBack
        )
        
        when (uiState) {
            is CustomerDetailUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                }
            }
            is CustomerDetailUiState.Success -> {
                val successState = uiState as CustomerDetailUiState.Success
                val customer = successState.customer
                val customerVehicles = successState.customerVehicles
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Customer Info Card
                    CustomerInfoDetailCard(customer = customer)
                    
                    // Action Buttons
                    CustomerActionButtons(
                        customer = customer,
                        onEditCustomer = onEditCustomer,
                        onAddVehicle = onAddVehicle
                    )
                    
                    // Customer Vehicles Section
                    CustomerVehiclesSection(
                        customerVehicles = customerVehicles,
                        onViewVehicleDetail = onViewVehicleDetail,
                        onAddVehicle = { onAddVehicle(customer) }
                    )
                }
            }
            is CustomerDetailUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Lỗi",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = (uiState as CustomerDetailUiState.Error).message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.loadCustomerData(customerId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF667eea)
                            )
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerInfoDetailCard(customer: Customer) {
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
                            Color(0xFF43e97b),
                            Color(0xFF38f9d7)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header with avatar and name
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Customer",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = customer.fullName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Khách hàng",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Customer Details
                CustomerDetailItem(
                    icon = Icons.Default.Phone,
                    label = "Số điện thoại",
                    value = customer.phoneNumber ?: "Không có"
                )
                
                CustomerDetailItem(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = customer.email ?: "Không có"
                )
                
                CustomerDetailItem(
                    icon = Icons.Default.LocationOn,
                    label = "Địa chỉ",
                    value = customer.address ?: "Không có"
                )
                
                CustomerDetailItem(
                    icon = Icons.Default.Cake,
                    label = "Ngày sinh",
                    value = customer.dateOfBirth ?: "Không có"
                )
                
                CustomerDetailItem(
                    icon = Icons.Default.Person,
                    label = "Giới tính",
                    value = customer.gender ?: "Không có"
                )
                
                CustomerDetailItem(
                    icon = Icons.Default.CalendarToday,
                    label = "Ngày tạo",
                    value = customer.createdAt ?: "Không có"
                )
            }
        }
    }
}

@Composable
fun CustomerDetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
fun CustomerActionButtons(
    customer: Customer,
    onEditCustomer: (Customer) -> Unit,
    onAddVehicle: (Customer) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Edit Customer Button
        Button(
            onClick = { onEditCustomer(customer) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF667eea)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Chỉnh sửa")
        }
        
        // Add Vehicle Button
        Button(
            onClick = { onAddVehicle(customer) },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF43e97b)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Thêm xe")
        }
    }
}

@Composable
fun CustomerVehiclesSection(
    customerVehicles: List<CustomerVehicle>,
    onViewVehicleDetail: (CustomerVehicle) -> Unit,
    onAddVehicle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Danh sách xe (${customerVehicles.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(onClick = onAddVehicle) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thêm xe")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (customerVehicles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Chưa có xe nào",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Nhấn 'Thêm xe' để thêm xe mới",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                customerVehicles.forEach { vehicle ->
                    VehicleItemCard(
                        vehicle = vehicle,
                        onClick = { onViewVehicleDetail(vehicle) }
                    )
                    if (vehicle != customerVehicles.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleItemCard(
    vehicle: CustomerVehicle,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF667eea),
                                Color(0xFF764ba2)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.DirectionsCar,
                    contentDescription = "Vehicle",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = vehicle.licensePlate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = vehicle.vehicleModelName ?: "Không rõ mẫu xe",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${vehicle.color} • ${vehicle.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "View detail",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}