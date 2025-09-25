package com.example.decalxeandroid.domain.model

sealed class AuthResult {
    object Loading : AuthResult()
    data class Success(val user: User, val accessToken: String, val refreshToken: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}
