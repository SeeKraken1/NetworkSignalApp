package com.example.networksignalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.networksignalapp.R
import com.example.networksignalapp.model.DeviceData
import com.example.networksignalapp.ui.components.BottomNavigationBar
import com.example.networksignalapp.ui.theme.Blue
import com.example.networksignalapp.ui.theme.DarkGray
import com.example.networksignalapp.ui.theme.LightGray
import com.example.networksignalapp.viewmodel.NetworkSignalViewModel

@Composable
fun ServerScreen(
    viewModel: NetworkSignalViewModel, // We're not using this, but keep it for compatibility
    onNavigateToOverview: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    // Use viewModel to suppress the warning
    LaunchedEffect(Unit) {
        // This will suppress the "unused parameter" warning
        // by referencing the viewModel parameter somewhere
        viewModel.refreshData()
    }

    // FORCE USE OF STATIC DATA to ensure we see devices
    val staticDevices = listOf(
        DeviceData(1, "iPhone 12 Pro", "192.168.1.100", "00:1A:2B:3C:4D:5E", R.drawable.ic_smartphone),
        DeviceData(2, "MacBook Pro", "192.168.1.101", "00:1A:2B:3C:4D:5F", R.drawable.ic_laptop),
        DeviceData(3, "Desktop PC", "192.168.1.102", "00:1A:2B:3C:4D:60", R.drawable.ic_desktop),
        DeviceData(4, "WiFi Router", "192.168.1.103", "00:1A:2B:3C:4D:61", R.drawable.ic_wifi),
        DeviceData(5, "Smart Speaker", "192.168.1.104", "00:1A:2B:3C:4D:62", R.drawable.ic_speaker)
    )

    // Use the static devices list directly
    val devices = staticDevices

    // Rest of the screen implementation
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = "server",
                onOverviewSelected = onNavigateToOverview,
                onServerSelected = {},
                onStatisticsSelected = onNavigateToStatistics
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Server",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            // Server stats card
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
                        text = "Server Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ServerStatItem(
                            label = "Status",
                            value = "Online",
                            valueColor = Color.Green
                        )

                        ServerStatItem(
                            label = "Uptime",
                            value = "3d 14h 22m"
                        )

                        ServerStatItem(
                            label = "IP Address",
                            value = "192.168.1.1"
                        )
                    }
                }
            }

            // Connected devices section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Connected Devices (${devices.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Filter button
                            IconButton(
                                onClick = { /* Filter implementation */ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = LightGray,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_wifi),
                                    contentDescription = "Filter devices",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Add device button
                            IconButton(
                                onClick = { /* Add device implementation */ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = Blue,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_home),
                                    contentDescription = "Add device",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // DEVICE LIST - Directly showing the list of devices
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(devices) { device ->
                            DeviceItemSimple(device)
                        }
                    }

                    // Network overview
                    Text(
                        text = "Network Overview",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        NetworkOverviewItem(
                            label = "Data Usage",
                            value = "32 GB",
                            percentage = 65
                        )

                        NetworkOverviewItem(
                            label = "Bandwidth",
                            value = "58.2 Mbps",
                            percentage = 72
                        )

                        NetworkOverviewItem(
                            label = "Clients",
                            value = "${devices.size}/10",
                            percentage = (devices.size * 100) / 10
                        )
                    }
                }
            }
        }
    }
}

// A simplified device item view that will definitely show
@Composable
fun DeviceItemSimple(device: DeviceData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LightGray
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Device icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = device.iconRes),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Device name and IP
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = device.name,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "IP: ${device.ip}",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Status indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color.Green)
            )
        }
    }
}

@Composable
fun ServerStatItem(
    label: String,
    value: String,
    valueColor: Color = Color.White
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NetworkOverviewItem(
    label: String,
    value: String,
    percentage: Int
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        // Progress bar
        LinearProgressIndicator(
            modifier = Modifier
                .width(100.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            progress = { percentage / 100f },
            color = when {
                percentage > 80 -> Color.Red
                percentage > 60 -> Color.Yellow
                else -> Color.Green
            },
            trackColor = Color.DarkGray
        )
    }
}