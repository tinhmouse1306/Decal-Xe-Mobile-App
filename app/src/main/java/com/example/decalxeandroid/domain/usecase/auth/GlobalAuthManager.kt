package com.example.decalxeandroid.domain.usecase.auth

import com.example.decalxeandroid.domain.model.AuthResult
import com.example.decalxeandroid.domain.model.User
import com.example.decalxeandroid.domain.model.EmployeeInfo
import com.example.decalxeandroid.domain.repository.AuthRepository
import com.example.decalxeandroid.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Global AuthManager singleton to maintain login state across the app
 */
object GlobalAuthManager {
    private var _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private var _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private var _currentEmployeeInfo = MutableStateFlow<EmployeeInfo?>(null)
    val currentEmployeeInfo: StateFlow<EmployeeInfo?> = _currentEmployeeInfo.asStateFlow()
    
    private var authRepository: AuthRepository? = null
    
    fun initialize(authRepository: AuthRepository) {
        this.authRepository = authRepository
        // If already logged in, restore user data
        kotlinx.coroutines.runBlocking {
            try {
                val isLoggedIn = authRepository.isLoggedIn()
                _isLoggedIn.value = isLoggedIn
                
                if (isLoggedIn) {
                    val user = authRepository.getCurrentUser()
                    _currentUser.value = user
                    
                    // Load employee info if user has accountId
                    user?.let { loadEmployeeInfo(it) }
                }
            } catch (e: Exception) {
                // If there's an error, clear the state
                _isLoggedIn.value = false
                _currentUser.value = null
            }
        }
    }
    
    suspend fun login(username: String, password: String): AuthResult {
        val repository = authRepository ?: return AuthResult.Error("AuthRepository not initialized")
        val result = repository.login(username, password)
        
        if (result is AuthResult.Success) {
            _isLoggedIn.value = true
            _currentUser.value = result.user
            
            // Load employee info after successful login
            loadEmployeeInfo(result.user)
        }
        
        return result
    }
    
    suspend fun logout() {
        authRepository?.logout()
        _isLoggedIn.value = false
        _currentUser.value = null
        _currentEmployeeInfo.value = null
    }
    
    suspend fun checkAuthStatus() {
        val repository = authRepository ?: return
        val isLoggedIn = repository.isLoggedIn()
        _isLoggedIn.value = isLoggedIn
        
        if (isLoggedIn) {
            val user = repository.getCurrentUser()
            _currentUser.value = user
            
            // Load employee info if user exists
            user?.let { loadEmployeeInfo(it) }
        }
    }
    
    fun clearUserData() {
        _isLoggedIn.value = false
        _currentUser.value = null
        _currentEmployeeInfo.value = null
    }
    
    private suspend fun loadEmployeeInfo(user: User) {
        try {
            // Try to get employee info by accountId
            AppContainer.employeeRepository.getEmployeeById(user.accountId).collect { result ->
                when (result) {
                    is com.example.decalxeandroid.domain.model.Result.Success -> {
                        _currentEmployeeInfo.value = EmployeeInfo.fromEmployee(result.data)
                    }
                    is com.example.decalxeandroid.domain.model.Result.Error -> {
                        // Employee not found, keep employeeInfo as null
                        _currentEmployeeInfo.value = null
                    }
                    else -> {
                        // Loading state, do nothing
                    }
                }
            }
        } catch (e: Exception) {
            // Error loading employee info, keep as null
            _currentEmployeeInfo.value = null
        }
    }
}
