package com.example.decalxeandroid.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.di.AppContainer

class LoginViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // Initialize GlobalAuthManager before creating LoginViewModel
            kotlinx.coroutines.runBlocking {
                com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager.initialize(
                        AppContainer.authRepository
                )
            }

            @Suppress("UNCHECKED_CAST") return LoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
