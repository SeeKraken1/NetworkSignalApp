package com.example.networksignalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.networksignalapp.R
import com.example.networksignalapp.ui.components.BottomNavigationBar
import com.example.networksignalapp.ui.components.InfoCard
import com.example.networksignalapp.ui.components.LineChartView
import com.example.networksignalapp.ui.components.SanFranciscoMap
import com.example.networksignalapp.ui.theme.DarkGray
import com.example.networksignalapp.ui.theme.LightGray
import com.example.networksignalapp.ui.theme.Red

@Composable
fun SignalOverviewScreen(
    onNavigateToServer: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    Scaffold(
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
                .background(Color.Black)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Signal",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            
            // Signal Strength Card
            InfoCard(
                icon = R.drawable.ic_signal,
                title = "Signal strength",
                value = "-106dBm"
            )
            
            // Network Type Card
            InfoCard(
                icon = R.drawable.ic_wifi,
                title = "Network type",
                value = "3G"
            )
            
            // Map
            SanFranciscoMap()
            
            // Network Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Network Information",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    NetworkInfoItem("Operator", "T-Mobile")
                    NetworkInfoItem("Signal Power", "-95dBm")
                    NetworkInfoItem("SINR/SNR", "20dB")
                    NetworkInfoItem("Network Type", "LTE")
                    NetworkInfoItem("Frequency Band", "Band 66")
                    NetworkInfoItem("Cell ID", "1234567")
                    NetworkInfoItem("Time Stamp", "2022-02-13 12:34:56")
                }
            }
            
            // Speed Test Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Speed test",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    NetworkInfoItem("Download speed:", "12.4 Mbps")
                    NetworkInfoItem("Upload speed:", "8.3 Mbps")
                    NetworkInfoItem("Ping:", "109 ms")
                    NetworkInfoItem("Jitter:", "9 ms")
                    NetworkInfoItem("Packet loss:", "0%")
                }
            }
            
            // Signal Strength Chart Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DarkGray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Signal strength",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "-106dBm",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                            Text(
                                text = "This month -2%",
                                style = MaterialTheme.typography.labelSmall,
                                color = Red
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Signal Strength Chart
                    LineChartView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
            }
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
            color = Color.Gray,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
    
    Divider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = LightGray.copy(alpha = 0.5f)
    )
}

