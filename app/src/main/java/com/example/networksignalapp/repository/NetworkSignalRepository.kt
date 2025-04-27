package com.example.networksignalapp.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import com.example.networksignalapp.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository handling all network-related operations including authentication,
 * data submission, and statistics retrieval
 */
class NetworkRepository(private val context: Context) {

    private val apiService: ApiService = RetrofitClient.create(ApiService::class.java)
    private val prefs: SharedPreferences = context.getSharedPreferences("network_signal_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm"
        private const val TIMESTAMP_FORMAT = "dd MMM yyyy hh:mm:ss a" // Format expected by server
    }

    /**
     * Login user and store token
     */
    suspend fun login(username: String, password: String): Flow<Result<TokenResponse>> = flow {
        try {
            val response = apiService.login(LoginRequest(username, password))
            if (response.isSuccessful && response.body() != null) {
                // Save token
                saveToken(response.body()!!.token)
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Login failed: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Register a new user
     */
    suspend fun register(username: String, password: String): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.register(RegisterRequest(username, password))
            if (response.isSuccessful) {
                emit(Result.success(true))
            } else {
                val errorMessage = if (response.code() == 409) {
                    "Username already taken"
                } else {
                    "Registration failed: ${response.code()}"
                }
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Submit cell network data to server
     */
    suspend fun submitCellData(
        operator: String,
        signalPower: Float,
        sinrSnr: Float,
        networkType: String,
        frequencyBand: String,
        cellId: String
    ): Flow<Result<Boolean>> = flow {
        try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                emit(Result.failure(Exception("Not authenticated")))
                return@flow
            }

            val timestamp = SimpleDateFormat(TIMESTAMP_FORMAT, Locale.US).format(Date())
            val deviceInfo = getDeviceNetworkInfo()

            val cellData = CellDataRequest(
                operator = operator,
                signalPower = signalPower,
                sinr_snr = sinrSnr,
                networkType = networkType,
                frequency_band = frequencyBand,
                cell_id = cellId,
                timestamp = timestamp,
                user_ip = deviceInfo.first,
                user_mac = deviceInfo.second
            )

            val response = apiService.submitCellData("Bearer $token", cellData)
            if (response.isSuccessful) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception("Failed to submit data: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get statistics for a specific date range
     */
    suspend fun getStatistics(startDate: Date, endDate: Date): Flow<Result<StatisticsResponse>> = flow {
        try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                emit(Result.failure(Exception("Not authenticated")))
                return@flow
            }

            val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.US)
            val request = DateRangeRequest(
                start_date = dateFormat.format(startDate),
                end_date = dateFormat.format(endDate)
            )

            val response = apiService.getStatistics("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to get statistics: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get centralized statistics about connected devices
     */
    suspend fun getCentralizedStatistics(): Flow<Result<CentralizedStatisticsResponse>> = flow {
        try {
            val response = apiService.getCentralizedStatistics()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.success(response.body()!!))
            } else {
                emit(Result.failure(Exception("Failed to get centralized statistics: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get device IP and MAC address
     */
    private fun getDeviceNetworkInfo(): Pair<String, String> {
        // Get IP address
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress = wifiManager.connectionInfo.ipAddress
        val ip = String.format(
            "%d.%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )

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
            e.printStackTrace()
            macAddress = "02:00:00:00:00:00" // Default MAC if unable to get
        }

        return Pair(ip, macAddress)
    }

    /**
     * Save authentication token
     */
    private fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    /**
     * Get saved authentication token
     */
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    /**
     * Logout user by clearing token
     */
    fun logout() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }
}