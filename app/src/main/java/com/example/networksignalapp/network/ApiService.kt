package com.example.networksignalapp.network

import retrofit2.Call
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

/**
 * Request models
 */
data class LoginRequest(val user_name: String, val password: String)

data class RegisterRequest(val user_name: String, val password: String)

data class CellDataRequest(
    val operator: String,
    val signalPower: Float,
    val sinr_snr: Float,
    val networkType: String,
    val frequency_band: String,
    val cell_id: String,
    val timestamp: String,
    val user_ip: String,
    val user_mac: String
)

data class DateRangeRequest(
    val start_date: String,
    val end_date: String
)

/**
 * Response models
 */
data class TokenResponse(val token: String)

data class StatisticsResponse(
    val operators: Map<String, Float>,
    val network_types: Map<String, Float>,
    val signal_powers: Map<String, Float>,
    val signal_power_avg_device: Float,
    val sinr_snr: Map<String, Float>
)

data class DeviceInfo(
    val user_ip: String,
    val user_mac: String
)

data class CentralizedStatisticsResponse(
    val connected_devices: Int,
    val previous_devices: Map<String, DeviceInfo>,
    val current_devices: Map<String, DeviceInfo>,
    val device_statistics: StatisticsResponse
)