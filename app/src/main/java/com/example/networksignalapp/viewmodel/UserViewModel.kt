package com.example.networksignalapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.networksignalapp.network.AuthService
import com.example.networksignalapp.network.LoginRequest
import com.example.networksignalapp.network.RegisterRequest
import com.example.networksignalapp.network.RetrofitClient
import com.example.networksignalapp.network.UserSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AuthState {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}

class UserViewModel(private val context: Context) : ViewModel() {

    private val sessionManager = UserSessionManager(context)
    private val authService = RetrofitClient.create(AuthService::class.java)

    // Authentication state
    private val _authState = MutableStateFlow(AuthState.IDLE)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Login form state
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    // Is user logged in
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        // Check if user is already logged in
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        val token = sessionManager.getToken()
        _isLoggedIn.value = !token.isNullOrEmpty()
    }

    fun updateUsername(username: String) {
        _username.value = username
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun login() {
        if (_username.value.isBlank() || _password.value.isBlank()) {
            _errorMessage.value = "Username and password cannot be empty"
            return
        }

        _authState.value = AuthState.LOADING

        viewModelScope.launch {
            try {
                val response = authService.login(
                    LoginRequest(
                        user_name = _username.value,
                        password = _password.value
                    )
                )

                if (response.isSuccessful && response.body() != null) {
                    // Save token
                    sessionManager.saveToken(response.body()!!.token)
                    _isLoggedIn.value = true
                    _authState.value = AuthState.SUCCESS
                    clearForm()
                } else {
                    _errorMessage.value = "Login failed: ${response.message()}"
                    _authState.value = AuthState.ERROR
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _authState.value = AuthState.ERROR
            }
        }
    }

    fun register() {
        if (_username.value.isBlank() || _password.value.isBlank()) {
            _errorMessage.value = "Username and password cannot be empty"
            return
        }

        _authState.value = AuthState.LOADING

        viewModelScope.launch {
            try {
                val response = authService.register(
                    RegisterRequest(
                        user_name = _username.value,
                        password = _password.value
                    )
                )

                if (response.isSuccessful) {
                    _authState.value = AuthState.SUCCESS
                    clearForm()
                } else {
                    _errorMessage.value = "Registration failed: ${response.message()}"
                    _authState.value = AuthState.ERROR
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                _authState.value = AuthState.ERROR
            }
        }
    }

    fun logout() {
        sessionManager.logout()
        _isLoggedIn.value = false
        _authState.value = AuthState.IDLE
        clearForm()
    }

    private fun clearForm() {
        _username.value = ""
        _password.value = ""
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
        _authState.value = AuthState.IDLE
    }

    /**
     * Factory for creating UserViewModel with context parameter
     */
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}