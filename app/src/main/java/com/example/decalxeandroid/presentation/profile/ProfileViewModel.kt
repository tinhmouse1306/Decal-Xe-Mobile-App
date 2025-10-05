package com.example.decalxeandroid.presentation.profile

// import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.decalxeandroid.domain.model.Employee
import com.example.decalxeandroid.domain.model.Result
import com.example.decalxeandroid.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// import javax.inject.Inject

class ProfileViewModel : ViewModel() {
    // Use AppContainer to get repository
    private val employeeRepository: EmployeeRepository =
            com.example.decalxeandroid.di.AppContainer.employeeRepository

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadEmployeeByAccountId(accountId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // First, get all employees to find the one with matching accountId
                employeeRepository.getEmployees(1, 100).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val employee = result.data.find { it.accountId == accountId }
                            _uiState.value =
                                    _uiState.value.copy(
                                            isLoading = false,
                                            currentEmployee = employee,
                                            error =
                                                    if (employee == null)
                                                            "Không tìm thấy thông tin nhân viên"
                                                    else null
                                    )
                        }
                        is Result.Error -> {
                            _uiState.value =
                                    _uiState.value.copy(isLoading = false, error = result.message)
                        }
                        else -> {
                            _uiState.value =
                                    _uiState.value.copy(
                                            isLoading = false,
                                            error = "Unknown error loading employee data"
                                    )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value =
                        _uiState.value.copy(
                                isLoading = false,
                                error = e.message ?: "Có lỗi xảy ra khi tải thông tin nhân viên"
                        )
            }
        }
    }

    fun loadEmployees() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                employeeRepository.getEmployees(1, 100).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _uiState.value =
                                    _uiState.value.copy(isLoading = false, employees = result.data)
                        }
                        is Result.Error -> {
                            _uiState.value =
                                    _uiState.value.copy(isLoading = false, error = result.message)
                        }
                        else -> {
                            _uiState.value =
                                    _uiState.value.copy(
                                            isLoading = false,
                                            error = "Unknown error loading employees"
                                    )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value =
                        _uiState.value.copy(
                                isLoading = false,
                                error = e.message ?: "Có lỗi xảy ra khi tải danh sách nhân viên"
                        )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ProfileUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val employees: List<Employee> = emptyList(),
        val currentEmployee: Employee? = null
)
