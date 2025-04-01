package com.example.networksignalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.networksignalapp.R
import com.example.networksignalapp.ui.components.BottomNavigationBar
import com.example.networksignalapp.ui.theme.DarkGray
import com.example.networksignalapp.ui.theme.LightGray

@Composable
fun ServerScreen(
    onNavigateToOverview: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    val devices = listOf(
        DeviceInfo(1, "Device 1", "192.168.1.100", "00:1A:2B:3C:4D:5E", R.drawable.ic_smartphone),
        DeviceInfo(2, "Device 2", "192.168.1.101", "00:1A:2B:3C:4D:5F", R.drawable.ic_laptop),
        DeviceInfo(3, "Device 3", "192.168.1.102", "00:1A:2B:3C:4D:60", R.drawable.ic_desktop),
        DeviceInfo(4, "Device 4", "192.168.1.103", "00:1A:2B:3C:4D:61", R.drawable.ic_wifi),
        DeviceInfo(5, "Device 5", "192.168.1.104", "00:1A:2B:3C:4D:62", R.drawable.ic_wifi),
        DeviceInfo(5, "Device 5", "192.168.1.104", "00:1A:2B:3C:4D:62", R.drawable.ic_speaker)
    )
    
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Server",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            
            // Mobile Network Card
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
                        text = "Mobile Network",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    
                    Column {
                        Text(
                            text = "Number of connected mobile devices",
                            color = Color.Gray
                        )
                        Text(
                            text = "4",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                    
                    Button(
                        onClick = { /* View detailed stats */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightGray
                        )
                    ) {
                        Text("See detailed device statistics")
                    }
                }
            }
            
            // Connected Devices Card
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
                    Text(
                        text = "Connected Devices",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    
                    devices.forEach { device ->
                        DeviceItem(device)
                    }
                    
                    Button(
                        onClick = { /* Add device */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("+ Add Device")
                    }
                }
            }
        }
    }
}

data class DeviceInfo(
    val id: Int,
    val name: String,
    val ip: String,
    val mac: String,
    val iconRes: Int
)

@Composable
fun DeviceItem(device: DeviceInfo) {
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
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = device.iconRes),
                    contentDescription = device.name,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column {
                Text(
                    text = device.name,
                    color = Color.White
                )
                Text(
                    text = "IP: ${device.ip} MAC: ${device.mac}",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

