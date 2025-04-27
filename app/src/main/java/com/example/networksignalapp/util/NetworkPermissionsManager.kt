package com.example.networksignalapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

/**
 * Helper class to manage network-related permissions for the application
 */
class NetworkPermissionsManager(
    private val activity: ComponentActivity
) {
    companion object {
        // Permissions needed for network signal information
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE
        )

        // Additional location permissions needed for specific network info on newer Android versions
        val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        /**
         * Check if all required permissions are granted
         */
        fun arePermissionsGranted(context: Context): Boolean {
            return REQUIRED_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
        }

        /**
         * Check if location permissions are granted
         */
        fun areLocationPermissionsGranted(context: Context): Boolean {
            return LOCATION_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    // Permission request launcher
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    // Callback for permission results
    private var onPermissionResult: ((Boolean) -> Unit)? = null

    /**
     * Initialize the permission launcher
     */
    fun initialize() {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.entries.all { it.value }
            onPermissionResult?.invoke(allGranted)
        }
    }

    /**
     * Request all required permissions
     */
    fun requestRequiredPermissions(callback: (Boolean) -> Unit) {
        onPermissionResult = callback

        val permissionsToRequest = REQUIRED_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isEmpty()) {
            callback(true)
            return
        }

        permissionLauncher.launch(permissionsToRequest)
    }

    /**
     * Request location permissions (needed for detailed cell info)
     */
    fun requestLocationPermissions(callback: (Boolean) -> Unit) {
        onPermissionResult = callback

        val permissionsToRequest = LOCATION_PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isEmpty()) {
            callback(true)
            return
        }

        permissionLauncher.launch(permissionsToRequest)
    }

    /**
     * Request all permissions needed for full app functionality
     */
    fun requestAllPermissions(callback: (Boolean) -> Unit) {
        onPermissionResult = callback

        val allPermissions = REQUIRED_PERMISSIONS + LOCATION_PERMISSIONS
        val permissionsToRequest = allPermissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isEmpty()) {
            callback(true)
            return
        }

        permissionLauncher.launch(permissionsToRequest)
    }

    /**
     * Check if post-notifications permission is needed (Android 13+)
     * and request it if necessary
     */
    fun requestNotificationPermissionIfNeeded(callback: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onPermissionResult = callback

            val permissionToRequest = Manifest.permission.POST_NOTIFICATIONS

            if (ContextCompat.checkSelfPermission(activity, permissionToRequest)
                == PackageManager.PERMISSION_GRANTED) {
                callback(true)
                return
            }

            permissionLauncher.launch(arrayOf(permissionToRequest))
        } else {
            // Not needed on older Android versions
            callback(true)
        }
    }
}