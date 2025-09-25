package com.example.decalxeandroid.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Initial)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    fun register(fullName: String, username: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            
            try {
                val result = registerUseCase(fullName, username, email, password, confirmPassword)
                
                when (result) {
                    is com.example.decalxeandroid.domain.model.AuthResult.Success -> {
                        _uiState.value = RegisterUiState.Success(result.user)
                    }
                    is com.example.decalxeandroid.domain.model.AuthResult.Error -> {
                        _uiState.value = RegisterUiState.Error(result.message)
                    }
                    is com.example.decalxeandroid.domain.model.AuthResult.Loading -> {
                        _uiState.value = RegisterUiState.Loading
                    }
                }
            } catch (e: Exception) {
                _uiState.value = RegisterUiState.Error(e.message ?: "Đăng ký thất bại")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = RegisterUiState.Initial
    }
}

sealed class RegisterUiState {
    object Initial : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val user: com.example.decalxeandroid.domain.model.User) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
