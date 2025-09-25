package com.example.decalxeandroid.presentation.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.model.CustomerVehicle
import com.example.decalxeandroid.domain.model.Employee

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
fun VehicleSelectionField(
    vehicles: List<CustomerVehicle>,
    selectedVehicleId: String?,
    onVehicleChanged: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = vehicles.find { it.vehicleID == selectedVehicleId }?.let { 
                "${it.licensePlate} - ${it.vehicleModelName ?: "N/A"}"
            } ?: "Chọn xe",
            onValueChange = { },
            readOnly = true,
            label = { Text("Xe *") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            leadingIcon = {
                Icon(Icons.Default.DirectionsCar, contentDescription = null)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = selectedVehicleId == null,
            supportingText = if (selectedVehicleId == null) {
                { Text("Vui lòng chọn xe", color = MaterialTheme.colorScheme.error) }
            } else null
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            vehicles.forEach { vehicle ->
                DropdownMenuItem(
                    text = { 
                        Column {
                            Text(vehicle.licensePlate)
                            Text(
                                "${vehicle.vehicleModelName ?: "N/A"} - ${vehicle.vehicleBrandName ?: "N/A"}", 
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    onClick = {
                        onVehicleChanged(vehicle.vehicleID)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeSelectionField(
    employees: List<Employee>,
    selectedEmployeeId: String?,
    onEmployeeChanged: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = employees.find { it.employeeId == selectedEmployeeId }?.let { "${it.firstName} ${it.lastName}" } ?: "Chọn nhân viên",
            onValueChange = { },
            readOnly = true,
            label = { Text("Nhân viên phụ trách *") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            leadingIcon = {
                Icon(Icons.Default.WorkOutline, contentDescription = null)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            isError = selectedEmployeeId == null,
            supportingText = if (selectedEmployeeId == null) {
                { Text("Vui lòng chọn nhân viên", color = MaterialTheme.colorScheme.error) }
            } else null
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            employees.forEach { employee ->
                DropdownMenuItem(
                    text = { 
                        Column {
                            Text("${employee.firstName} ${employee.lastName}")
                            Text(employee.accountRoleName ?: "N/A", style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    onClick = {
                        onEmployeeChanged(employee.employeeId)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrioritySelectionField(
    priority: String,
    onPriorityChanged: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val priorities = listOf("Low", "Normal", "High", "Urgent")
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = priority,
            onValueChange = { },
            readOnly = true,
            label = { Text("Độ ưu tiên") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            leadingIcon = {
                Icon(Icons.Default.Flag, contentDescription = null)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            priorities.forEach { priorityOption ->
                DropdownMenuItem(
                    text = { Text(priorityOption) },
                    onClick = {
                        onPriorityChanged(priorityOption)
                        expanded = false
                    }
                )
            }
        }
    }
}
