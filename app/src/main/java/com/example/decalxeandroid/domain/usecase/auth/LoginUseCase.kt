package com.example.decalxeandroid.domain.usecase.auth

import com.example.decalxeandroid.domain.model.AuthResult
import com.example.decalxeandroid.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
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
            val allowedRoles =
                    listOf(
                            com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN,
                            com.example.decalxeandroid.domain.model.UserRole.SALES,
                            com.example.decalxeandroid.domain.model.UserRole.CUSTOMER
                    )

            if (user.role !in allowedRoles) {
                val roleName =
                        when (user.role) {
                            com.example.decalxeandroid.domain.model.UserRole.ADMIN -> "Admin"
                            com.example.decalxeandroid.domain.model.UserRole.MANAGER -> "Manager"
                            com.example.decalxeandroid.domain.model.UserRole.SALES -> "Sales"
                            com.example.decalxeandroid.domain.model.UserRole.TECHNICIAN ->
                                    "Technician"
                            com.example.decalxeandroid.domain.model.UserRole.CUSTOMER -> "Customer"
                            else -> "Unknown"
                        }
                return AuthResult.Error(
                        "Tài khoản với quyền $roleName không được phép truy cập ứng dụng mobile.\n\nỨng dụng này chỉ dành cho:\n• Nhân viên Bán hàng (Sales)\n• Kỹ thuật viên (Technician)\n• Khách hàng (Customer)\n\nAdmin và Manager vui lòng sử dụng hệ thống web."
                )
            }
        }

        return result
    }
}
