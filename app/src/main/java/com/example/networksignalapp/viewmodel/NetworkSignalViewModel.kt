package com.example.networksignalapp.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networksignalapp.R
import com.example.networksignalapp.model.DeviceData
import com.example.networksignalapp.model.NetworkSignalData
import com.example.networksignalapp.model.NetworkStatisticsData
import com.example.networksignalapp.model.SignalHistoryData
import com.example.networksignalapp.repository.NetworkSignalRepository
import com.example.networksignalapp.repository.ServerInfo
import com.example.networksignalapp.repository.SpeedTestResult
import com.example.networksignalapp.repository.SpeedTestStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NetworkSignalViewModel(
    private val repository: NetworkSignalRepository = NetworkSignalRepository(),
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

    // Server information
    private val _serverInfo = MutableStateFlow<ServerInfo?>(null)
    val serverInfo: StateFlow<ServerInfo?> = _serverInfo.asStateFlow()

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
        loadServerInfo()
        loadThemePreference()

        // Start collecting live network data
        viewModelScope.launch {
            repository.getNetworkSignalData().collect { data ->
                _networkSignalData.value = data
            }
        }

        // Periodically refresh connected devices (simulate real-time updates)
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(5000) // Refresh every 5 seconds
                loadConnectedDevices()
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

    fun loadServerInfo() {
        _serverInfo.value = repository.getServerInfo()
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
        _speedTestResult.value = null

        viewModelScope.launch {
            repository.runSpeedTest().collect { result ->
                _speedTestResult.value = result
                _isSpeedTestRunning.value = result.status != SpeedTestStatus.COMPLETE

                // Update network data with speed test results when complete
                if (result.status == SpeedTestStatus.COMPLETE) {
                    _networkSignalData.value = _networkSignalData.value.copy(
                        downloadSpeed = "${result.downloadSpeed} Mbps",
                        uploadSpeed = "${result.uploadSpeed} Mbps",
                        ping = "${result.ping} ms",
                        jitter = "${result.jitter} ms",
                        packetLoss = "${result.packetLoss}%"
                    )
                }
            }
        }
    }

    fun refreshData() {
        loadConnectedDevices()
        loadNetworkStatistics()
        loadServerInfo()
        loadSignalHistory()
    }

    // Filter devices by type
    fun getDevicesByType(type: String?): List<DeviceData> {
        val devices = _connectedDevices.value
        if (type == null) return devices

        return devices.filter { device ->
            when (type) {
                "smartphone" -> device.iconRes == R.drawable.ic_smartphone
                "laptop" -> device.iconRes == R.drawable.ic_laptop
                "desktop" -> device.iconRes == R.drawable.ic_desktop
                "wifi" -> device.iconRes == R.drawable.ic_wifi
                else -> true
            }
        }
    }

    // Search devices
    fun searchDevices(query: String): List<DeviceData> {
        val devices = _connectedDevices.value
        if (query.isEmpty()) return devices

        return devices.filter { device ->
            device.name.contains(query, ignoreCase = true) ||
                    device.ip.contains(query, ignoreCase = true) ||
                    device.mac.contains(query, ignoreCase = true)
        }
    }

    // Export data to CSV
    fun exportToCsv(context: Context) {
        val data = _networkSignalData.value
        com.example.networksignalapp.ui.screens.exportToCsv(
            context = context,
            operator = data.operator,
            signalStrength = data.signalStrength,
            networkType = data.networkType,
            sinr = data.sinrSnr
        )
    }
}