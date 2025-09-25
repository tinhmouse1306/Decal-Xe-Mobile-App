package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.AnalyticsDashboardDto
import com.example.decalxeandroid.data.dto.RevenueAnalyticsDto
import com.example.decalxeandroid.data.dto.OrderAnalyticsDto
import com.example.decalxeandroid.data.dto.CustomerAnalyticsDto
import retrofit2.http.*

interface AnalyticsApiService {
    @GET(ApiConstants.ANALYTICS_DASHBOARD_ENDPOINT)
    suspend fun getDashboardAnalytics(): AnalyticsDashboardDto
    
    @GET(ApiConstants.ANALYTICS_REVENUE_ENDPOINT)
    suspend fun getRevenueAnalytics(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("period") period: String = "monthly"
    ): RevenueAnalyticsDto
    
    @GET(ApiConstants.ANALYTICS_ORDERS_ENDPOINT)
    suspend fun getOrderAnalytics(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("status") status: String? = null
    ): OrderAnalyticsDto
    
    @GET(ApiConstants.ANALYTICS_CUSTOMERS_ENDPOINT)
    suspend fun getCustomerAnalytics(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): CustomerAnalyticsDto
    
    @GET("Analytics/top-selling-services")
    suspend fun getTopSellingServices(
        @Query("limit") limit: Int = 10,
        @Query("period") period: String = "monthly"
    ): List<Map<String, Any>>
    
    @GET("Analytics/top-customers")
    suspend fun getTopCustomers(
        @Query("limit") limit: Int = 10,
        @Query("period") period: String = "monthly"
    ): List<Map<String, Any>>
    
    @GET("Analytics/store-performance")
    suspend fun getStorePerformance(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): List<Map<String, Any>>
    
    @GET("Analytics/employee-performance")
    suspend fun getEmployeePerformance(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): List<Map<String, Any>>
}



