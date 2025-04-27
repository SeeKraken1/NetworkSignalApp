package com.example.networksignalapp.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.networksignalapp.R
import com.example.networksignalapp.ui.components.BottomNavigationBar
import com.example.networksignalapp.ui.components.InfoCard
import com.example.networksignalapp.ui.components.LineChartView
import com.example.networksignalapp.ui.components.SanFranciscoMap
import com.example.networksignalapp.ui.components.SpeedTestView
import com.example.networksignalapp.ui.theme.Red
import com.example.networksignalapp.viewmodel.NetworkSignalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignalOverviewScreen(
    viewModel: NetworkSignalViewModel,
    onNavigateToServer: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    onToggleTheme: () -> Unit,
    onLogout: () -> Unit = {}
) {
    // Collect state from ViewModel
    val networkData by viewModel.networkSignalData.collectAsState()
    val signalHistory by viewModel.signalHistory.collectAsState()
    val isRecordingHistory by viewModel.isRecordingHistory.collectAsState()
    val realSignalStrength by viewModel.realSignalStrength.collectAsState()
    val signalQuality by viewModel.signalQuality.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Network Signal Monitor") },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onToggleTheme() }
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = "overview",
                onOverviewSelected = {},
                onServerSelected = onNavigateToServer,
                onStatisticsSelected = onNavigateToStatistics
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Signal Overview",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Signal Strength Card with real-time data
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Current Signal Strength",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$realSignalStrength dBm",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Signal Quality: $signalQuality",
                                style = MaterialTheme.typography.bodyMedium,
                                color = when(signalQuality) {
                                    "Excellent" -> MaterialTheme.colorScheme.primary
                                    "Good" -> MaterialTheme.colorScheme.primary
                                    "Fair" -> MaterialTheme.colorScheme.secondary
                                    else -> Red
                                }
                            )
                        }

                        // Signal Quality Indicator
                        SignalQualityIndicator(signalQuality = signalQuality)
                    }

                    // Toggle for history recording
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Record Signal History",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Switch(
                            checked = isRecordingHistory,
                            onCheckedChange = { viewModel.toggleHistoryRecording() }
                        )
                    }
                }
            }

            // Basic network information cards
            InfoCard(
                icon = R.drawable.ic_wifi,
                title = "Network Type",
                value = networkData.networkType
            )

            InfoCard(
                icon = R.drawable.ic_signal,
                title = "Operator",
                value = networkData.operator
            )

            // Map showing location - using our simplified map component
            SanFranciscoMap(signalStrength = "$realSignalStrength dBm")

            // Network Information Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Network Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NetworkInfoItem("Signal Power", networkData.signalPower)
                    NetworkInfoItem("SINR/SNR", networkData.sinrSnr)
                    NetworkInfoItem("Frequency Band", networkData.frequencyBand)
                    NetworkInfoItem("Cell ID", networkData.cellId)
                    NetworkInfoItem("Time Stamp", networkData.timeStamp)
                }
            }

            // Speed Test View
            SpeedTestView(viewModel = viewModel)

            // Signal History Chart
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Signal Strength History",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Signal Strength History Chart
                    LineChartView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        data = signalHistory.map { Pair(it.date, it.value) },
                        isDdbm = true
                    )

                    // Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.runSpeedTest() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Run Speed Test")
                        }

                        Button(
                            onClick = { viewModel.exportToCsv(context) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Export Data")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SignalQualityIndicator(signalQuality: String) {
    val color = when(signalQuality) {
        "Excellent" -> MaterialTheme.colorScheme.primary
        "Good" -> MaterialTheme.colorScheme.tertiary
        "Fair" -> MaterialTheme.colorScheme.secondary
        else -> Red
    }

    val bars = when(signalQuality) {
        "Excellent" -> 4
        "Good" -> 3
        "Fair" -> 2
        else -> 1
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.height(32.dp)
    ) {
        repeat(4) { index ->
            val barHeight = (index + 1) * 8
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(barHeight.dp)
                    .background(
                        color = if (index < bars) color else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                    )
            )
        }
    }
}

@Composable
fun NetworkInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }

    Divider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    )
}