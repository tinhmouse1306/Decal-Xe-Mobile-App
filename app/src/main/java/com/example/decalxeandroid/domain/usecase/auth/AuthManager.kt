package com.example.decalxeandroid.domain.usecase.auth

import com.example.decalxeandroid.domain.model.AuthResult
import com.example.decalxeandroid.domain.model.User
import com.example.decalxeandroid.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthManager(
    private val authRepository: AuthRepository
) {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    suspend fun login(username: String, password: String): AuthResult {
        val result = authRepository.login(username, password)
        
        if (result is AuthResult.Success) {
            _isLoggedIn.value = true
            _currentUser.value = result.user
        }
        
        return result
    }
    
    suspend fun logout() {
        authRepository.logout()
        _isLoggedIn.value = false
        _currentUser.value = null
    }
    
    suspend fun checkAuthStatus() {
        val isLoggedIn = authRepository.isLoggedIn()
        _isLoggedIn.value = isLoggedIn
        
        if (isLoggedIn) {
            val user = authRepository.getCurrentUser()
            _currentUser.value = user
        }
    }
}
