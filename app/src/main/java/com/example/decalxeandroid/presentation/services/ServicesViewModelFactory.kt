package com.example.decalxeandroid.presentation.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.decalxeandroid.di.AppContainer

class ServicesViewModelFactory : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServicesViewModel::class.java)) {
            return ServicesViewModel(AppContainer.decalServiceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
