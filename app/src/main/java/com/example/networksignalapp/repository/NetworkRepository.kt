package com.example.networksignalapp.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Repository for handling network-related data including signal strength and cellular information.
 */
class NetworkRepository(private val context: Context) {

    /**
     * Submit cellular data to server
     */
    fun submitCellData(
        operator: String,
        signalPower: Float,
        sinrSnr: Float,
        networkType: String,
        frequencyBand: String,
        cellId: String
    ): Flow<Result<Boolean>> = flow {
        // Simulate network call delay
        kotlinx.coroutines.delay(1000)

        // Simulate success with 90% probability
        if (Random.nextFloat() < 0.9f) {
            emit(Result.success(true))
        } else {
            emit(Result.failure(Exception("Network error")))
        }
    }

    /**
     * Get the current timestamp in a formatted string
     */
    fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }

    /**
     * Generates a device IP address (simulated)
     */
    fun getDeviceIP(): String {
        return "192.168.1.${(1..254).random()}"
    }

    /**
     * Generates a device MAC address (simulated)
     */
    fun getDeviceMAC(): String {
        val random = java.util.Random()
        val bytes = ByteArray(6)
        random.nextBytes(bytes)
        return bytes.joinToString(":") { String.format("%02X", it) }
    }
}