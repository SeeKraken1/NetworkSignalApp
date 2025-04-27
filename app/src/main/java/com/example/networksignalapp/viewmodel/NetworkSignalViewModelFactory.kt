package com.example.networksignalapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory for creating NetworkSignalViewModel with a context parameter
 */
class NetworkSignalViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NetworkSignalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NetworkSignalViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}