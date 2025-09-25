package com.example.decalxeandroid.domain.usecase.auth

import com.example.decalxeandroid.domain.model.AuthResult
import com.example.decalxeandroid.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): AuthResult {
        if (username.isBlank()) {
            return AuthResult.Error("Username không được để trống")
        }
        if (password.isBlank()) {
            return AuthResult.Error("Mật khẩu không được để trống")
        }
        
        val result = authRepository.login(username, password)
        
        // Kiểm tra role sau khi login thành công
        if (result is AuthResult.Success) {
            val user = result.user
            val allowedRoles = listOf(
                com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN,
                com.example.decalxeandroid.domain.model.UserRole.SALES,
                com.example.decalxeandroid.domain.model.UserRole.CUSTOMER
            )
            
            if (user.role !in allowedRoles) {
                val roleName = when (user.role) {
                    com.example.decalxeandroid.domain.model.UserRole.ADMIN -> "Admin"
                    com.example.decalxeandroid.domain.model.UserRole.MANAGER -> "Manager"
                    com.example.decalxeandroid.domain.model.UserRole.SALES -> "Sales"
                    com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN -> "Technician"
                    com.example.decalxeandroid.domain.model.UserRole.CUSTOMER -> "Customer"
                }
                return AuthResult.Error("Tài khoản $roleName không có quyền truy cập ứng dụng mobile. Ứng dụng này chỉ dành cho Nhân viên Bán hàng (Sales), Kỹ thuật viên Lắp đặt (Technician) và Khách hàng (Customer). Admin và Manager sử dụng hệ thống quản lý trên web.")
            }
        }
        
        return result
    }
}
