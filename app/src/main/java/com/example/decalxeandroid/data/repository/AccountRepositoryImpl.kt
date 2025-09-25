package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.data.remote.AccountApiService
import com.example.decalxeandroid.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AccountRepositoryImpl(
    private val accountApiService: AccountApiService
) : AccountRepository {
    
    override fun getAccounts(page: Int, pageSize: Int): Flow<Result<List<AccountDto>>> = flow {
        try {
            val accounts = accountApiService.getAccounts(page, pageSize)
            emit(Result.success(accounts))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getAccountById(accountId: String): Flow<Result<AccountDto>> = flow {
        try {
            val account = accountApiService.getAccountById(accountId)
            emit(Result.success(account))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun createAccount(account: CreateAccountDto): Flow<Result<AccountDto>> = flow {
        try {
            val createdAccount = accountApiService.createAccount(account)
            emit(Result.success(createdAccount))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun updateAccount(accountId: String, account: UpdateAccountDto): Flow<Result<AccountDto>> = flow {
        try {
            val updatedAccount = accountApiService.updateAccount(accountId, account)
            emit(Result.success(updatedAccount))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun deleteAccount(accountId: String): Flow<Result<String>> = flow {
        try {
            val message = accountApiService.deleteAccount(accountId)
            emit(Result.success(message))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getAccountsByCustomer(customerId: String): Flow<Result<List<AccountDto>>> = flow {
        try {
            val accounts = accountApiService.getAccountsByCustomer(customerId)
            emit(Result.success(accounts))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getAccountStatistics(): Flow<Result<AccountStatisticsDto>> = flow {
        try {
            val statistics = accountApiService.getAccountStatistics()
            emit(Result.success(statistics))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getAccountsByStatus(status: String): Flow<Result<List<AccountDto>>> = flow {
        try {
            val accounts = accountApiService.getAccountsByStatus(status)
            emit(Result.success(accounts))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getAccountsByType(type: String): Flow<Result<List<AccountDto>>> = flow {
        try {
            val accounts = accountApiService.getAccountsByType(type)
            emit(Result.success(accounts))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun searchAccounts(query: String): Flow<Result<List<AccountDto>>> = flow {
        try {
            val accounts = accountApiService.searchAccounts(query)
            emit(Result.success(accounts))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    override fun getAccountsByBalanceRange(minBalance: Double, maxBalance: Double): Flow<Result<List<AccountDto>>> = flow {
        try {
            val accounts = accountApiService.getAccountsByBalanceRange(minBalance, maxBalance)
            emit(Result.success(accounts))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}



