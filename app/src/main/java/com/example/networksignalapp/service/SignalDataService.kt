package com.example.networksignalapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telephony.CellInfo
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.networksignalapp.MainActivity
import com.example.networksignalapp.R
import com.example.networksignalapp.repository.NetworkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Background service that monitors network signal strength and submits data to the server
 */
class SignalDataService : Service() {
    companion object {
        private const val TAG = "SignalDataService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "signal_monitor_channel"

        // Service control actions
        const val ACTION_START_SERVICE = "com.example.networksignalapp.START_SERVICE"
        const val ACTION_STOP_SERVICE = "com.example.networksignalapp.STOP_SERVICE"

        // Data collection interval in milliseconds (5 minutes by default)
        private const val DATA_COLLECTION_INTERVAL = 5 * 60 * 1000L
    }

    private val serviceScope = CoroutineScope(Dispatchers.Default)
    private lateinit var repository: NetworkRepository
    private lateinit var telephonyManager: TelephonyManager

    private var dataCollectionJob: Job? = null

    // Signal data
    private var signalStrength = -120 // Default weak signal value
    private var signalQuality = "Poor"
    private var operator = ""
    private var networkType = ""
    private var frequencyBand = ""
    private var cellId = ""
    private var sinrSnr = 0f

    override fun onCreate() {
        super.onCreate()
        repository = NetworkRepository(applicationContext)
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Monitoring signal strength..."))

        // Initialize signal tracking
        initializeSignalTracking()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SERVICE -> startDataCollection()
            ACTION_STOP_SERVICE -> stopSelf()
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        dataCollectionJob?.cancel()
        super.onDestroy()
    }

    /**
     * Initialize tracking of signal strength
     */
    private fun initializeSignalTracking() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Use the new TelephonyCallback API for Android 12+
                val telephonyCallback = object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
                    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                        processSignalStrength(signalStrength)
                    }
                }
                telephonyManager.registerTelephonyCallback(mainExecutor, telephonyCallback)
            } else {
                // Fall back to deprecated method for older Android versions
                @Suppress("DEPRECATION")
                val phoneStateListener = object : PhoneStateListener() {
                    @Suppress("DEPRECATION")
                    override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                        processSignalStrength(signalStrength)
                    }
                }

                @Suppress("DEPRECATION")
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
            }

            // Also get initial cell information
            updateCellInfo()

        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for signal monitoring", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing signal monitoring", e)
        }
    }

    /**
     * Process signal strength updates
     */
    private fun processSignalStrength(signalStrength: SignalStrength) {
        try {
            // Extract signal data based on Android version
            val dbm = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    signalStrength.cellSignalStrengths.firstOrNull()?.dbm ?: -120
                } else {
                    @Suppress("DEPRECATION")
                    // Attempt to use reflection to get the signal strength on older devices
                    val method = SignalStrength::class.java.getDeclaredMethod("getDbm")
                    method.isAccessible = true
                    (method.invoke(signalStrength) as? Int) ?: -120
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting signal strength", e)
                -120 // Default fallback value
            }

            // Update the signal data
            this.signalStrength = dbm

            // Update signal quality based on dbm value
            this.signalQuality = when {
                dbm > -80 -> "Excellent"
                dbm > -90 -> "Good"
                dbm > -100 -> "Fair"
                else -> "Poor"
            }

            // Update notification with current signal strength
            updateNotification("Signal: $dbm dBm ($signalQuality)")

            // Also update cell information when signal strength changes
            updateCellInfo()

        } catch (e: Exception) {
            Log.e(TAG, "Error processing signal strength", e)
        }
    }

    /**
     * Update cell information
     */
    private fun updateCellInfo() {
        try {
            // Get all cell info
            val cellInfoList = telephonyManager.allCellInfo

            if (cellInfoList != null && cellInfoList.isNotEmpty()) {
                val cellInfo = cellInfoList[0]

                // Extract operator info
                this.operator = telephonyManager.networkOperatorName ?: "Unknown"

                // Process cell info based on type
                when (cellInfo) {
                    is CellInfoLte -> {
                        this.networkType = "4G"
                        this.frequencyBand = "Band ${cellInfo.cellIdentity.earfcn / 1000}"
                        this.cellId = cellInfo.cellIdentity.ci.toString()

                        // Get SINR/SNR for LTE
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            this.sinrSnr = cellInfo.cellSignalStrength.rsrq.toFloat()
                        } else {
                            @Suppress("DEPRECATION")
                            this.sinrSnr = cellInfo.cellSignalStrength.rsrp.toFloat()
                        }
                    }
                    is CellInfoWcdma -> {
                        this.networkType = "3G"
                        this.frequencyBand = "UMTS ${cellInfo.cellIdentity.uarfcn}"
                        this.cellId = cellInfo.cellIdentity.cid.toString()
                        this.sinrSnr = cellInfo.cellSignalStrength.dbm.toFloat() / 10
                    }
                    is CellInfoGsm -> {
                        this.networkType = "2G"
                        this.frequencyBand = "GSM ${cellInfo.cellIdentity.arfcn}"
                        this.cellId = cellInfo.cellIdentity.cid.toString()
                        this.sinrSnr = 0f // Not available for GSM
                    }
                    else -> {
                        this.networkType = "Unknown"
                        this.frequencyBand = "Unknown"
                        this.cellId = "Unknown"
                        this.sinrSnr = 0f
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for cell info", e)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating cell info", e)
        }
    }

    /**
     * Start periodic data collection and submission
     */
    private fun startDataCollection() {
        // Cancel any existing job
        dataCollectionJob?.cancel()

        // Start a new data collection job
        dataCollectionJob = serviceScope.launch {
            while (true) {
                try {
                    collectAndSubmitData()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in data collection cycle", e)
                }

                // Wait for the next collection interval
                delay(DATA_COLLECTION_INTERVAL)
            }
        }
    }

    /**
     * Collect current signal data and submit to server
     */
    private suspend fun collectAndSubmitData() {
        Log.d(TAG, "Collecting signal data...")

        // Make sure we have fresh cell info
        withContext(Dispatchers.Main) {
            updateCellInfo()
        }

        // Only send data if we have valid signal strength
        if (signalStrength <= -140 || operator.isEmpty() || networkType.isEmpty()) {
            Log.d(TAG, "Skipping data submission - incomplete data")
            return
        }

        // Submit data to server
        repository.submitCellData(
            operator = operator,
            signalPower = signalStrength.toFloat(),
            sinrSnr = sinrSnr,
            networkType = networkType,
            frequencyBand = frequencyBand,
            cellId = cellId
        ).catch { e ->
            Log.e(TAG, "Error submitting data: ${e.message}")
        }.collect { result ->
            result.fold(
                onSuccess = {
                    Log.d(TAG, "Successfully submitted signal data")
                    // Update notification
                    val timeString = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    updateNotification("Last update: $timeString - $signalStrength dBm")
                },
                onFailure = { e ->
                    Log.e(TAG, "Failed to submit data: ${e.message}")
                }
            )
        }
    }

    /**
     * Create notification channel for foreground service (required for Android 8+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Signal Monitor Service"
            val descriptionText = "Monitors and records network signal strength"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Create notification for foreground service
     */
    private fun createNotification(content: String): android.app.Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Network Signal Monitor")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_signal)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    /**
     * Update the notification content
     */
    private fun updateNotification(content: String) {
        val notification = createNotification(content)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}