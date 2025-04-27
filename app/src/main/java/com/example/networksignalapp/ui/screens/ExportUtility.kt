package com.example.networksignalapp.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Exports network signal data to a CSV file
 */
fun exportToCsv(
    context: Context,
    operator: String,
    signalStrength: String,
    networkType: String,
    sinr: String
) {
    try {
        // Get the file
        val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+ use app's private directory
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        } else {
            // For older Android versions, use public directory
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        }

        // Create directory if it doesn't exist
        if (directory?.exists() != true) {
            directory?.mkdirs()
        }

        // Create timestamp for filename
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // Create file
        val fileName = "network_signal_data_$timestamp.csv"
        val file = File(directory, fileName)

        // Create header if file is new
        val isNewFile = !file.exists()

        if (isNewFile) {
            file.appendText("Timestamp,Operator,Signal Strength,Network Type,SINR\n")
        }

        // Format timestamp for data
        val dataTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Format the data as CSV
        val csvData = "$dataTimestamp,$operator,$signalStrength,$networkType,$sinr\n"

        // Append data to file
        file.appendText(csvData)

        // Show success message
        Toast.makeText(
            context,
            "Data exported to $fileName",
            Toast.LENGTH_SHORT
        ).show()

        // Share the file
        shareFile(context, file)

    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Error exporting data: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

/**
 * Shares a file with other apps
 */
private fun shareFile(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share CSV File"))

    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Error sharing file: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

/**
 * Exports historical signal data to CSV
 */
fun exportHistoricalData(
    context: Context,
    historyData: List<com.example.networksignalapp.model.SignalHistoryData>
) {
    try {
        // Get the file
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        // Create directory if it doesn't exist
        if (directory?.exists() != true) {
            directory?.mkdirs()
        }

        // Create timestamp for filename
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        // Create file
        val fileName = "signal_history_$timestamp.csv"
        val file = File(directory, fileName)

        // Write header
        file.writeText("Date,Signal Strength (dBm)\n")

        // Write data
        historyData.forEach { data ->
            file.appendText("${data.date},${data.value}\n")
        }

        // Show success message
        Toast.makeText(
            context,
            "History exported to $fileName",
            Toast.LENGTH_SHORT
        ).show()

        // Share the file
        shareFile(context, file)

    } catch (e: Exception) {
        Toast.makeText(
            context,
            "Error exporting history: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}