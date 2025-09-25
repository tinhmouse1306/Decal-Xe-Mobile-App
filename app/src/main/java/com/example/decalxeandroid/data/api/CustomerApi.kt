package com.example.decalxeandroid.data.api

import com.example.decalxeandroid.data.dto.CustomerDto
import com.example.decalxeandroid.data.dto.CreateCustomerDto
import com.example.decalxeandroid.data.dto.UpdateCustomerDto
import retrofit2.Response
import retrofit2.http.*

interface CustomerApi {
    
    @GET("Customers")
    suspend fun getCustomers(): Response<List<CustomerDto>>
    
    @GET("Customers/{id}")
    suspend fun getCustomerById(@Path("id") id: String): Response<CustomerDto>
    
    @POST("Customers")
    suspend fun createCustomer(@Body customer: CreateCustomerDto): Response<CustomerDto>
    
    @PUT("Customers/{id}")
    suspend fun updateCustomer(
        @Path("id") id: String, 
        @Body customer: UpdateCustomerDto
    ): Response<CustomerDto>
    
    @DELETE("Customers/{id}")
    suspend fun deleteCustomer(@Path("id") id: String): Response<Unit>
}
