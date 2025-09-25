package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.data.dto.*
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccounts(page: Int = 1, pageSize: Int = 20): Flow<Result<List<AccountDto>>>
    fun getAccountById(accountId: String): Flow<Result<AccountDto>>
    fun createAccount(account: CreateAccountDto): Flow<Result<AccountDto>>
    fun updateAccount(accountId: String, account: UpdateAccountDto): Flow<Result<AccountDto>>
    fun deleteAccount(accountId: String): Flow<Result<String>>
    fun getAccountsByCustomer(customerId: String): Flow<Result<List<AccountDto>>>
    fun getAccountStatistics(): Flow<Result<AccountStatisticsDto>>
    fun getAccountsByStatus(status: String): Flow<Result<List<AccountDto>>>
    fun getAccountsByType(type: String): Flow<Result<List<AccountDto>>>
    fun searchAccounts(query: String): Flow<Result<List<AccountDto>>>
    fun getAccountsByBalanceRange(minBalance: Double, maxBalance: Double): Flow<Result<List<AccountDto>>>
}



