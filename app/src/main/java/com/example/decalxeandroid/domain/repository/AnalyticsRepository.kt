package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.data.dto.*
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun getDashboardAnalytics(): Flow<Result<AnalyticsDashboardDto>>
    fun getRevenueAnalytics(startDate: String?, endDate: String?, period: String = "monthly"): Flow<Result<RevenueAnalyticsDto>>
    fun getOrderAnalytics(startDate: String?, endDate: String?, status: String?): Flow<Result<OrderAnalyticsDto>>
    fun getCustomerAnalytics(startDate: String?, endDate: String?): Flow<Result<CustomerAnalyticsDto>>
    fun getTopSellingServices(limit: Int = 10, period: String = "monthly"): Flow<Result<List<Map<String, Any>>>>
    fun getTopCustomers(limit: Int = 10, period: String = "monthly"): Flow<Result<List<Map<String, Any>>>>
    fun getStorePerformance(startDate: String?, endDate: String?): Flow<Result<List<Map<String, Any>>>>
    fun getEmployeePerformance(startDate: String?, endDate: String?): Flow<Result<List<Map<String, Any>>>>
}



