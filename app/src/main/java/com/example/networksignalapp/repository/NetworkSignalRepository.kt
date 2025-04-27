package com.example.networksignalapp.repository

import android.content.Context
import com.example.networksignalapp.R
import com.example.networksignalapp.model.DeviceData
import com.example.networksignalapp.model.NetworkSignalData
import com.example.networksignalapp.model.NetworkStatisticsData
import com.example.networksignalapp.model.SignalHistoryData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Repository providing network signal data and related information.
 * Currently using simulated data for demonstration purposes.
 */
class NetworkSignalRepository(private val context: Context) {

    /**
     * Get current network signal data
     */
    fun getNetworkSignalData(): Flow<NetworkSignalData> = flowOf(
        NetworkSignalData(
            signalStrength = "${Random.nextInt(-110, -60)}dBm",
            networkType = listOf("4G", "5G", "3G").random(),
            operator = listOf("T-Mobile", "Verizon", "AT&T").random(),
            signalPower = "${Random.nextInt(-105, -55)}dBm",
            sinrSnr = "${Random.nextInt(5, 25)}dB",
            frequencyBand = "Band ${Random.nextInt(1, 80)}",
            cellId = Random.nextInt(1000000, 9999999).toString(),
            timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            downloadSpeed = "${Random.nextDouble(5.0, 25.0).round(1)} Mbps",
            uploadSpeed = "${Random.nextDouble(2.0, 15.0).round(1)} Mbps",
            ping = "${Random.nextInt(20, 150)} ms",
            jitter = "${Random.nextInt(2, 20)} ms",
            packetLoss = "${Random.nextDouble(0.0, 3.0).round(1)}%"
        )
    )

    /**
     * Get signal history data for charting
     */
    fun getSignalHistory(): List<SignalHistoryData> {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = Calendar.getInstance()

        return List(24) { index ->
            calendar.add(Calendar.MINUTE, -10)
            val time = timeFormat.format(calendar.time)
            // Simulate varying signal strength between -120 and -60 dBm
            val signalStrength = -90f + Random.nextFloat() * 30f - Random.nextFloat() * 30f
            SignalHistoryData(time, signalStrength)
        }.reversed()
    }

    /**
     * Get list of connected devices
     */
    fun getConnectedDevices(): List<DeviceData> {
        return listOf(
            DeviceData(1, "iPhone 12 Pro", "192.168.1.100", "00:1A:2B:3C:4D:5E", R.drawable.ic_smartphone),
            DeviceData(2, "MacBook Pro", "192.168.1.101", "00:1A:2B:3C:4D:5F", R.drawable.ic_laptop),
            DeviceData(3, "Desktop PC", "192.168.1.102", "00:1A:2B:3C:4D:60", R.drawable.ic_desktop),
            DeviceData(4, "WiFi Router", "192.168.1.103", "00:1A:2B:3C:4D:61", R.drawable.ic_wifi),
            DeviceData(5, "Smart Speaker", "192.168.1.104", "00:1A:2B:3C:4D:62", R.drawable.ic_speaker)
        )
    }

    /**
     * Get network statistics data
     */
    fun getNetworkStatistics(): NetworkStatisticsData {
        return NetworkStatisticsData(
            averageConnectivity = "98%",
            timeInNetworkType = mapOf(
                "4G" to 40f,
                "3G" to 30f,
                "2G" to 30f
            ),
            operatorTime = mapOf(
                "Verizon" to 1.2f,
                "T-Mobile" to 1.5f,
                "AT&T" to 0.8f
            ),
            signalPowerByType = mapOf(
                "4G" to -65f,
                "3G" to -85f,
                "2G" to -100f
            ),
            snrByType = mapOf(
                "4G" to 12f,
                "3G" to 8f,
                "2G" to 5f
            )
        )
    }

    /**
     * Submit cell data to server
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
     * Helper extension function to round doubles for display
     */
    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return kotlin.math.round(this * multiplier) / multiplier
    }
}