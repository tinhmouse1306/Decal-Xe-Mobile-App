package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.data.remote.PaymentApiService
import com.example.decalxeandroid.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PaymentRepositoryImpl(
    private val paymentApiService: PaymentApiService
) : PaymentRepository {
    
    override fun getPayments(page: Int, pageSize: Int): Flow<Result<List<PaymentDto>>> = flow {
        try {
            val payments = paymentApiService.getPayments(page, pageSize)
            emit(Result.success(payments))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getPaymentById(paymentId: String): Flow<Result<PaymentDto>> = flow {
        try {
            val payment = paymentApiService.getPaymentById(paymentId)
            emit(Result.success(payment))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun createPayment(payment: CreatePaymentDto): Flow<Result<PaymentDto>> = flow {
        try {
            val createdPayment = paymentApiService.createPayment(payment)
            emit(Result.success(createdPayment))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun updatePayment(paymentId: String, payment: UpdatePaymentDto): Flow<Result<PaymentDto>> = flow {
        try {
            val updatedPayment = paymentApiService.updatePayment(paymentId, payment)
            emit(Result.success(updatedPayment))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun deletePayment(paymentId: String): Flow<Result<String>> = flow {
        try {
            val message = paymentApiService.deletePayment(paymentId)
            emit(Result.success(message))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getPaymentsByOrder(orderId: String): Flow<Result<List<PaymentDto>>> = flow {
        try {
            val payments = paymentApiService.getPaymentsByOrder(orderId)
            emit(Result.success(payments))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getPaymentStatistics(): Flow<Result<PaymentStatisticsDto>> = flow {
        try {
            val statistics = paymentApiService.getPaymentStatistics()
            emit(Result.success(statistics))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getPaymentsByStatus(status: String): Flow<Result<List<PaymentDto>>> = flow {
        try {
            val payments = paymentApiService.getPaymentsByStatus(status)
            emit(Result.success(payments))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getPaymentsByDateRange(startDate: String, endDate: String): Flow<Result<List<PaymentDto>>> = flow {
        try {
            val payments = paymentApiService.getPaymentsByDateRange(startDate, endDate)
            emit(Result.success(payments))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getPaymentsByMethod(method: String): Flow<Result<List<PaymentDto>>> = flow {
        try {
            val payments = paymentApiService.getPaymentsByMethod(method)
            emit(Result.success(payments))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getPaymentsByCustomer(customerId: String): Flow<Result<List<PaymentDto>>> = flow {
        try {
            val payments = paymentApiService.getPaymentsByCustomer(customerId)
            emit(Result.success(payments))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}



