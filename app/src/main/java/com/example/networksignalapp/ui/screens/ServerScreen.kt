package com.example.networksignalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
    viewModel: NetworkSignalViewModel,
    onNavigateToOverview: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    // Collect state from ViewModel
    val devices by viewModel.connectedDevices.collectAsState()
    
    // State for filtering devices
    var searchQuery by remember { mutableStateOf("") }
    var isFilterMenuExpanded by remember { mutableStateOf(false) }
    var selectedDeviceType by remember { mutableStateOf<String?>(null) }
    
    // Filter devices based on search query and selected type
    val filteredDevices = devices.filter { device ->
        val matchesSearch = searchQuery.isEmpty() || 
            device.name.contains(searchQuery, ignoreCase = true) ||
            device.ip.contains(searchQuery, ignoreCase = true) ||
            device.mac.contains(searchQuery, ignoreCase = true)
        
        val matchesType = selectedDeviceType == null || 
            getDeviceTypeFromIcon(device.iconRes) == selectedDeviceType
            
        matchesSearch && matchesType
    }
    
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
            
            // Connected devices section with search and filter
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Connected Devices (${filteredDevices.size})",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Filter button
                            IconButton(
                                onClick = { isFilterMenuExpanded = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        color = if (selectedDeviceType != null) Blue else LightGray,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_wifi),
                                    contentDescription = "Filter devices",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                
                                DropdownMenu(
                                    expanded = isFilterMenuExpanded,
                                    onDismissRequest = { isFilterMenuExpanded = false },
                                    modifier = Modifier.background(DarkGray)
                                ) {
                                    DeviceTypeFilterItem("All Devices", null, selectedDeviceType) {
                                        selectedDeviceType = it
                                        isFilterMenuExpanded = false
                                    }
                                    
                                    DeviceTypeFilterItem("Smartphones", "smartphone", selectedDeviceType) {
                                        selectedDeviceType = it
                                        isFilterMenuExpanded = false
                                    }
                                    
                                    DeviceTypeFilterItem("Laptops", "laptop", selectedDeviceType) {
                                        selectedDeviceType = it
                                        isFilterMenuExpanded = false
                                    }
                                    
                                    DeviceTypeFilterItem("Desktops", "desktop", selectedDeviceType) {
                                        selectedDeviceType = it
                                        isFilterMenuExpanded = false
                                    }
                                    
                                    DeviceTypeFilterItem("WiFi Devices", "wifi", selectedDeviceType) {
                                        selectedDeviceType = it
                                        isFilterMenuExpanded = false
                                    }
                                    
                                    DeviceTypeFilterItem("Other", "other", selectedDeviceType) {
                                        selectedDeviceType = it
                                        isFilterMenuExpanded = false
                                    }
                                }
                            }
                            
                            // Add new device button
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
                    
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search by name, IP, or MAC") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = LightGray,
                            focusedContainerColor = LightGray,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            cursorColor = Blue,
                            unfocusedPlaceholderColor = Color.Gray,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_wifi),
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                        }
                    )
                    
                    // Connected devices list
                    if (filteredDevices.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isEmpty() && selectedDeviceType == null) 
                                    "No devices connected" 
                                else 
                                    "No devices match your filter",
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredDevices) { device ->
                                DeviceItem(device)
                            }
                        }
                    }
                    
                    // Network overview section
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
                            value = "32.4 GB",
                            percentage = 65
                        )
                        
                        NetworkOverviewItem(
                            label = "Bandwidth",
                            value = "58.2 Mbps",
                            percentage = 72
                        )
                        
                        NetworkOverviewItem(
                            label = "Clients",
                            value = "${filteredDevices.size}/10",
                            percentage = filteredDevices.size * 10
                        )
                    }
                }
            }
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
fun DeviceTypeFilterItem(
    label: String,
    type: String?,
    selectedType: String?,
    onSelect: (String?) -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (type == selectedType) || (type == null && selectedType == null),
                    onClick = { onSelect(type) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Blue,
                        unselectedColor = Color.Gray
                    )
                )
                Text(
                    text = label,
                    color = Color.White
                )
            }
        },
        onClick = { onSelect(type) }
    )
}

@Composable
fun DeviceItem(device: DeviceData) {
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
                Text(
                    text = "MAC: ${device.mac}",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Connected",
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
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
            progress = percentage / 100f,
            modifier = Modifier
                .width(100.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = when {
                percentage > 80 -> Color.Red
                percentage > 60 -> Color.Yellow
                else -> Color.Green
            },
            trackColor = Color.DarkGray
        )
    }
}

// Helper function to get device type from icon resource
fun getDeviceTypeFromIcon(iconRes: Int): String {
    return when (iconRes) {
        R.drawable.ic_smartphone -> "smartphone"
        R.drawable.ic_laptop -> "laptop"
        R.drawable.ic_desktop -> "desktop"
        R.drawable.ic_wifi -> "wifi"
        else -> "other"
    }
}

