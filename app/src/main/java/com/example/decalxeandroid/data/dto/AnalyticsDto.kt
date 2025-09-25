package com.example.decalxeandroid.data.dto

import com.google.gson.annotations.SerializedName

data class ServiceStatisticsDto(
    @SerializedName("totalServices")
    val totalServices: Int,
    
    @SerializedName("activeServices")
    val activeServices: Int,
    
    @SerializedName("totalRevenue")
    val totalRevenue: Double,
    
    @SerializedName("averageServicePrice")
    val averageServicePrice: Double,
    
    @SerializedName("topServices")
    val topServices: List<TopServiceDto>,
    
    @SerializedName("serviceCategories")
    val serviceCategories: List<ServiceCategoryDto>,
    
    @SerializedName("monthlyRevenue")
    val monthlyRevenue: List<MonthlyRevenueDto>
)

data class TopServiceDto(
    @SerializedName("serviceID")
    val serviceID: String,
    
    @SerializedName("serviceName")
    val serviceName: String,
    
    @SerializedName("orderCount")
    val orderCount: Int,
    
    @SerializedName("totalRevenue")
    val totalRevenue: Double
)

data class ServiceCategoryDto(
    @SerializedName("category")
    val category: String,
    
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("totalRevenue")
    val totalRevenue: Double
)

data class MonthlyRevenueDto(
    @SerializedName("month")
    val month: String,
    
    @SerializedName("revenue")
    val revenue: Double,
    
    @SerializedName("orderCount")
    val orderCount: Int
)

data class SalesStatisticsDto(
    @SerializedName("totalOrders")
    val totalOrders: Int,
    
    @SerializedName("totalRevenue")
    val totalRevenue: Double,
    
    @SerializedName("averageOrderValue")
    val averageOrderValue: Double
)

// Add missing Analytics DTOs
data class AnalyticsDashboardDto(
    @SerializedName("totalRevenue")
    val totalRevenue: Double,
    
    @SerializedName("totalOrders")
    val totalOrders: Int,
    
    @SerializedName("totalCustomers")
    val totalCustomers: Int,
    
    @SerializedName("totalEmployees")
    val totalEmployees: Int,
    
    @SerializedName("recentOrders")
    val recentOrders: List<OrderDto>,
    
    @SerializedName("topServices")
    val topServices: List<TopServiceDto>,
    
    @SerializedName("monthlyRevenue")
    val monthlyRevenue: List<MonthlyRevenueDto>
)

data class RevenueAnalyticsDto(
    @SerializedName("totalRevenue")
    val totalRevenue: Double,
    
    @SerializedName("monthlyRevenue")
    val monthlyRevenue: List<MonthlyRevenueDto>,
    
    @SerializedName("revenueByService")
    val revenueByService: List<ServiceRevenueDto>,
    
    @SerializedName("revenueByStore")
    val revenueByStore: List<StoreRevenueDto>
)

data class OrderAnalyticsDto(
    @SerializedName("totalOrders")
    val totalOrders: Int,
    
    @SerializedName("completedOrders")
    val completedOrders: Int,
    
    @SerializedName("pendingOrders")
    val pendingOrders: Int,
    
    @SerializedName("ordersByStatus")
    val ordersByStatus: Map<String, Int>,
    
    @SerializedName("averageOrderValue")
    val averageOrderValue: Double
)

data class CustomerAnalyticsDto(
    @SerializedName("totalCustomers")
    val totalCustomers: Int,
    
    @SerializedName("newCustomersThisMonth")
    val newCustomersThisMonth: Int,
    
    @SerializedName("activeCustomers")
    val activeCustomers: Int,
    
    @SerializedName("customersByLocation")
    val customersByLocation: Map<String, Int>
)

// Add missing DTOs
data class ServiceRevenueDto(
    @SerializedName("serviceName")
    val serviceName: String,
    
    @SerializedName("revenue")
    val revenue: Double,
    
    @SerializedName("orderCount")
    val orderCount: Int
)

data class StoreRevenueDto(
    @SerializedName("storeName")
    val storeName: String,
    
    @SerializedName("revenue")
    val revenue: Double,
    
    @SerializedName("orderCount")
    val orderCount: Int
)



