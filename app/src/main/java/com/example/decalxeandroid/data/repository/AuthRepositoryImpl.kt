package com.example.decalxeandroid.data.repository

import com.example.decalxeandroid.data.dto.*
import com.example.decalxeandroid.data.mapper.AuthMapper
import com.example.decalxeandroid.data.remote.AuthApiService
import com.example.decalxeandroid.data.remote.CustomerApiService
import com.example.decalxeandroid.domain.model.AuthResult
import com.example.decalxeandroid.domain.model.User
import com.example.decalxeandroid.domain.repository.AuthRepository
import com.example.decalxeandroid.data.local.TokenManager
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val authApiService: AuthApiService,
    private val customerApiService: CustomerApiService,
    private val tokenManager: TokenManager
) : AuthRepository {
    
    private var _currentUser: User? = null
    
    override suspend fun login(username: String, password: String): AuthResult {
        return try {
            val loginRequest = LoginRequestDto(username, password)
            val response = authApiService.login(loginRequest)
            
            // Save tokens
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
            
            // Map user data
            val user = AuthMapper.mapUserDataDtoToUser(response.user)
            
            // Save current user
            _currentUser = user
            
            AuthResult.Success(user, response.accessToken, response.refreshToken)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Đăng nhập thất bại")
        }
    }
    
    override suspend fun register(fullName: String, username: String, email: String, password: String, confirmPassword: String): AuthResult {
        return try {
            // Bước 1: Tạo tài khoản (Account)
            val registerRequest = RegisterMobileRequestDto(fullName, username, email, password, confirmPassword)
            val registerResponse = authApiService.register(registerRequest)
            
            // Lấy accountID từ response (giả sử backend trả về accountID)
            val accountID = registerResponse.accountID ?: "temp-account-id"
            
            // Bước 2: Tạo khách hàng (Customer)
            val nameParts = fullName.split(" ")
            val firstName = nameParts.firstOrNull() ?: fullName
            val lastName = nameParts.drop(1).joinToString(" ")
            
            val customerRequest = CustomerRequestDto(
                firstName = firstName,
                lastName = lastName,
                phoneNumber = "", // Có thể thêm sau
                email = email,
                address = "", // Có thể thêm sau
                accountID = accountID
            )
            
            customerApiService.createCustomer(customerRequest)
            
            // Trả về user đã tạo
            AuthResult.Success(
                User(
                    accountId = accountID,
                    username = username,
                    email = email,
                    fullName = fullName,
                    phoneNumber = null,
                    role = com.example.decalxeandroid.domain.model.UserRole.CUSTOMER,
                    isActive = true,
                    createdAt = "",
                    lastLoginAt = null
                ), 
                "", 
                ""
            )
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Đăng ký thất bại")
        }
    }
    
    override suspend fun changePassword(oldPassword: String, newPassword: String): AuthResult {
        return try {
            val changePasswordRequest = ChangePasswordRequestDto(oldPassword, newPassword, newPassword) // Use newPassword as confirmPassword
            authApiService.changePassword(changePasswordRequest)
            AuthResult.Success(User("", "", "", "", null, com.example.decalxeandroid.domain.model.UserRole.CUSTOMER, true, "", null), "", "")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Đổi mật khẩu thất bại")
        }
    }
    
    override suspend fun resetPassword(username: String): AuthResult {
        return try {
            val resetPasswordRequest = ResetPasswordRequestDto(username, "", "") // Empty strings for newPassword and confirmPassword
            authApiService.resetPassword(resetPasswordRequest)
            AuthResult.Success(User("", "", "", "", null, com.example.decalxeandroid.domain.model.UserRole.CUSTOMER, true, "", null), "", "")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Đặt lại mật khẩu thất bại")
        }
    }
    
    override suspend fun logout() {
        tokenManager.clearTokens()
        _currentUser = null
    }
    
    override suspend fun refreshToken(): AuthResult {
        return try {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken == null) {
                return AuthResult.Error("Không tìm thấy refresh token")
            }
            
            val refreshTokenRequest = RefreshTokenRequestDto(refreshToken)
            val response = authApiService.refreshToken(refreshTokenRequest)
            
            // Save new tokens
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
            
            // Map user data
            val user = AuthMapper.mapUserDataDtoToUser(response.user)
            
            AuthResult.Success(user, response.accessToken, response.refreshToken)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Làm mới token thất bại")
        }
    }
    
    override suspend fun getCurrentUser(): User? {
        // Check if user is logged in
        if (!isLoggedIn()) {
            return null
        }
        
        // Return the stored current user
        return _currentUser
    }
    
    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.getAccessToken() != null
    }
    
    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenManager.saveTokens(accessToken, refreshToken)
    }
    
    override suspend fun getAccessToken(): String? {
        return tokenManager.getAccessToken()
    }
    
    override suspend fun getRefreshToken(): String? {
        return tokenManager.getRefreshToken()
    }
    
    override suspend fun clearTokens() {
        tokenManager.clearTokens()
    }
}
