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
import com.example.decalxeandroid.domain.model.CustomerVehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerVehicleDetailScreen(
        vehicleId: String,
        onNavigateBack: (String?) -> Unit, // Now accepts customerId parameter
        onEditVehicle: (CustomerVehicle) -> Unit,
        onDeleteVehicle: (CustomerVehicle) -> Unit,
        viewModel: CustomerVehicleDetailViewModel =
                androidx.lifecycle.viewmodel.compose.viewModel(
                        factory =
                                CustomerVehicleDetailViewModelFactory(
                                        customerVehicleRepository =
                                                com.example.decalxeandroid.di.AppContainer
                                                        .customerVehicleRepository
                                )
                )
) {
    val uiState by viewModel.uiState

    // Load vehicle data when screen is created
    LaunchedEffect(vehicleId) { viewModel.loadVehicleData(vehicleId) }
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
        when (uiState) {
            is CustomerVehicleDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            is CustomerVehicleDetailUiState.Success -> {
                val successState = uiState as CustomerVehicleDetailUiState.Success
                val vehicle = successState.vehicle

                Column(modifier = Modifier.fillMaxSize()) {
                    // Modern Top App Bar
                    CustomerModernTopAppBar(
                            title = "Chi tiết xe",
                            subtitle = vehicle.licensePlate,
                            onNavigateBack = { onNavigateBack(vehicle.customerID) }
                    )

                    Column(
                            modifier =
                                    Modifier.fillMaxSize()
                                            .padding(16.dp)
                                            .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Vehicle Info Card
                        VehicleInfoDetailCard(vehicle = vehicle)

                        // Action Buttons
                        VehicleActionButtons(
                                vehicle = vehicle,
                                onEditVehicle = onEditVehicle,
                                onDeleteVehicle = { vehicle ->
                                    viewModel.deleteVehicle(vehicle.vehicleID) {
                                        onNavigateBack(vehicle.customerID)
                                    }
                                }
                        )

                        // Vehicle Specifications
                        VehicleSpecificationsCard(vehicle = vehicle)
                    }
                }
            }
            is CustomerVehicleDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                                text = (uiState as CustomerVehicleDetailUiState.Error).message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                        )
                        Button(
                                onClick = { viewModel.loadVehicleData(vehicleId) },
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF667eea)
                                        )
                        ) { Text("Thử lại") }
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleInfoDetailCard(vehicle: CustomerVehicle) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(20.dp)
    ) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(
                                        brush =
                                                Brush.linearGradient(
                                                        colors =
                                                                listOf(
                                                                        Color(0xFF4facfe),
                                                                        Color(0xFF00f2fe)
                                                                )
                                                )
                                )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Header with car icon and license plate
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                            modifier =
                                    Modifier.size(64.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                    ) {
                        Icon(
                                Icons.Default.DirectionsCar,
                                contentDescription = "Vehicle",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                                text = vehicle.licensePlate,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                        )
                        Text(
                                text = vehicle.vehicleModelName ?: "Không rõ mẫu xe",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Vehicle Details
                VehicleDetailItem(
                        icon = Icons.Default.ConfirmationNumber,
                        label = "Số khung xe",
                        value = vehicle.chassisNumber
                )

                VehicleDetailItem(
                        icon = Icons.Default.Palette,
                        label = "Màu sắc",
                        value = vehicle.color
                )

                VehicleDetailItem(
                        icon = Icons.Default.CalendarToday,
                        label = "Năm sản xuất",
                        value = vehicle.year.toString()
                )

                VehicleDetailItem(
                        icon = Icons.Default.Speed,
                        label = "Số km ban đầu",
                        value = "${vehicle.initialKM} km"
                )

                VehicleDetailItem(
                        icon = Icons.Default.Business,
                        label = "Hãng xe",
                        value = vehicle.vehicleBrandName ?: "Không rõ"
                )
            }
        }
    }
}

@Composable
fun VehicleDetailItem(icon: ImageVector, label: String, value: String) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
fun VehicleActionButtons(
        vehicle: CustomerVehicle,
        onEditVehicle: (CustomerVehicle) -> Unit,
        onDeleteVehicle: (CustomerVehicle) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        // Edit Vehicle Button
        Button(
                onClick = { onEditVehicle(vehicle) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF667eea)),
                shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Chỉnh sửa")
        }

        // Delete Vehicle Button
        Button(
                onClick = { onDeleteVehicle(vehicle) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff6b6b)),
                shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Xóa xe")
        }
    }
}

@Composable
fun VehicleSpecificationsCard(vehicle: CustomerVehicle) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                    text = "Thông số kỹ thuật",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Specifications Grid
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpecificationItem(
                            icon = Icons.Default.ConfirmationNumber,
                            label = "Số khung",
                            value = vehicle.chassisNumber
                    )

                    SpecificationItem(
                            icon = Icons.Default.Palette,
                            label = "Màu sắc",
                            value = vehicle.color
                    )
                }

                Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpecificationItem(
                            icon = Icons.Default.CalendarToday,
                            label = "Năm sản xuất",
                            value = vehicle.year.toString()
                    )

                    SpecificationItem(
                            icon = Icons.Default.Speed,
                            label = "Km ban đầu",
                            value = "${vehicle.initialKM} km"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Model Information
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                    shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                            Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                                text = vehicle.vehicleModelName ?: "Không rõ mẫu xe",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                        )
                        Text(
                                text = vehicle.vehicleBrandName ?: "Không rõ hãng xe",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpecificationItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
                icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
            )
        }
    }
}
