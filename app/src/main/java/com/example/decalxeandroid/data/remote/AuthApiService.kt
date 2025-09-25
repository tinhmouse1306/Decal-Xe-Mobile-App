package com.example.decalxeandroid.data.remote

import com.example.decalxeandroid.data.dto.*
import retrofit2.http.*

interface AuthApiService {
    @POST(ApiConstants.LOGIN_ENDPOINT)
    suspend fun login(@Body loginRequest: LoginRequestDto): LoginResponseDto
    
    @POST(ApiConstants.REGISTER_ENDPOINT)
    suspend fun register(@Body registerRequest: RegisterMobileRequestDto): RegisterResponseDto
    
    @PUT(ApiConstants.CHANGE_PASSWORD_ENDPOINT)
    suspend fun changePassword(@Body changePasswordRequest: ChangePasswordRequestDto): String
    
    @POST(ApiConstants.RESET_PASSWORD_ENDPOINT)
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequestDto): String
    
    @POST(ApiConstants.REFRESH_TOKEN_ENDPOINT)
    suspend fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequestDto): LoginResponseDto
}
