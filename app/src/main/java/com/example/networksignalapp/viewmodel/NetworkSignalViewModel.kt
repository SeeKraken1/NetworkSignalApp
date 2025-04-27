package com.example.networksignalapp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networksignalapp.model.DeviceData
import com.example.networksignalapp.model.NetworkSignalData
import com.example.networksignalapp.model.NetworkStatisticsData
import com.example.networksignalapp.model.SignalHistoryData
import com.example.networksignalapp.ui.components.SpeedTestResult
import com.example.networksignalapp.ui.components.SpeedTestStatus
import com.example.networksignalapp.ui.screens.exportToCsv
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class NetworkSignalViewModel(
    private val appContext: Context? = null
) : ViewModel() {

    private val prefs: SharedPreferences? = appContext?.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    // Network signal data
    private val _networkSignalData = MutableStateFlow(NetworkSignalData())
    val networkSignalData: StateFlow<NetworkSignalData> = _networkSignalData.asStateFlow()

    // Signal history data
    private val _signalHistory = MutableStateFlow<List<SignalHistoryData>>(emptyList())
    val signalHistory: StateFlow<List<SignalHistoryData>> = _signalHistory.asStateFlow()

    // Connected devices
    private val _connectedDevices = MutableStateFlow<List<DeviceData>>(emptyList())
    val connectedDevices: StateFlow<List<DeviceData>> = _connectedDevices.asStateFlow()

    // Network statistics
    private val _networkStatistics = MutableStateFlow(NetworkStatisticsData())
    val networkStatistics: StateFlow<NetworkStatisticsData> = _networkStatistics.asStateFlow()

    // Speed test results
    private val _speedTestResult = MutableStateFlow<SpeedTestResult?>(null)
    val speedTestResult: StateFlow<SpeedTestResult?> = _speedTestResult.asStateFlow()

    // Is speed test running
    private val _isSpeedTestRunning = MutableStateFlow(false)
    val isSpeedTestRunning: StateFlow<Boolean> = _isSpeedTestRunning.asStateFlow()

    // Theme preference
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Signal history recording state
    private val _isRecordingHistory = MutableStateFlow(false)
    val isRecordingHistory: StateFlow<Boolean> = _isRecordingHistory.asStateFlow()

    // Real signal data (from TelephonyManager)
    private val _realSignalStrength = MutableStateFlow<Int>(-120)
    val realSignalStrength: StateFlow<Int> = _realSignalStrength.asStateFlow()

    // Signal quality categories
    private val _signalQuality = MutableStateFlow("Poor")
    val signalQuality: StateFlow<String> = _signalQuality.asStateFlow()

    init {
        // Load initial data
        loadSignalHistory()
        loadConnectedDevices()
        loadNetworkStatistics()
        loadThemePreference()

        // Generate initial mock data
        generateMockNetworkData()
    }

    /**
     * Generate mock network data for development
     */
    private fun generateMockNetworkData() {
        _networkSignalData.value = NetworkSignalData(
            signalStrength = "${_realSignalStrength.value}dBm",
            networkType = "4G",
            operator = "T-Mobile",
            signalPower = "${_realSignalStrength.value}dBm",
            sinrSnr = "20dB",
            frequencyBand = "Band 66",
            cellId = "1234567",
            timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            downloadSpeed = "12.4 Mbps",
            uploadSpeed = "8.3 Mbps",
            ping = "109 ms",
            jitter = "9 ms",
            packetLoss = "0%"
        )
    }

    fun loadSignalHistory() {
        _signalHistory.value = generateMockSignalHistory()
    }

    fun loadConnectedDevices() {
        _connectedDevices.value = generateMockDevices()
    }

    fun loadNetworkStatistics() {
        _networkStatistics.value = NetworkStatisticsData(
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

    private fun loadThemePreference() {
        val isDark = prefs?.getBoolean("dark_theme", true) ?: true
        _isDarkTheme.value = isDark
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value

        // Save preference if context is available
        prefs?.edit()?.putBoolean("dark_theme", _isDarkTheme.value)?.apply()
    }

    fun updateSignalStrength(dbm: Int) {
        _realSignalStrength.value = dbm

        // Update signal quality based on dBm value
        _signalQuality.value = when {
            dbm > -80 -> "Excellent"
            dbm > -90 -> "Good"
            dbm > -100 -> "Fair"
            else -> "Poor"
        }

        // Update the network signal data with real values
        _networkSignalData.value = _networkSignalData.value.copy(
            signalStrength = "$dbm dBm",
            signalPower = "$dbm dBm"
        )

        // Record signal history if enabled
        if (_isRecordingHistory.value) {
            recordSignalHistory(dbm)
        }
    }

    private fun recordSignalHistory(dbm: Int) {
        val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val newHistoryPoint = SignalHistoryData(currentTime, dbm.toFloat())

        // Limit history size to most recent 100 points
        val updatedHistory = (_signalHistory.value + newHistoryPoint).takeLast(100)
        _signalHistory.value = updatedHistory
    }

    fun toggleHistoryRecording() {
        _isRecordingHistory.value = !_isRecordingHistory.value
    }

    fun runSpeedTest() {
        if (_isSpeedTestRunning.value) return

        _isSpeedTestRunning.value = true
        _speedTestResult.value = SpeedTestResult(status = SpeedTestStatus.STARTING)

        viewModelScope.launch {
            // Simulated speed test process
            simulateSpeedTest()
        }
    }

    private suspend fun simulateSpeedTest() {
        try {
            // Initial state
            _speedTestResult.value = SpeedTestResult(status = SpeedTestStatus.STARTING)
            kotlinx.coroutines.delay(500)

            // Testing download
            _speedTestResult.value = SpeedTestResult(status = SpeedTestStatus.TESTING_DOWNLOAD)

            // Simulate download test progress
            for (i in 1..10) {
                val progress = i * 10
                val currentSpeed = Random.nextFloat() * 15f + 5f // 5-20 Mbps
                _speedTestResult.value = SpeedTestResult(
                    downloadSpeed = currentSpeed,
                    status = SpeedTestStatus.TESTING_DOWNLOAD,
                    progressPercentage = progress
                )
                kotlinx.coroutines.delay(300)
            }

            // Final download result
            val downloadSpeed = Random.nextFloat() * 10f + 10f // 10-20 Mbps
            _speedTestResult.value = SpeedTestResult(
                downloadSpeed = downloadSpeed,
                status = SpeedTestStatus.TESTING_UPLOAD
            )

            // Simulate upload test progress
            for (i in 1..10) {
                val progress = i * 10
                val currentSpeed = Random.nextFloat() * 8f + 2f // 2-10 Mbps
                _speedTestResult.value = _speedTestResult.value?.copy(
                    uploadSpeed = currentSpeed,
                    status = SpeedTestStatus.TESTING_UPLOAD,
                    progressPercentage = progress
                )
                kotlinx.coroutines.delay(300)
            }

            // Final upload result
            val uploadSpeed = Random.nextFloat() * 5f + 5f // 5-10 Mbps
            _speedTestResult.value = _speedTestResult.value?.copy(
                uploadSpeed = uploadSpeed,
                status = SpeedTestStatus.TESTING_PING
            )
            kotlinx.coroutines.delay(1000)

            // Final ping result
            val ping = Random.nextInt(50, 150)
            _speedTestResult.value = _speedTestResult.value?.copy(
                ping = ping,
                status = SpeedTestStatus.TESTING_JITTER
            )
            kotlinx.coroutines.delay(500)

            // Final jitter result
            val jitter = Random.nextFloat() * 10f + 5f
            _speedTestResult.value = _speedTestResult.value?.copy(
                jitter = jitter,
                status = SpeedTestStatus.TESTING_PACKET_LOSS
            )
            kotlinx.coroutines.delay(500)

            // Complete result
            val packetLoss = Random.nextFloat() * 2f
            _speedTestResult.value = _speedTestResult.value?.copy(
                packetLoss = packetLoss,
                status = SpeedTestStatus.COMPLETE
            )

            // Update network data with speed test results
            _networkSignalData.value = _networkSignalData.value.copy(
                downloadSpeed = "${_speedTestResult.value?.downloadSpeed} Mbps",
                uploadSpeed = "${_speedTestResult.value?.uploadSpeed} Mbps",
                ping = "${_speedTestResult.value?.ping} ms",
                jitter = "${_speedTestResult.value?.jitter} ms",
                packetLoss = "${_speedTestResult.value?.packetLoss}%"
            )

            // Speed test completed
            _isSpeedTestRunning.value = false

        } catch (e: Exception) {
            // Handle any errors
            _speedTestResult.value = SpeedTestResult(status = SpeedTestStatus.COMPLETE)
            _isSpeedTestRunning.value = false
        }
    }

    fun refreshData() {
        loadConnectedDevices()
        loadNetworkStatistics()
        loadSignalHistory()
        generateMockNetworkData()
    }

    // Added mock data generator functions to replace repository calls

    private fun generateMockSignalHistory(): List<SignalHistoryData> {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val calendar = java.util.Calendar.getInstance()

        return List(24) { index ->
            calendar.add(java.util.Calendar.MINUTE, -10)
            val time = timeFormat.format(calendar.time)
            // Simulate varying signal strength between -120 and -60 dBm
            val signalStrength = -90f + Random.nextFloat() * 30f - Random.nextFloat() * 30f
            SignalHistoryData(time, signalStrength)
        }.reversed()
    }

    private fun generateMockDevices(): List<DeviceData> {
        return listOf(
            DeviceData(1, "iPhone 12 Pro", "192.168.1.100", "00:1A:2B:3C:4D:5E", com.example.networksignalapp.R.drawable.ic_smartphone),
            DeviceData(2, "MacBook Pro", "192.168.1.101", "00:1A:2B:3C:4D:5F", com.example.networksignalapp.R.drawable.ic_laptop),
            DeviceData(3, "Desktop PC", "192.168.1.102", "00:1A:2B:3C:4D:60", com.example.networksignalapp.R.drawable.ic_desktop),
            DeviceData(4, "WiFi Router", "192.168.1.103", "00:1A:2B:3C:4D:61", com.example.networksignalapp.R.drawable.ic_wifi),
            DeviceData(5, "Smart Speaker", "192.168.1.104", "00:1A:2B:3C:4D:62", com.example.networksignalapp.R.drawable.ic_speaker)
        )
    }

    // Filter devices by type
    fun getDevicesByType(type: String?): List<DeviceData> {
        return _connectedDevices.value.filter { device ->
            when (type) {
                "smartphone" -> device.name.contains("phone", ignoreCase = true)
                "laptop" -> device.name.contains("laptop", ignoreCase = true) ||
                        device.name.contains("book", ignoreCase = true)
                "desktop" -> device.name.contains("desktop", ignoreCase = true) ||
                        device.name.contains("pc", ignoreCase = true)
                "wifi" -> device.name.contains("wifi", ignoreCase = true) ||
                        device.name.contains("router", ignoreCase = true)
                else -> true
            }
        }
    }

    // Search devices
    fun searchDevices(query: String): List<DeviceData> {
        if (query.isEmpty()) return _connectedDevices.value

        return _connectedDevices.value.filter { device ->
            device.name.contains(query, ignoreCase = true) ||
                    device.ip.contains(query, ignoreCase = true) ||
                    device.mac.contains(query, ignoreCase = true)
        }
    }

    // Export data to CSV
    fun exportToCsv(context: Context) {
        val data = _networkSignalData.value
        exportToCsv(
            context = context,
            operator = data.operator,
            signalStrength = data.signalStrength,
            networkType = data.networkType,
            sinr = data.sinrSnr
        )
    }

    // Added missing method for repository compatibility
    fun getNetworkSignalData() = flowOf(_networkSignalData.value)

    // Add method to simulate submitting data to server
    fun submitSignalDataToServer() {
        viewModelScope.launch {
            // In a real app, this would make an API call
            // For now, just simulate a successful submission
            kotlinx.coroutines.delay(500)
            // Successfully submitted data simulation
        }
    }
}