package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.data.dto.*
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    fun getPayments(page: Int = 1, pageSize: Int = 20): Flow<Result<List<PaymentDto>>>
    fun getPaymentById(paymentId: String): Flow<Result<PaymentDto>>
    fun createPayment(payment: CreatePaymentDto): Flow<Result<PaymentDto>>
    fun updatePayment(paymentId: String, payment: UpdatePaymentDto): Flow<Result<PaymentDto>>
    fun deletePayment(paymentId: String): Flow<Result<String>>
    fun getPaymentsByOrder(orderId: String): Flow<Result<List<PaymentDto>>>
    fun getPaymentStatistics(): Flow<Result<PaymentStatisticsDto>>
    fun getPaymentsByStatus(status: String): Flow<Result<List<PaymentDto>>>
    fun getPaymentsByDateRange(startDate: String, endDate: String): Flow<Result<List<PaymentDto>>>
    fun getPaymentsByMethod(method: String): Flow<Result<List<PaymentDto>>>
    fun getPaymentsByCustomer(customerId: String): Flow<Result<List<PaymentDto>>>
}



