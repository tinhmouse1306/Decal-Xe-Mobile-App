package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.AccountDto
import com.example.decalxeandroid.data.dto.CreateAccountDto
import com.example.decalxeandroid.data.dto.UpdateAccountDto
import com.example.decalxeandroid.data.dto.AccountStatisticsDto
import retrofit2.http.*

interface AccountApiService {
    @GET(ApiConstants.ACCOUNTS_ENDPOINT)
    suspend fun getAccounts(
        @Query("pageNumber") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): List<AccountDto>
    
    @GET(ApiConstants.ACCOUNT_BY_ID_ENDPOINT)
    suspend fun getAccountById(@Path("id") accountId: String): AccountDto
    
    @POST(ApiConstants.ACCOUNTS_ENDPOINT)
    suspend fun createAccount(@Body account: CreateAccountDto): AccountDto
    
    @PUT(ApiConstants.ACCOUNT_BY_ID_ENDPOINT)
    suspend fun updateAccount(
        @Path("id") accountId: String,
        @Body account: UpdateAccountDto
    ): AccountDto
    
    @DELETE(ApiConstants.ACCOUNT_BY_ID_ENDPOINT)
    suspend fun deleteAccount(@Path("id") accountId: String): String
    
    @GET(ApiConstants.ACCOUNT_BY_CUSTOMER_ENDPOINT)
    suspend fun getAccountsByCustomer(@Path("customerId") customerId: String): List<AccountDto>
    
    @GET(ApiConstants.ACCOUNT_STATISTICS_ENDPOINT)
    suspend fun getAccountStatistics(): AccountStatisticsDto
    
    @GET("${ApiConstants.ACCOUNTS_ENDPOINT}/by-status/{status}")
    suspend fun getAccountsByStatus(@Path("status") status: String): List<AccountDto>
    
    @GET("${ApiConstants.ACCOUNTS_ENDPOINT}/by-type/{type}")
    suspend fun getAccountsByType(@Path("type") type: String): List<AccountDto>
    
    @GET("${ApiConstants.ACCOUNTS_ENDPOINT}/search")
    suspend fun searchAccounts(@Query("query") query: String): List<AccountDto>
    
    @GET("${ApiConstants.ACCOUNTS_ENDPOINT}/by-balance-range")
    suspend fun getAccountsByBalanceRange(
        @Query("minBalance") minBalance: Double,
        @Query("maxBalance") maxBalance: Double
    ): List<AccountDto>
}



