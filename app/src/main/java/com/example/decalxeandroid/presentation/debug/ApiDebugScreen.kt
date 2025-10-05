package com.example.decalxeandroid.presentation.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decalxeandroid.di.AppContainer
import com.example.decalxeandroid.presentation.services.ServicesViewModel
import com.example.decalxeandroid.presentation.vehicles.VehiclesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiDebugScreen() {
    val vehiclesViewModel: VehiclesViewModel = viewModel {
        VehiclesViewModel(AppContainer.customerVehicleRepository)
    }

    val servicesViewModel: ServicesViewModel = viewModel {
        ServicesViewModel(AppContainer.decalServiceRepository)
    }

    val vehiclesUiState by vehiclesViewModel.uiState.collectAsState()
    val servicesUiState by servicesViewModel.uiState.collectAsState()

    Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
                text = "API Debug - Customer Vehicles & Services",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                        text = "Endpoint Test",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                Text("URL: https://decalxesequences-production.up.railway.app/api/CustomerVehicles")

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                        onClick = { vehiclesViewModel.loadVehicles() },
                        modifier = Modifier.fillMaxWidth()
                ) { Text("Test CustomerVehicles API") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // DecalServices API Test Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                        text = "DecalServices Test",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                Text("URL: https://decalxesequences-production.up.railway.app/api/DecalServices")

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                        onClick = { servicesViewModel.loadServices() },
                        modifier = Modifier.fillMaxWidth()
                ) { Text("Test DecalServices API") }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Card
        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor =
                                        when {
                                            vehiclesUiState.isLoading ||
                                                    servicesUiState.isLoading ->
                                                    MaterialTheme.colorScheme.primaryContainer
                                            vehiclesUiState.error != null ||
                                                    servicesUiState.error != null ->
                                                    MaterialTheme.colorScheme.errorContainer
                                            vehiclesUiState.vehicles.isNotEmpty() ||
                                                    servicesUiState.services.isNotEmpty() ->
                                                    MaterialTheme.colorScheme.primaryContainer
                                            else -> MaterialTheme.colorScheme.surface
                                        }
                        )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                        text = "Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )

                when {
                    vehiclesUiState.isLoading || servicesUiState.isLoading -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Loading...")
                        }
                    }
                    vehiclesUiState.error != null -> {
                        Text(
                                text = "❌ Vehicles Error: ${vehiclesUiState.error}",
                                color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    servicesUiState.error != null -> {
                        Text(
                                text = "❌ Services Error: ${servicesUiState.error}",
                                color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    vehiclesUiState.vehicles.isNotEmpty() ||
                            servicesUiState.services.isNotEmpty() -> {
                        Column {
                            if (vehiclesUiState.vehicles.isNotEmpty()) {
                                Text(
                                        text =
                                                "✅ Vehicles: ${vehiclesUiState.vehicles.size} loaded",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            if (servicesUiState.services.isNotEmpty()) {
                                Text(
                                        text =
                                                "✅ Services: ${servicesUiState.services.size} loaded",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    else -> {
                        Text("Ready to test...")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results
        if (vehiclesUiState.vehicles.isNotEmpty() || servicesUiState.services.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                            text = "Results",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn {
                        // Vehicles section
                        if (vehiclesUiState.vehicles.isNotEmpty()) {
                            item {
                                Text(
                                        text = "Vehicles (${vehiclesUiState.vehicles.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        items(vehiclesUiState.vehicles) { vehicle ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                            text = "ID: ${vehicle.vehicleID}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                    )
                                    Text("Chassis: ${vehicle.chassisNumber}")
                                    Text("License: ${vehicle.licensePlate}")
                                    Text("Color: ${vehicle.color}")
                                    Text("Year: ${vehicle.year}")
                                    Text(
                                            "Model: ${vehicle.vehicleModelName ?: "Chưa có thông tin"}"
                                    )
                                    Text(
                                            "Brand: ${vehicle.vehicleBrandName ?: "Chưa có thông tin"}"
                                    )
                                    Text("Customer: ${vehicle.customerID}")
                                }
                            }
                        }

                        // Services section
                        if (servicesUiState.services.isNotEmpty()) {
                            item {
                                Text(
                                        text = "Services (${servicesUiState.services.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        items(servicesUiState.services) { service ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                            text = "ID: ${service.serviceId}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                    )
                                    Text("Name: ${service.serviceName}")
                                    Text("Description: ${service.description ?: "N/A"}")
                                    Text("Price: ${service.price}đ")
                                    Text("Work Units: ${service.standardWorkUnits ?: "N/A"}")
                                    Text("Template: ${service.decalTemplateName ?: "N/A"}")
                                    Text("Type: ${service.decalTypeName ?: "N/A"}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
