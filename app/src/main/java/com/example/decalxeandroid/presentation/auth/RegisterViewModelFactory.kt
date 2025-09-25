package com.example.decalxeandroid.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.domain.usecase.auth.RegisterUseCase
import com.example.decalxeandroid.di.AppContainer

class RegisterViewModelFactory : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            val registerUseCase = RegisterUseCase(AppContainer.authRepository)
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(registerUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
