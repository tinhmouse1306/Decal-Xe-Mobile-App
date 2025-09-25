package com.example.decalxeandroid.domain.usecase.auth

import com.example.decalxeandroid.domain.model.AuthResult
import com.example.decalxeandroid.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fullName: String, username: String, email: String, password: String, confirmPassword: String
    ): AuthResult {
        if (fullName.isBlank()) {
            return AuthResult.Error("Họ và tên không được để trống")
        }
        if (username.isBlank()) {
            return AuthResult.Error("Username không được để trống")
        }
        if (email.isBlank()) {
            return AuthResult.Error("Email không được để trống")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AuthResult.Error("Email không hợp lệ")
        }
        if (password.length < 6) {
            return AuthResult.Error("Mật khẩu phải có ít nhất 6 ký tự")
        }
        if (password != confirmPassword) {
            return AuthResult.Error("Xác nhận mật khẩu không khớp")
        }
        
        return authRepository.register(fullName, username, email, password, confirmPassword)
    }
}
