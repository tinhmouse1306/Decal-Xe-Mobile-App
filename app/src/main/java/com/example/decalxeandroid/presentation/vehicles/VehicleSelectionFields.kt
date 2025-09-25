package com.example.decalxeandroid.presentation.vehicles

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.decalxeandroid.domain.model.Customer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSelectionField(
    customers: List<Customer>,
    selectedCustomerId: String?,
    onCustomerChanged: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = customers.find { it.customerId == selectedCustomerId }?.fullName ?: "Chọn khách hàng",
            onValueChange = { },
            readOnly = true,
            label = { Text("Khách hàng *") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = selectedCustomerId == null,
            supportingText = if (selectedCustomerId == null) {
                { Text("Vui lòng chọn khách hàng", color = MaterialTheme.colorScheme.error) }
            } else null
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            customers.forEach { customer ->
                DropdownMenuItem(
                    text = { 
                        Column {
                            Text(customer.fullName)
                            customer.phoneNumber?.let { 
                                Text(it, style = MaterialTheme.typography.bodySmall) 
                            }
                        }
                    },
                    onClick = {
                        onCustomerChanged(customer.customerId)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleModelSelectionField(
    vehicleModels: List<SimpleVehicleModel>,
    selectedModelId: String?,
    onVehicleModelChanged: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = vehicleModels.find { it.id == selectedModelId }?.let { 
                "${it.name} - ${it.brand}"
            } ?: "Chọn mẫu xe",
            onValueChange = { },
            readOnly = true,
            label = { Text("Mẫu xe *") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            leadingIcon = {
                Icon(Icons.Default.DirectionsCar, contentDescription = null)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = selectedModelId == null,
            supportingText = if (selectedModelId == null) {
                { Text("Vui lòng chọn mẫu xe", color = MaterialTheme.colorScheme.error) }
            } else null
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            vehicleModels.forEach { model ->
                DropdownMenuItem(
                    text = { 
                        Column {
                            Text(model.name)
                            Text(model.brand, style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    onClick = {
                        onVehicleModelChanged(model.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
