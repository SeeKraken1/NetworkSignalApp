package com.example.networksignalapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.networksignalapp.ui.theme.Green
import com.example.networksignalapp.viewmodel.NetworkSignalViewModel
import java.util.Locale

/**
 * Enum for speed test status
 */
enum class SpeedTestStatus {
    IDLE,
    STARTING,
    TESTING_DOWNLOAD,
    TESTING_UPLOAD,
    TESTING_PING,
    TESTING_JITTER,
    TESTING_PACKET_LOSS,
    COMPLETE
}

/**
 * Data class for speed test result
 */
data class SpeedTestResult(
    val downloadSpeed: Float = 0f, // Mbps
    val uploadSpeed: Float = 0f, // Mbps
    val ping: Int = 0, // ms
    val jitter: Float = 0f, // ms
    val packetLoss: Float = 0f, // percentage
    val status: SpeedTestStatus = SpeedTestStatus.IDLE,
    val progressPercentage: Int = 0
)

@Composable
fun SpeedTestView(
    viewModel: NetworkSignalViewModel,
    modifier: Modifier = Modifier
) {
    val speedTestResult by viewModel.speedTestResult.collectAsState()
    val isRunningTest by viewModel.isSpeedTestRunning.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Speed Test",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )

            if (isRunningTest) {
                // Show test in progress
                SpeedTestProgress(speedTestResult)
            } else if (speedTestResult?.status == SpeedTestStatus.COMPLETE) {
                // Show completed test results
                SpeedTestResults(speedTestResult!!)
            } else {
                // Show start test button
                Button(
                    onClick = { viewModel.runSpeedTest() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Start Speed Test")
                }
            }
        }
    }
}

@Composable
fun SpeedTestProgress(result: SpeedTestResult?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status text
        Text(
            text = when (result?.status) {
                SpeedTestStatus.STARTING -> "Preparing test..."
                SpeedTestStatus.TESTING_DOWNLOAD -> "Testing download speed..."
                SpeedTestStatus.TESTING_UPLOAD -> "Testing upload speed..."
                SpeedTestStatus.TESTING_PING -> "Testing ping..."
                SpeedTestStatus.TESTING_JITTER -> "Testing jitter..."
                SpeedTestStatus.TESTING_PACKET_LOSS -> "Testing packet loss..."
                SpeedTestStatus.COMPLETE -> "Test complete"
                else -> "Preparing test..."
            },
            color = MaterialTheme.colorScheme.onSurface
        )

        // Progress
        LinearProgressIndicator(
            progress = { result?.progressPercentage?.div(100f) ?: 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        // Current result if available
        if (result != null) {
            when (result.status) {
                SpeedTestStatus.TESTING_DOWNLOAD, SpeedTestStatus.TESTING_UPLOAD -> {
                    val speed = if (result.status == SpeedTestStatus.TESTING_DOWNLOAD)
                        result.downloadSpeed else result.uploadSpeed
                    Text(
                        text = "${String.format(Locale.US, "%.1f", speed)} Mbps",
                        color = Green,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                SpeedTestStatus.TESTING_PING -> {
                    Text(
                        text = "${result.downloadSpeed.toInt()} Mbps↓  ${result.uploadSpeed.toInt()} Mbps↑",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                else -> {
                    // Show partial results as they become available
                    if (result.downloadSpeed > 0) {
                        Text(
                            text = "Download: ${String.format(Locale.US, "%.1f", result.downloadSpeed)} Mbps",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (result.uploadSpeed > 0) {
                        Text(
                            text = "Upload: ${String.format(Locale.US, "%.1f", result.uploadSpeed)} Mbps",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (result.ping > 0) {
                        Text(
                            text = "Ping: ${result.ping} ms",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpeedTestResults(result: SpeedTestResult) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main results
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Download",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", result.downloadSpeed)} Mbps",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Upload",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", result.uploadSpeed)} Mbps",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Additional metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SpeedTestMetric("Ping", "${result.ping} ms")
            SpeedTestMetric("Jitter", "${String.format(Locale.US, "%.1f", result.jitter)} ms")
            SpeedTestMetric("Packet Loss", "${String.format(Locale.US, "%.1f", result.packetLoss)}%")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SpeedTestMetric(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}