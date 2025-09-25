package com.example.decalxeandroid.domain.usecase.auth

import com.example.decalxeandroid.domain.repository.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.logout()
    }
}
