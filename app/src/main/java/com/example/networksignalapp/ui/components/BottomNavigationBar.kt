package com.example.networksignalapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    onOverviewSelected: () -> Unit,
    onServerSelected: () -> Unit,
    onStatisticsSelected: () -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        // Overview tab
        NavigationBarItem(
            selected = selectedTab == "overview",
            onClick = onOverviewSelected,
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Overview",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Overview") }
        )

        // Server tab
        NavigationBarItem(
            selected = selectedTab == "server",
            onClick = onServerSelected,
            icon = {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = "Server",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Server") }
        )

        // Statistics tab
        NavigationBarItem(
            selected = selectedTab == "statistics",
            onClick = onStatisticsSelected,
            icon = {
                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = "Statistics",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = { Text("Statistics") }
        )
    }
}