package com.example.networksignalapp.repository

import com.example.networksignalapp.R
import com.example.networksignalapp.model.DeviceData
import com.example.networksignalapp.model.NetworkSignalData
import com.example.networksignalapp.model.NetworkStatisticsData
import com.example.networksignalapp.model.SignalHistoryData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/**
 * Repository class to provide network signal data.
 * In a real app, this would fetch data from the system APIs or a remote source.
 */
class NetworkSignalRepository {

    /**
     * Emits current network signal data every second with slight variations
     */
    fun getNetworkSignalData(): Flow<NetworkSignalData> = flow {
        val baseData = NetworkSignalData()

        while (true) {
            // Simulate small variations in signal strength
            val signalVariation = Random.nextInt(-3, 4)
            val baseSignalValue = -106
            val newSignalStrength = "${baseSignalValue + signalVariation}dBm"

            emit(baseData.copy(signalStrength = newSignalStrength))
            delay(1000)
        }
    }

    /**
     * Provides historical signal data for charts
     */
    fun getSignalHistory(): List<SignalHistoryData> = listOf(
        SignalHistoryData("1/1", -110f),
        SignalHistoryData("1/8", -102f),
        SignalHistoryData("1/15", -105f),
        SignalHistoryData("1/22", -98f),
        SignalHistoryData("1/30", -106f)
    )

    /**
     * Provides connected devices data for the server tab
     */
    fun getConnectedDevices(): List<DeviceData> = listOf(
        DeviceData(1, "iPhone 12 Pro", "192.168.1.100", "00:1A:2B:3C:4D:5E", R.drawable.ic_smartphone),
        DeviceData(2, "MacBook Pro", "192.168.1.101", "00:1A:2B:3C:4D:5F", R.drawable.ic_laptop),
        DeviceData(3, "Desktop PC", "192.168.1.102", "00:1A:2B:3C:4D:60", R.drawable.ic_desktop),
        DeviceData(4, "WiFi Router", "192.168.1.103", "00:1A:2B:3C:4D:61", R.drawable.ic_wifi),
        DeviceData(5, "Smart Speaker", "192.168.1.104", "00:1A:2B:3C:4D:62", R.drawable.ic_speaker),
        DeviceData(6, "Samsung Galaxy S21", "192.168.1.105", "00:1A:2B:3C:4D:63", R.drawable.ic_smartphone),
        DeviceData(7, "iPad Pro", "192.168.1.106", "00:1A:2B:3C:4D:64", R.drawable.ic_laptop),
        DeviceData(8, "Smart TV", "192.168.1.107", "00:1A:2B:3C:4D:65", R.drawable.ic_wifi),
        DeviceData(9, "Network Printer", "192.168.1.108", "00:1A:2B:3C:4D:66", R.drawable.ic_desktop)
    )

    /**
     * Provides server information
     */
    fun getServerInfo(): ServerInfo {
        return ServerInfo(
            status = "Online",
            uptime = "3d 14h 22m",
            ipAddress = "192.168.1.1",
            totalDataUsage = 32.4f, // GB
            bandwidth = 58.2f, // Mbps
            maxClients = 10,
            connectedClients = 9
        )
    }

    /**
     * Provides network statistics data
     */
    fun getNetworkStatistics(): NetworkStatisticsData = NetworkStatisticsData()

    /**
     * Run a simulated speed test
     */
    fun runSpeedTest(): Flow<SpeedTestResult> = flow {
        // Initial state
        emit(SpeedTestResult(0f, 0f, 0, 0f, 0f, SpeedTestStatus.STARTING))
        delay(500)

        // Testing download
        emit(SpeedTestResult(0f, 0f, 0, 0f, 0f, SpeedTestStatus.TESTING_DOWNLOAD))

        // Simulate download test progress
        for (i in 1..10) {
            val progress = i * 10
            val currentSpeed = Random.nextFloat() * 15f + 5f // 5-20 Mbps
            emit(SpeedTestResult(currentSpeed, 0f, 0, 0f, 0f, SpeedTestStatus.TESTING_DOWNLOAD, progress))
            delay(300)
        }

        // Final download result
        val downloadSpeed = Random.nextFloat() * 10f + 10f // 10-20 Mbps
        emit(SpeedTestResult(downloadSpeed, 0f, 0, 0f, 0f, SpeedTestStatus.TESTING_UPLOAD))

        // Simulate upload test progress
        for (i in 1..10) {
            val progress = i * 10
            val currentSpeed = Random.nextFloat() * 8f + 2f // 2-10 Mbps
            emit(SpeedTestResult(downloadSpeed, currentSpeed, 0, 0f, 0f, SpeedTestStatus.TESTING_UPLOAD, progress))
            delay(300)
        }

        // Final upload result
        val uploadSpeed = Random.nextFloat() * 5f + 5f // 5-10 Mbps
        emit(SpeedTestResult(downloadSpeed, uploadSpeed, 0, 0f, 0f, SpeedTestStatus.TESTING_PING))
        delay(1000)

        // Final ping result
        val ping = Random.nextInt(50, 150)
        emit(SpeedTestResult(downloadSpeed, uploadSpeed, ping, 0f, 0f, SpeedTestStatus.TESTING_JITTER))
        delay(500)

        // Final jitter result
        val jitter = Random.nextFloat() * 10f + 5f
        emit(SpeedTestResult(downloadSpeed, uploadSpeed, ping, jitter, 0f, SpeedTestStatus.TESTING_PACKET_LOSS))
        delay(500)

        // Complete result
        val packetLoss = Random.nextFloat() * 2f
        emit(SpeedTestResult(downloadSpeed, uploadSpeed, ping, jitter, packetLoss, SpeedTestStatus.COMPLETE))
    }
}

/**
 * Server information data class
 */
data class ServerInfo(
    val status: String,
    val uptime: String,
    val ipAddress: String,
    val totalDataUsage: Float, // GB
    val bandwidth: Float, // Mbps
    val maxClients: Int,
    val connectedClients: Int
)

enum class SpeedTestStatus {
    STARTING,
    TESTING_DOWNLOAD,
    TESTING_UPLOAD,
    TESTING_PING,
    TESTING_JITTER,
    TESTING_PACKET_LOSS,
    COMPLETE
}

data class SpeedTestResult(
    val downloadSpeed: Float, // Mbps
    val uploadSpeed: Float, // Mbps
    val ping: Int, // ms
    val jitter: Float, // ms
    val packetLoss: Float, // percentage
    val status: SpeedTestStatus,
    val progressPercentage: Int = 0
)