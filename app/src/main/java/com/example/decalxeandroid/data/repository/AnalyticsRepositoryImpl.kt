package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.data.remote.AnalyticsApiService
import com.example.decalxeandroid.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AnalyticsRepositoryImpl(
    private val analyticsApiService: AnalyticsApiService
) : AnalyticsRepository {
    
    override fun getDashboardAnalytics(): Flow<Result<AnalyticsDashboardDto>> = flow {
        try {
            val analytics = analyticsApiService.getDashboardAnalytics()
            emit(Result.success(analytics))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getRevenueAnalytics(startDate: String?, endDate: String?, period: String): Flow<Result<RevenueAnalyticsDto>> = flow {
        try {
            val analytics = analyticsApiService.getRevenueAnalytics(startDate, endDate, period)
            emit(Result.success(analytics))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getOrderAnalytics(startDate: String?, endDate: String?, status: String?): Flow<Result<OrderAnalyticsDto>> = flow {
        try {
            val analytics = analyticsApiService.getOrderAnalytics(startDate, endDate, status)
            emit(Result.success(analytics))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getCustomerAnalytics(startDate: String?, endDate: String?): Flow<Result<CustomerAnalyticsDto>> = flow {
        try {
            val analytics = analyticsApiService.getCustomerAnalytics(startDate, endDate)
            emit(Result.success(analytics))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getTopSellingServices(limit: Int, period: String): Flow<Result<List<Map<String, Any>>>> = flow {
        try {
            val services = analyticsApiService.getTopSellingServices(limit, period)
            emit(Result.success(services))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getTopCustomers(limit: Int, period: String): Flow<Result<List<Map<String, Any>>>> = flow {
        try {
            val customers = analyticsApiService.getTopCustomers(limit, period)
            emit(Result.success(customers))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getStorePerformance(startDate: String?, endDate: String?): Flow<Result<List<Map<String, Any>>>> = flow {
        try {
            val performance = analyticsApiService.getStorePerformance(startDate, endDate)
            emit(Result.success(performance))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getEmployeePerformance(startDate: String?, endDate: String?): Flow<Result<List<Map<String, Any>>>> = flow {
        try {
            val performance = analyticsApiService.getEmployeePerformance(startDate, endDate)
            emit(Result.success(performance))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}



