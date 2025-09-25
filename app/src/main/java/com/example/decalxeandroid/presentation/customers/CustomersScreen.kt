package com.example.decalxeandroid.presentation.customers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    onNavigateToCustomerDetail: (String) -> Unit,
    onNavigateToAddCustomer: () -> Unit,
    viewModel: CustomersViewModel = viewModel(
        factory = CustomersViewModelFactory(
            customerRepository = AppContainer.customerRepository
        )
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val activeFilters by viewModel.activeFilters.collectAsState()
    val currentFilters by viewModel.currentFilters.collectAsState()
    
    var showSearchBar by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Khách hàng") },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(
                            if (showSearchBar) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Tìm kiếm"
                        )
                    }
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Lọc")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddCustomer) {
                Icon(Icons.Default.Add, contentDescription = "Thêm khách hàng")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            if (showSearchBar) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) },
                    onSearch = { viewModel.searchCustomers() },
                    active = false,
                    onActiveChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Search suggestions can be added here
                }
            }
            
            // Filter Chips
            if (activeFilters.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(activeFilters) { filter ->
                        FilterChip(
                            onClick = { viewModel.removeFilter(filter) },
                            label = { Text(filter.displayName) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, contentDescription = "Xóa bộ lọc")
                            },
                            selected = false
                        )
                    }
                }
            }
            
            // Content
            when (uiState) {
                is CustomersUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CustomersUiState.Success -> {
                    val customers = (uiState as CustomersUiState.Success).customers
                    
                    if (customers.isEmpty()) {
                        EmptyState(
                            onRefresh = { viewModel.loadCustomers() }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(customers) { customer ->
                                CustomerCard(
                                    customer = customer,
                                    onClick = { onNavigateToCustomerDetail(customer.customerId) }
                                )
                            }
                        }
                    }
                }
                is CustomersUiState.Error -> {
                    ErrorState(
                        message = (uiState as CustomersUiState.Error).message,
                        onRetry = { viewModel.loadCustomers() }
                    )
                }
            }
        }
        
        // Filter Dialog
        if (showFilterDialog) {
            FilterDialog(
                onDismiss = { showFilterDialog = false },
                onApplyFilters = { filters ->
                    viewModel.applyFilters(filters)
                    showFilterDialog = false
                },
                currentFilters = currentFilters
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerCard(
    customer: Customer,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with gradient background
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.size(56.dp),
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = customer.fullName.firstOrNull()?.uppercase() ?: "K",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Customer Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = customer.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Số điện thoại",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = customer.phoneNumber ?: "Chưa có số điện thoại",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (!customer.email.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = customer.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Status indicator
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                modifier = Modifier.size(8.dp)
            ) {
                // Active status indicator
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Xem chi tiết",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(onRefresh: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.People,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Chưa có khách hàng nào",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRefresh) {
                Text("Làm mới")
            }
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Thử lại")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDialog(
    onDismiss: () -> Unit,
    onApplyFilters: (List<CustomerFilter>) -> Unit,
    currentFilters: List<CustomerFilter>
) {
    var selectedFilters by remember { mutableStateOf(currentFilters.toMutableList()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Lọc khách hàng") },
        text = {
            Column {
                // Filter by status
                Text(
                    text = "Trạng thái",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = {
                            if (selectedFilters.contains(CustomerFilter.Active)) {
                                selectedFilters.remove(CustomerFilter.Active)
                            } else {
                                selectedFilters.add(CustomerFilter.Active)
                            }
                        },
                        label = { Text("Đang hoạt động") },
                        selected = selectedFilters.contains(CustomerFilter.Active)
                    )
                    
                    FilterChip(
                        onClick = {
                            if (selectedFilters.contains(CustomerFilter.Inactive)) {
                                selectedFilters.remove(CustomerFilter.Inactive)
                            } else {
                                selectedFilters.add(CustomerFilter.Inactive)
                            }
                        },
                        label = { Text("Không hoạt động") },
                        selected = selectedFilters.contains(CustomerFilter.Inactive)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Filter by registration date
                Text(
                    text = "Ngày đăng ký",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        onClick = {
                            if (selectedFilters.contains(CustomerFilter.RegisteredThisWeek)) {
                                selectedFilters.remove(CustomerFilter.RegisteredThisWeek)
                            } else {
                                selectedFilters.add(CustomerFilter.RegisteredThisWeek)
                            }
                        },
                        label = { Text("Tuần này") },
                        selected = selectedFilters.contains(CustomerFilter.RegisteredThisWeek)
                    )
                    
                    FilterChip(
                        onClick = {
                            if (selectedFilters.contains(CustomerFilter.RegisteredThisMonth)) {
                                selectedFilters.remove(CustomerFilter.RegisteredThisMonth)
                            } else {
                                selectedFilters.add(CustomerFilter.RegisteredThisMonth)
                            }
                        },
                        label = { Text("Tháng này") },
                        selected = selectedFilters.contains(CustomerFilter.RegisteredThisMonth)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onApplyFilters(selectedFilters) }) {
                Text("Áp dụng")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

sealed class CustomerFilter(val displayName: String) {
    object Active : CustomerFilter("Đang hoạt động")
    object Inactive : CustomerFilter("Không hoạt động")
    object RegisteredThisWeek : CustomerFilter("Đăng ký tuần này")
    object RegisteredThisMonth : CustomerFilter("Đăng ký tháng này")
}
