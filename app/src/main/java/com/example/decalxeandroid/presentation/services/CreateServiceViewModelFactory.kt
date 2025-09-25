package com.example.decalxeandroid.presentation.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.di.AppContainer

class CreateServiceViewModelFactory : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateServiceViewModel::class.java)) {
            return CreateServiceViewModel(AppContainer.decalServiceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
