package com.example.decalxeandroid.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.usecase.auth.GlobalAuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            try {
                // Use GlobalAuthManager instead of direct LoginUseCase
                val result = GlobalAuthManager.login(username, password)

                when (result) {
                    is com.example.decalxeandroid.domain.model.AuthResult.Success -> {
                        _uiState.value = LoginUiState.Success(result.user)
                    }
                    is com.example.decalxeandroid.domain.model.AuthResult.Error -> {
                        _uiState.value = LoginUiState.Error(result.message)
                    }
                    is com.example.decalxeandroid.domain.model.AuthResult.Loading -> {
                        _uiState.value = LoginUiState.Loading
                    }
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Đăng nhập thất bại")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Initial
    }
}

sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val user: com.example.decalxeandroid.domain.model.User) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
