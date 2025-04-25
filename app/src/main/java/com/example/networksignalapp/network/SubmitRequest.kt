package com.example.networksignalapp.network

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