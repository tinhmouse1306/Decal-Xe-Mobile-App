package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.*
import retrofit2.http.*

interface ApiService {
    
    // Auth APIs
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequestDto): LoginResponseDto
    
    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterDto): LoginResponseDto
    
    // Customer APIs
    @GET("customers")
    suspend fun getAllCustomers(): List<CustomerDto>
    
    @GET("customers/{id}")
    suspend fun getCustomerById(@Path("id") customerId: String): CustomerDto
    
    @POST("customers")
    suspend fun createCustomer(@Body customer: CustomerDto): CustomerDto
    
    @PUT("customers/{id}")
    suspend fun updateCustomer(@Path("id") customerId: String, @Body customer: CustomerDto): CustomerDto
    
    @DELETE("customers/{id}")
    suspend fun deleteCustomer(@Path("id") customerId: String): Boolean
    
    // Order APIs
    @GET("orders")
    suspend fun getAllOrders(): List<OrderDto>
    
    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: String): OrderDto
    
    @POST("orders")
    suspend fun createOrder(@Body order: OrderDto): OrderDto
    
    @PUT("orders/{id}")
    suspend fun updateOrder(@Path("id") orderId: String, @Body order: OrderDto): OrderDto
    
    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") orderId: String): Boolean
    
    @GET("orders/customer/{customerId}")
    suspend fun getOrdersByCustomerId(@Path("customerId") customerId: String): List<OrderDto>
    
    @GET("orders/status/{status}")
    suspend fun getOrdersByStatus(@Path("status") status: String): List<OrderDto>
    
    @GET("orders/date-range")
    suspend fun getOrdersByDateRange(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): List<OrderDto>
    
    // Vehicle APIs
    @GET("vehicles")
    suspend fun getAllVehicles(): List<CustomerVehicleDto>
    
    @GET("vehicles/{id}")
    suspend fun getVehicleById(@Path("id") vehicleId: String): CustomerVehicleDto
    
    @POST("vehicles")
    suspend fun createVehicle(@Body vehicle: CustomerVehicleDto): CustomerVehicleDto
    
    @PUT("vehicles/{id}")
    suspend fun updateVehicle(@Path("id") vehicleId: String, @Body vehicle: CustomerVehicleDto): CustomerVehicleDto
    
    @DELETE("vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") vehicleId: String): Boolean
    
    // Employee APIs
    @GET("employees")
    suspend fun getAllEmployees(): List<EmployeeDto>
    
    @GET("employees/{id}")
    suspend fun getEmployeeById(@Path("id") employeeId: String): EmployeeDto
    
    @POST("employees")
    suspend fun createEmployee(@Body employee: EmployeeDto): EmployeeDto
    
    @PUT("employees/{id}")
    suspend fun updateEmployee(@Path("id") employeeId: String, @Body employee: EmployeeDto): EmployeeDto
    
    @DELETE("employees/{id}")
    suspend fun deleteEmployee(@Path("id") employeeId: String): Boolean
    
    // Service APIs
    @GET("services")
    suspend fun getAllServices(): List<DecalServiceDto>
    
    @GET("services/{id}")
    suspend fun getServiceById(@Path("id") serviceId: String): DecalServiceDto
    
    @POST("services")
    suspend fun createService(@Body service: DecalServiceDto): DecalServiceDto
    
    @PUT("services/{id}")
    suspend fun updateService(@Path("id") serviceId: String, @Body service: DecalServiceDto): DecalServiceDto
    
    @DELETE("services/{id}")
    suspend fun deleteService(@Path("id") serviceId: String): Boolean
    
    // Store APIs
    @GET("stores")
    suspend fun getAllStores(): List<StoreDto>
    
    @GET("stores/{id}")
    suspend fun getStoreById(@Path("id") storeId: String): StoreDto
    
    @POST("stores")
    suspend fun createStore(@Body store: StoreDto): StoreDto
    
    @PUT("stores/{id}")
    suspend fun updateStore(@Path("id") storeId: String, @Body store: StoreDto): StoreDto
    
    @DELETE("stores/{id}")
    suspend fun deleteStore(@Path("id") storeId: String): Boolean
    
    // Payment APIs
    @GET("payments")
    suspend fun getAllPayments(): List<PaymentDto>
    
    @GET("payments/{id}")
    suspend fun getPaymentById(@Path("id") paymentId: String): PaymentDto
    
    @POST("payments")
    suspend fun createPayment(@Body payment: PaymentDto): PaymentDto
    
    @PUT("payments/{id}")
    suspend fun updatePayment(@Path("id") paymentId: String, @Body payment: PaymentDto): PaymentDto
    
    @DELETE("payments/{id}")
    suspend fun deletePayment(@Path("id") paymentId: String): Boolean
    
    // Account APIs
    @GET("accounts")
    suspend fun getAllAccounts(): List<AccountDto>
    
    @GET("accounts/{id}")
    suspend fun getAccountById(@Path("id") accountId: String): AccountDto
    
    @POST("accounts")
    suspend fun createAccount(@Body account: AccountDto): AccountDto
    
    @PUT("accounts/{id}")
    suspend fun updateAccount(@Path("id") accountId: String, @Body account: AccountDto): AccountDto
    
    @DELETE("accounts/{id}")
    suspend fun deleteAccount(@Path("id") accountId: String): Boolean
    
    // Analytics APIs
    @GET("analytics/service-statistics")
    suspend fun getServiceStatistics(): ServiceStatisticsDto
    
    @GET("analytics/sales-statistics")
    suspend fun getSalesStatistics(): SalesStatisticsDto
}
