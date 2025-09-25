package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.PaymentDto
import com.example.decalxeandroid.data.dto.CreatePaymentDto
import com.example.decalxeandroid.data.dto.UpdatePaymentDto
import com.example.decalxeandroid.data.dto.PaymentStatisticsDto
import retrofit2.http.*

interface PaymentApiService {
    @GET(ApiConstants.PAYMENTS_ENDPOINT)
    suspend fun getPayments(
        @Query("pageNumber") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): List<PaymentDto>
    
    @GET(ApiConstants.PAYMENT_BY_ID_ENDPOINT)
    suspend fun getPaymentById(@Path("id") paymentId: String): PaymentDto
    
    @POST(ApiConstants.PAYMENTS_ENDPOINT)
    suspend fun createPayment(@Body payment: CreatePaymentDto): PaymentDto
    
    @PUT(ApiConstants.PAYMENT_BY_ID_ENDPOINT)
    suspend fun updatePayment(
        @Path("id") paymentId: String,
        @Body payment: UpdatePaymentDto
    ): PaymentDto
    
    @DELETE(ApiConstants.PAYMENT_BY_ID_ENDPOINT)
    suspend fun deletePayment(@Path("id") paymentId: String): String
    
    @GET(ApiConstants.PAYMENT_BY_ORDER_ENDPOINT)
    suspend fun getPaymentsByOrder(@Path("orderId") orderId: String): List<PaymentDto>
    
    @GET(ApiConstants.PAYMENT_STATISTICS_ENDPOINT)
    suspend fun getPaymentStatistics(): PaymentStatisticsDto
    
    @GET("${ApiConstants.PAYMENTS_ENDPOINT}/by-status/{status}")
    suspend fun getPaymentsByStatus(@Path("status") status: String): List<PaymentDto>
    
    @GET("${ApiConstants.PAYMENTS_ENDPOINT}/by-date-range")
    suspend fun getPaymentsByDateRange(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<PaymentDto>
    
    @GET("${ApiConstants.PAYMENTS_ENDPOINT}/by-method/{method}")
    suspend fun getPaymentsByMethod(@Path("method") method: String): List<PaymentDto>
    
    @GET("${ApiConstants.PAYMENTS_ENDPOINT}/by-customer/{customerId}")
    suspend fun getPaymentsByCustomer(@Path("customerId") customerId: String): List<PaymentDto>
}



