package com.example.networksignalapp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networksignalapp.model.DeviceData
import com.example.networksignalapp.model.NetworkSignalData
import com.example.networksignalapp.model.NetworkStatisticsData
import com.example.networksignalapp.model.SignalHistoryData
import com.example.networksignalapp.repository.NetworkRepository
import com.example.networksignalapp.ui.components.SpeedTestResult
import com.example.networksignalapp.ui.components.SpeedTestStatus
import com.example.networksignalapp.ui.screens.exportToCsv
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class NetworkSignalViewModel(
    private val repository: NetworkRepository,
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

        // Start collecting live network data from repository if available
        viewModelScope.launch {
            repository.getNetworkSignalData().collect { data ->
                _networkSignalData.value = data
            }
        }
    }

    fun loadSignalHistory() {
        _signalHistory.value = repository.getSignalHistory()
    }

    fun loadConnectedDevices() {
        _connectedDevices.value = repository.getConnectedDevices()
    }

    fun loadNetworkStatistics() {
        _networkStatistics.value = repository.getNetworkStatistics()
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
    }

    // Submit data to the backend server
    fun submitSignalDataToServer() {
        viewModelScope.launch {
            val data = _networkSignalData.value
            repository.submitCellData(
                operator = data.operator,
                signalPower = _realSignalStrength.value.toFloat(),
                sinrSnr = data.sinrSnr.replace("dB", "").trim().toFloatOrNull() ?: 0f,
                networkType = data.networkType,
                frequencyBand = data.frequencyBand,
                cellId = data.cellId
            ).collect { result ->
                result.fold(
                    onSuccess = {
                        // Successfully submitted data to server
                        // You could update a status message here if needed
                    },
                    onFailure = { error ->
                        // Failed to submit data
                        // Log error or show notification to user
                    }
                )
            }
        }
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
}