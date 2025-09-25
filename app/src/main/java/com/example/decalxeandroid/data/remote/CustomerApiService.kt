package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.CustomerRequestDto
import retrofit2.http.*

interface CustomerApiService {
    @POST(ApiConstants.CUSTOMERS_ENDPOINT)
    suspend fun createCustomer(@Body customerRequest: CustomerRequestDto): String
}