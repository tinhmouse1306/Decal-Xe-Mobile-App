package com.example.decalxeandroid.domain.repository

import com.example.decalxeandroid.domain.model.AuthResult
import com.example.decalxeandroid.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): AuthResult
    suspend fun register(fullName: String, username: String, email: String, password: String, confirmPassword: String): AuthResult
    suspend fun changePassword(oldPassword: String, newPassword: String): AuthResult
    suspend fun resetPassword(username: String): AuthResult
    suspend fun logout()
    suspend fun refreshToken(): AuthResult
    suspend fun getCurrentUser(): User?
    suspend fun isLoggedIn(): Boolean
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
}
