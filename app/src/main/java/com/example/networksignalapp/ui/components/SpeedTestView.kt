package com.example.networksignalapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.networksignalapp.repository.NetworkSignalRepository
import com.example.networksignalapp.repository.SpeedTestResult
import com.example.networksignalapp.repository.SpeedTestStatus
import com.example.networksignalapp.ui.theme.Blue
import com.example.networksignalapp.ui.theme.DarkGray
import com.example.networksignalapp.ui.theme.Green
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SpeedTestView() {
    val repository = remember { NetworkSignalRepository() }
    val coroutineScope = rememberCoroutineScope()

    var isRunningTest by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<SpeedTestResult?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = DarkGray
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
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            if (isRunningTest) {
                // Show test in progress
                SpeedTestProgress(testResult)
            } else if (testResult?.status == SpeedTestStatus.COMPLETE) {
                // Show completed test results
                SpeedTestResults(testResult!!)
            } else {
                // Show start test button
                Button(
                    onClick = {
                        isRunningTest = true
                        coroutineScope.launch {
                            repository.runSpeedTest().collect { result ->
                                testResult = result
                                isRunningTest = result.status != SpeedTestStatus.COMPLETE
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue
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
                null -> "Preparing test..."
            },
            color = Color.White
        )

        // Progress
        LinearProgressIndicator(
            progress = result?.progressPercentage?.div(100f) ?: 0f,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Blue,
            trackColor = Color.DarkGray
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
                        color = Color.White
                    )
                }
                else -> {
                    // Show partial results as they become available
                    if (result.downloadSpeed > 0) {
                        Text(
                            text = "Download: ${String.format(Locale.US, "%.1f", result.downloadSpeed)} Mbps",
                            color = Color.White
                        )
                    }
                    if (result.uploadSpeed > 0) {
                        Text(
                            text = "Upload: ${String.format(Locale.US, "%.1f", result.uploadSpeed)} Mbps",
                            color = Color.White
                        )
                    }
                    if (result.ping > 0) {
                        Text(
                            text = "Ping: ${result.ping} ms",
                            color = Color.White
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
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", result.downloadSpeed)} Mbps",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Upload",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", result.uploadSpeed)} Mbps",
                    color = Color.White,
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

        // Run again button
        Button(
            onClick = { /* Will be handled in parent */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Blue
            )
        ) {
            Text("Run Again")
        }
    }
}

@Composable
fun SpeedTestMetric(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}