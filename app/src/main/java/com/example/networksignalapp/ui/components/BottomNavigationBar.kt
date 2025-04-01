package com.example.networksignalapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.networksignalapp.R
import com.example.networksignalapp.ui.theme.Black

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    onOverviewSelected: () -> Unit,
    onServerSelected: () -> Unit,
    onStatisticsSelected: () -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = Black,
        contentColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == "overview",
            onClick = onOverviewSelected,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = "Overview",
                    modifier = Modifier.size(24.dp) // Reduced size here
                )
            },
            label = { Text("Overview") }
        )
        
        NavigationBarItem(
            selected = selectedTab == "server",
            onClick = onServerSelected,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_server),
                    contentDescription = "Server",
                    modifier = Modifier.size(24.dp) // Reduced size here
                )
            },
            label = { Text("Server") }
        )
        
        NavigationBarItem(
            selected = selectedTab == "statistics",
            onClick = onStatisticsSelected,
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chart),
                    contentDescription = "Statistics",
                    modifier = Modifier.size(24.dp) // Reduced size here
                )
            },
            label = { Text("Statistics") }
        )
    }
}

