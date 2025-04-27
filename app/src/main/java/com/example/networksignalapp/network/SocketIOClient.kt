package com.example.networksignalapp.network

import android.content.Context
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject
import java.net.NetworkInterface
import java.net.URISyntaxException

/**
 * Client for Socket.IO real-time communication with the server
 */
class SocketIOClient(private val context: Context) {
    private lateinit var socket: Socket
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    companion object {
        private const val TAG = "SocketIOClient"
        private const val SERVER_URL = "http://10.0.2.2:5000" // Default for Android emulator
    }

    /**
     * Initialize the socket connection
     */
    fun initialize(serverUrl: String = SERVER_URL) {
        try {
            val options = IO.Options()
            options.reconnection = true
            options.reconnectionAttempts = Int.MAX_VALUE

            socket = IO.socket(serverUrl, options)
            setupSocketEventListeners()
        } catch (e: URISyntaxException) {
            Log.e(TAG, "Error initializing socket: ${e.message}")
        }
    }

    /**
     * Connect to the socket server
     */
    fun connect() {
        if (!::socket.isInitialized) {
            initialize()
        }
        socket.connect()
    }

    /**
     * Disconnect from the socket server
     */
    fun disconnect() {
        if (::socket.isInitialized && socket.connected()) {
            socket.disconnect()
            _isConnected.value = false
        }
    }

    /**
     * Set up event listeners for socket events
     */
    private fun setupSocketEventListeners() {
        socket.on(Socket.EVENT_CONNECT) {
            Log.d(TAG, "Socket connected")
            _isConnected.value = true

            // Send device info to server
            sendDeviceInfo()
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Log.d(TAG, "Socket disconnected")
            _isConnected.value = false
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.e(TAG, "Socket connection error: ${args[0]}")
            _isConnected.value = false
        }

        socket.on("disconnection_ack") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                Log.d(TAG, "Disconnection acknowledgment: ${data.getString("message")}")
            }
        }
    }

    /**
     * Send device information to the server
     */
    private fun sendDeviceInfo() {
        val deviceInfo = getDeviceNetworkInfo()
        val dataJson = JSONObject().apply {
            put("user_ip", deviceInfo.first)
            put("user_mac", deviceInfo.second)
        }

        socket.emit("user_data", dataJson)
        Log.d(TAG, "Sent device info: $dataJson")
    }

    /**
     * Get device IP and MAC address
     */
    private fun getDeviceNetworkInfo(): Pair<String, String> {
        // Get IP address (uses a simplified approach for demo)
        val ip = "192.168.1.${(1..254).random()}" // Simulated IP for demo

        // Get MAC address
        var macAddress = ""
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()

                if (networkInterface.name.equals("wlan0", ignoreCase = true)) {
                    val macBytes = networkInterface.hardwareAddress
                    if (macBytes != null) {
                        val strBuilder = StringBuilder()
                        for (b in macBytes) {
                            strBuilder.append(String.format("%02X:", b))
                        }
                        if (strBuilder.isNotEmpty()) {
                            strBuilder.deleteCharAt(strBuilder.length - 1)
                        }
                        macAddress = strBuilder.toString()
                        break
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting MAC address: ${e.message}")
            e.printStackTrace()
        }

        // If MAC not available, generate a random one for demo
        if (macAddress.isEmpty()) {
            val random = java.util.Random()
            val bytes = ByteArray(6)
            random.nextBytes(bytes)
            macAddress = bytes.joinToString(":") { String.format("%02X", it) }
        }

        return Pair(ip, macAddress)
    }
}