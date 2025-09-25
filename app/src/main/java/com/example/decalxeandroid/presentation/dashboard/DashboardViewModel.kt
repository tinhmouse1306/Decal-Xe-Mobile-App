package com.example.decalxeandroid.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Order
import com.example.decalxeandroid.domain.model.DecalService
import com.example.decalxeandroid.domain.repository.CustomerRepository
import com.example.decalxeandroid.domain.repository.CustomerVehicleRepository
import com.example.decalxeandroid.domain.repository.OrderRepository
import com.example.decalxeandroid.domain.repository.DecalServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val vehicleRepository: CustomerVehicleRepository,
    private val decalServiceRepository: DecalServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun loadDashboardData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // Simulate loading time
                kotlinx.coroutines.delay(1000)
                
                // Load DecalServices from repository
                decalServiceRepository.getServices().collect { servicesResult ->
                    when (servicesResult) {
                        is com.example.decalxeandroid.domain.model.Result.Success -> {
                            // Create mock data for better UI experience
                            val mockOrders = listOf(
                    com.example.decalxeandroid.domain.model.Order(
                        orderId = "ORD001",
                        orderNumber = "ORD001",
                        customerId = "CUST001",
                        customerFullName = "Nguyễn Văn An",
                        vehicleId = "VEH001",
                        vehicleLicensePlate = "30A-12345",
                        assignedEmployeeId = "EMP001",
                        assignedEmployeeName = "Trần Thị Bình",
                        orderStatus = "Đang xử lý",
                        currentStage = "Thiết kế",
                        totalAmount = 2500000.0,
                        depositAmount = 750000.0,
                        remainingAmount = 1750000.0,
                        orderDate = "2024-01-15",
                        expectedCompletionDate = "2024-01-25",
                        actualCompletionDate = null,
                        notes = "Khách hàng yêu cầu thiết kế đặc biệt",
                        isActive = true,
                        createdAt = "2024-01-15T08:00:00",
                        updatedAt = null,
                        chassisNumber = "ABC123456789",
                        vehicleModelName = "Honda Civic",
                        vehicleBrandName = "Honda",
                        expectedArrivalTime = "09:00",
                        priority = "Normal",
                        isCustomDecal = true,
                        storeId = "STORE001",
                        description = "Dán decal toàn thân xe",
                        customerPhoneNumber = "0123456789",
                        customerEmail = "nguyenvanan@email.com",
                        customerAddress = "123 Đường ABC, Quận 1, TP.HCM",
                        accountId = "ACC001",
                        accountUsername = "nguyenvanan",
                        accountCreated = true
                    ),
                    com.example.decalxeandroid.domain.model.Order(
                        orderId = "ORD002",
                        orderNumber = "ORD002",
                        customerId = "CUST002",
                        customerFullName = "Trần Thị Bình",
                        vehicleId = "VEH002",
                        vehicleLicensePlate = "51B-67890",
                        assignedEmployeeId = "EMP002",
                        assignedEmployeeName = "Lê Văn Cường",
                        orderStatus = "Hoàn thành",
                        currentStage = "Giao hàng",
                        totalAmount = 1800000.0,
                        depositAmount = 540000.0,
                        remainingAmount = 1260000.0,
                        orderDate = "2024-01-10",
                        expectedCompletionDate = "2024-01-20",
                        actualCompletionDate = "2024-01-18",
                        notes = "Khách hàng hài lòng với kết quả",
                        isActive = true,
                        createdAt = "2024-01-10T09:00:00",
                        updatedAt = "2024-01-18T16:30:00",
                        chassisNumber = "XYZ987654321",
                        vehicleModelName = "Toyota Camry",
                        vehicleBrandName = "Toyota",
                        expectedArrivalTime = "10:00",
                        priority = "High",
                        isCustomDecal = false,
                        storeId = "STORE001",
                        description = "Dán decal logo công ty",
                        customerPhoneNumber = "0987654321",
                        customerEmail = "tranthibinh@email.com",
                        customerAddress = "456 Đường XYZ, Quận 2, TP.HCM",
                        accountId = "ACC002",
                        accountUsername = "tranthibinh",
                        accountCreated = true
                    ),
                    com.example.decalxeandroid.domain.model.Order(
                        orderId = "ORD003",
                        orderNumber = "ORD003",
                        customerId = "CUST003",
                        customerFullName = "Phạm Văn Đức",
                        vehicleId = "VEH003",
                        vehicleLicensePlate = "43C-11111",
                        assignedEmployeeId = "EMP003",
                        assignedEmployeeName = "Nguyễn Thị Lan",
                        orderStatus = "Đang thiết kế",
                        currentStage = "Tạo mẫu",
                        totalAmount = 3200000.0,
                        depositAmount = 960000.0,
                        remainingAmount = 2240000.0,
                        orderDate = "2024-01-12",
                        expectedCompletionDate = "2024-01-28",
                        actualCompletionDate = null,
                        notes = "Thiết kế phức tạp, cần thời gian",
                        isActive = true,
                        createdAt = "2024-01-12T10:00:00",
                        updatedAt = null,
                        chassisNumber = "DEF456789012",
                        vehicleModelName = "BMW X5",
                        vehicleBrandName = "BMW",
                        expectedArrivalTime = "11:00",
                        priority = "Normal",
                        isCustomDecal = true,
                        storeId = "STORE002",
                        description = "Dán decal carbon fiber",
                        customerPhoneNumber = "0369258147",
                        customerEmail = "phamvanduc@email.com",
                        customerAddress = "789 Đường DEF, Quận 3, TP.HCM",
                        accountId = "ACC003",
                        accountUsername = "phamvanduc",
                        accountCreated = true
                    )
                )
                
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                totalOrders = mockOrders.size,
                                totalCustomers = 14, // Mock number
                                totalVehicles = 8, // Mock number
                                totalRevenue = mockOrders.sumOf { it.totalAmount },
                                recentOrders = mockOrders.take(3), // Show recent 3 orders
                                decalServices = servicesResult.data.take(6) // Show first 6 services
                            )
                        }
                        is com.example.decalxeandroid.domain.model.Result.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = servicesResult.message
                            )
                        }
                        else -> {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Unknown error loading services"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Có lỗi xảy ra khi tải dữ liệu dashboard"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalOrders: Int = 0,
    val totalCustomers: Int = 0,
    val totalVehicles: Int = 0,
    val totalRevenue: Double = 0.0,
    val recentOrders: List<Order> = emptyList(),
    val decalServices: List<DecalService> = emptyList()
)
