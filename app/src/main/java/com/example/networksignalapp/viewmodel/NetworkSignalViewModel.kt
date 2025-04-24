package com.example.networksignalapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class NetworkSignalViewModel : ViewModel() {

    private val repository = NetworkSignalRepository()

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

    init {
        // Load initial data
        loadSignalHistory()
        loadConnectedDevices()
        loadNetworkStatistics()
        loadServerInfo()

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

    fun runSpeedTest() {
        if (_isSpeedTestRunning.value) return

        _isSpeedTestRunning.value = true
        _speedTestResult.value = null

        viewModelScope.launch {
            repository.runSpeedTest().collect { result ->
                _speedTestResult.value = result
                _isSpeedTestRunning.value = result.status != SpeedTestStatus.COMPLETE
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
                "smartphone" -> device.iconRes == com.example.networksignalapp.R.drawable.ic_smartphone
                "laptop" -> device.iconRes == com.example.networksignalapp.R.drawable.ic_laptop
                "desktop" -> device.iconRes == com.example.networksignalapp.R.drawable.ic_desktop
                "wifi" -> device.iconRes == com.example.networksignalapp.R.drawable.ic_wifi
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
}