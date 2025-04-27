package com.example.networksignalapp.network.model

/**
 * Common network data models for API requests and responses
 */

data class LoginRequest(val user_name: String, val password: String)

data class RegisterRequest(val user_name: String, val password: String)

data class TokenResponse(val token: String)

/**
 * Models for cell data requests
 */
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

/**
 * Models for statistics requests
 */
data class DateRangeRequest(
    val start_date: String,
    val end_date: String
)

/**
 * Response models
 */
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

data class SubmitRequest(
    val timestamp: String,
    val operator: String,
    val signal_power: Int,
    val snr: Float,
    val network_type: String,
    val band: String,
    val cell_id: String,
    val device_ip: String,
    val device_mac: String
)