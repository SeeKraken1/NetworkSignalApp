package com.example.networksignalapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.networksignalapp.ui.components.BarChartView
import com.example.networksignalapp.ui.components.BottomNavigationBar
import com.example.networksignalapp.ui.components.DateRangeSelector
import com.example.networksignalapp.ui.theme.Blue
import com.example.networksignalapp.ui.theme.DarkGray
import com.example.networksignalapp.ui.theme.Green
import com.example.networksignalapp.ui.theme.Red
import com.example.networksignalapp.viewmodel.NetworkSignalViewModel

@Composable
fun NetworkStatisticsScreen(
    viewModel: NetworkSignalViewModel,
    onNavigateToOverview: () -> Unit,
    onNavigateToServer: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("connectivity") }
    var showCalendar by remember { mutableStateOf(false) }
    var dateRangeText by remember { mutableStateOf("Filter by date range") }

    // Collect state from ViewModel
    val statisticsData by viewModel.networkStatistics.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = "statistics",
                onOverviewSelected = onNavigateToOverview,
                onServerSelected = onNavigateToServer,
                onStatisticsSelected = {}
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
                text = "Network Statistics",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            if (showCalendar) {
                DateRangeSelector(
                    onApply = { rangeText ->
                        dateRangeText = rangeText
                        // Here you would filter your data based on the selected date range
                        // viewModel.filterDataByDateRange(startDate, endDate)
                        showCalendar = false
                    },
                    onDismiss = { showCalendar = false }
                )
            } else {
                // Date Range Filter Button
                Button(
                    onClick = { showCalendar = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkGray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(dateRangeText)
                }

                // Tabs
                TabRow(
                    selectedTabIndex = when(selectedTab) {
                        "connectivity" -> 0
                        "signal" -> 1
                        else -> 2
                    },
                    containerColor = DarkGray,
                    contentColor = Blue
                ) {
                    Tab(
                        selected = selectedTab == "connectivity",
                        onClick = { selectedTab = "connectivity" },
                        text = { Text("Connectivity") }
                    )
                    Tab(
                        selected = selectedTab == "signal",
                        onClick = { selectedTab = "signal" },
                        text = { Text("Signal Power") }
                    )
                    Tab(
                        selected = selectedTab == "snr",
                        onClick = { selectedTab = "snr" },
                        text = { Text("SNR/SINR") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    "connectivity" -> {
                        // Average Connectivity Time Per Operator
                        StatisticCard(
                            title = "Average Connectivity Time Per Operator",
                            value = statisticsData.averageConnectivity,
                            trend = "+0.3%",
                            trendIsPositive = true,
                            timeFrame = "Last 30 days"
                        ) {
                            BarChartView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                data = statisticsData.operatorTime.map {
                                    Pair(it.key, it.value)
                                },
                                barColor = Blue
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Average Connectivity Time Per Network Type
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
                                    text = "Average Connectivity Time Per Network Type",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )

                                statisticsData.timeInNetworkType.forEach { (type, value) ->
                                    NetworkTypeStatItem(
                                        type = type,
                                        percentage = value.toInt(),
                                        trend = if (type == "4G") "+5%" else if (type == "3G") "-5%" else "0%",
                                        trendIsPositive = type == "4G"
                                    )
                                }
                            }
                        }
                    }
                    "signal" -> {
                        // Signal Power Card
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
                                    text = "Average Signal Power Per Network Type",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )

                                statisticsData.signalPowerByType.forEach { (type, value) ->
                                    SignalPowerStatItem(
                                        type = type,
                                        value = value,
                                        trend = if (type == "4G") "+3%" else if (type == "3G") "-1%" else "0%",
                                        trendIsPositive = type == "4G"
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Signal Power Per Device
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
                                    text = "Average Signal Power Per Device",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )

                                val devices = listOf("Device1", "Device2", "Device3", "Device4")
                                val values = listOf(-68f, -72f, -78f, -65f)

                                devices.forEachIndexed { index, device ->
                                    SignalPowerStatItem(
                                        type = device,
                                        value = values[index],
                                        trend = if (index % 2 == 0) "+2%" else "-1%",
                                        trendIsPositive = index % 2 == 0
                                    )
                                }
                            }
                        }
                    }
                    "snr" -> {
                        // SNR Card
                        StatisticCard(
                            title = "Average SNR by Network Type",
                            value = "12 dB",
                            trend = "+5%",
                            trendIsPositive = true,
                            timeFrame = "Last 7 Days"
                        ) {
                            BarChartView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                data = listOf(
                                    Pair("2G", 5f),
                                    Pair("3G", 8f),
                                    Pair("4G", 12f),
                                    Pair("5G", 15f)
                                ),
                                barColor = Green
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // SINR Card
                        StatisticCard(
                            title = "Average SINR by Network Type",
                            value = "10 dB",
                            trend = "+2%",
                            trendIsPositive = true,
                            timeFrame = "Last 7 Days"
                        ) {
                            BarChartView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                data = listOf(
                                    Pair("2G", 3f),
                                    Pair("3G", 6f),
                                    Pair("4G", 10f),
                                    Pair("5G", 14f)
                                ),
                                barColor = Blue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String,
    trend: String,
    trendIsPositive: Boolean,
    timeFrame: String,
    content: @Composable () -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(0.7f)
                )

                // Improved layout for value and trend info
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.weight(0.3f)
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = timeFrame,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    Text(
                        text = trend,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (trendIsPositive) Green else Red
                    )
                }
            }

            content()
        }
    }
}

@Composable
fun NetworkTypeStatItem(
    type: String,
    percentage: Int,
    trend: String,
    trendIsPositive: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = type,
                color = Color.White
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$percentage%",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Daily $trend",
                    color = if (trendIsPositive) Green else if (trend == "0%") Color.White else Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // Progress bar
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            progress = { percentage / 100f },
            color = if (type == "4G") Blue else if (type == "3G") Green else Color.LightGray,
            trackColor = Color.DarkGray
        )

        // Chart placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            // Simple chart visualization would go here
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun SignalPowerStatItem(
    type: String,
    value: Float,
    trend: String,
    trendIsPositive: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = type,
                color = Color.White
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${value.toInt()} dBm",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Last 24 hours $trend",
                    color = if (trendIsPositive) Green else if (trend == "0%") Color.White else Red,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // Progress bar - normalize between -120 dBm (0%) and -50 dBm (100%)
        val normalizedValue = ((value + 120) / 70f).coerceIn(0f, 1f)
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            progress = { normalizedValue },
            color = if (normalizedValue > 0.7f) Green else if (normalizedValue > 0.4f) Blue else Red,
            trackColor = Color.DarkGray
        )
    }
}