package com.example.networksignalapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.networksignalapp.ui.screens.LoginScreen
import com.example.networksignalapp.ui.screens.NetworkStatisticsScreen
import com.example.networksignalapp.ui.screens.RegisterScreen
import com.example.networksignalapp.ui.screens.ServerScreen
import com.example.networksignalapp.ui.screens.SignalOverviewScreen
import com.example.networksignalapp.ui.theme.NetworkSignalAppTheme
import com.example.networksignalapp.viewmodel.NetworkSignalViewModel
import com.example.networksignalapp.viewmodel.NetworkSignalViewModelFactory
import com.example.networksignalapp.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {

    companion object {
        private const val CHANNEL_ID = "weak_signal_channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "MainActivity"
    }

    private lateinit var networkViewModel: NetworkSignalViewModel
    private lateinit var userViewModel: UserViewModel

    private var telephonyManager: TelephonyManager? = null

    // Permission request launcher for phone state
    private val phoneStatePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                startMonitoringSignal()
            } catch (e: Exception) {
                Log.e(TAG, "Error starting signal monitoring after permission granted", e)
            }
        } else {
            Toast.makeText(
                this,
                "Permission denied. Real signal data will not be available.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Permission request launcher for notifications
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            // Initialize ViewModels with proper factory
            networkViewModel = ViewModelProvider(
                this,
                NetworkSignalViewModelFactory(applicationContext)
            )[NetworkSignalViewModel::class.java]

            userViewModel = ViewModelProvider(
                this,
                UserViewModel.Factory(applicationContext)
            )[UserViewModel::class.java]

            // Create notification channel
            createNotificationChannel()

            // Request permissions
            requestRequiredPermissions()

            setContent {
                // Use the ViewModels that are already initialized
                val isDarkTheme by networkViewModel.isDarkTheme.collectAsState()
                val isLoggedIn by userViewModel.isLoggedIn.collectAsState(initial = false)

                NetworkSignalAppTheme(darkTheme = isDarkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NetworkSignalApp(
                            networkViewModel = networkViewModel,
                            userViewModel = userViewModel,
                            isLoggedIn = isLoggedIn
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during app initialization", e)
            Toast.makeText(this, "Error starting app: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Signal Strength Notifications"
                val descriptionText = "Notifications for weak signal strength"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating notification channel", e)
        }
    }

    private fun requestRequiredPermissions() {
        try {
            // Request READ_PHONE_STATE permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                phoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
            } else {
                startMonitoringSignal()
            }

            // Request POST_NOTIFICATIONS permission for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting permissions", e)
        }
    }

    private fun startMonitoringSignal() {
        try {
            telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

            telephonyManager?.let { manager ->
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // Use the new TelephonyCallback API for Android 12+
                        val telephonyCallback = object : TelephonyCallback(), TelephonyCallback.SignalStrengthsListener {
                            override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                                processSignalStrength(signalStrength)
                            }
                        }

                        manager.registerTelephonyCallback(mainExecutor, telephonyCallback)
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
                        manager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for signal monitoring", e)
            Toast.makeText(this, "Permission denied for signal monitoring", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting signal monitoring", e)
            Toast.makeText(this, "Error starting signal monitoring", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processSignalStrength(signalStrength: SignalStrength) {
        try {
            // Extract signal data
            val dbm = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    signalStrength.cellSignalStrengths.firstOrNull()?.dbm ?: -120
                } else {
                    -120 // Default value for older Android versions
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting signal strength", e)
                -120
            }

            // Update the signal data in ViewModel
            networkViewModel.updateSignalStrength(dbm)

            // Check if signal is weak
            if (dbm < -100) {
                showWeakSignalNotification(dbm)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing signal strength", e)
        }
    }

    private fun showWeakSignalNotification(dbm: Int) {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_signal)
                .setContentTitle("Weak Signal Detected")
                .setContentText("Your signal strength is $dbm dBm")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing notification", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        // Clean up resources
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // No need to unregister with the new API, it's done automatically
            } else {
                @Suppress("DEPRECATION")
                telephonyManager?.listen(null, PhoneStateListener.LISTEN_NONE)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up resources", e)
        }
    }
}

@Composable
fun NetworkSignalApp(
    networkViewModel: NetworkSignalViewModel,
    userViewModel: UserViewModel,
    isLoggedIn: Boolean
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "overview" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("overview") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate("register") },
                viewModel = userViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") },
                onLoginClick = { navController.navigate("login") },
                viewModel = userViewModel
            )
        }

        composable("overview") {
            SignalOverviewScreen(
                viewModel = networkViewModel,
                onNavigateToServer = { navController.navigate("server") },
                onNavigateToStatistics = { navController.navigate("statistics") },
                onToggleTheme = { networkViewModel.toggleTheme() },
                onLogout = {
                    userViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("overview") { inclusive = true }
                    }
                }
            )
        }

        composable("server") {
            ServerScreen(
                viewModel = networkViewModel,
                onNavigateToOverview = { navController.navigate("overview") },
                onNavigateToStatistics = { navController.navigate("statistics") }
            )
        }

        composable("statistics") {
            NetworkStatisticsScreen(
                viewModel = networkViewModel,
                onNavigateToOverview = { navController.navigate("overview") },
                onNavigateToServer = { navController.navigate("server") }
            )
        }
    }
}