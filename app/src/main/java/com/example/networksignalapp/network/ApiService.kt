package com.example.networksignalapp.network

import com.example.networksignalapp.network.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Interface for all API endpoints for the network signal backend
 */
interface ApiService {
    /**
     * Authenticate user
     */
    @POST("authentication")
    suspend fun login(@Body request: LoginRequest): Response<TokenResponse>

    /**
     * Register a new user
     */
    @POST("user")
    suspend fun register(@Body request: RegisterRequest): Response<Map<String, Any>>

    /**
     * Send cell network data to the server
     */
    @POST("cellData")
    suspend fun submitCellData(
        @Header("Authorization") token: String,
        @Body data: CellDataRequest
    ): Response<Map<String, Any>>

    /**
     * Get statistics for a specific date range
     */
    @POST("statistics")
    suspend fun getStatistics(
        @Header("Authorization") token: String,
        @Body dateRange: DateRangeRequest
    ): Response<StatisticsResponse>

    /**
     * Get centralized statistics of connected devices
     */
    @GET("centralized-statistics")
    suspend fun getCentralizedStatistics(): Response<CentralizedStatisticsResponse>
}