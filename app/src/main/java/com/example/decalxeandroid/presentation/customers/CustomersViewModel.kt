package com.example.decalxeandroid.presentation.customers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Customer
import com.example.decalxeandroid.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class CustomersViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CustomersUiState>(CustomersUiState.Loading)
    val uiState: StateFlow<CustomersUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _currentFilters = MutableStateFlow<List<CustomerFilter>>(emptyList())
    val currentFilters: StateFlow<List<CustomerFilter>> = _currentFilters.asStateFlow()
    
    private val _activeFilters = MutableStateFlow<List<CustomerFilter>>(emptyList())
    val activeFilters: StateFlow<List<CustomerFilter>> = _activeFilters.asStateFlow()
    
    private var allCustomers = listOf<Customer>()
    
    init {
        loadCustomers()
    }
    
    fun loadCustomers() {
        viewModelScope.launch {
            _uiState.value = CustomersUiState.Loading
            
            try {
                val result = customerRepository.getCustomers()
                result.collect { customersResult ->
                    when (customersResult) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            allCustomers = customersResult.data
                            applyFiltersAndSearch()
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value = CustomersUiState.Error(customersResult.message)
                        }
                        else -> {
                            _uiState.value = CustomersUiState.Error("Kết quả không xác định")
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CustomersUiState.Error("Lỗi không xác định: ${e.message}")
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFiltersAndSearch()
    }
    
    fun searchCustomers() {
        applyFiltersAndSearch()
    }
    
    fun applyFilters(filters: List<CustomerFilter>) {
        _currentFilters.value = filters
        _activeFilters.value = filters
        applyFiltersAndSearch()
    }
    
    fun removeFilter(filter: CustomerFilter) {
        val updatedFilters = _activeFilters.value.toMutableList()
        updatedFilters.remove(filter)
        _activeFilters.value = updatedFilters
        _currentFilters.value = updatedFilters
        applyFiltersAndSearch()
    }
    
    private fun applyFiltersAndSearch() {
        var filteredCustomers = allCustomers
        
        // Apply search
        val query = _searchQuery.value.trim()
        if (query.isNotEmpty()) {
            filteredCustomers = filteredCustomers.filter { customer ->
                customer.fullName.contains(query, ignoreCase = true) ||
                customer.phoneNumber?.contains(query, ignoreCase = true) == true ||
                customer.email?.contains(query, ignoreCase = true) == true
            }
        }
        
        // Apply filters
        val activeFilters = _activeFilters.value
        if (activeFilters.isNotEmpty()) {
            filteredCustomers = filteredCustomers.filter { customer ->
                activeFilters.all { filter ->
                    when (filter) {
                        is CustomerFilter.Active -> customer.isActive
                        is CustomerFilter.Inactive -> !customer.isActive
                        is CustomerFilter.RegisteredThisWeek -> {
                            // TODO: Implement date filtering logic
                            true
                        }
                        is CustomerFilter.RegisteredThisMonth -> {
                            // TODO: Implement date filtering logic
                            true
                        }
                    }
                }
            }
        }
        
        _uiState.value = CustomersUiState.Success(filteredCustomers)
    }
}

sealed class CustomersUiState {
    object Loading : CustomersUiState()
    data class Success(val customers: List<Customer>) : CustomersUiState()
    data class Error(val message: String) : CustomersUiState()
}
