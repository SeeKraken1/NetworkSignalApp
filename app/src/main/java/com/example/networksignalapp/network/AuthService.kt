package com.example.networksignalapp.network

import com.example.networksignalapp.network.model.LoginRequest
import com.example.networksignalapp.network.model.RegisterRequest
import com.example.networksignalapp.network.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface for authentication-related API endpoints
 */
interface AuthService {
    /**
     * Register a new user
     */
    @POST("user")
    suspend fun register(@Body request: RegisterRequest): Response<Map<String, Any>>

    /**
     * Authenticate user and get token
     */
    @POST("authentication")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>
}