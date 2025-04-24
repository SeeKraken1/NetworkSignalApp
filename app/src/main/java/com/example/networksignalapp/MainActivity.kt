package com.example.networksignalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.networksignalapp.ui.screens.NetworkStatisticsScreen
import com.example.networksignalapp.ui.screens.ServerScreen
import com.example.networksignalapp.ui.screens.SignalOverviewScreen
import com.example.networksignalapp.ui.theme.NetworkSignalAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetworkSignalAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NetworkSignalApp()
                }
            }
        }
    }
}

@Composable
fun NetworkSignalApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "overview"
    ) {
        composable("overview") {
            SignalOverviewScreen(
                onNavigateToServer = { navController.navigate("server") },
                onNavigateToStatistics = { navController.navigate("statistics") }
            )
        }
        composable("server") {
            ServerScreen(
                onNavigateToOverview = { navController.navigate("overview") },
                onNavigateToStatistics = { navController.navigate("statistics") }
            )
        }
        composable("statistics") {
            NetworkStatisticsScreen(
                onNavigateToOverview = { navController.navigate("overview") },
                onNavigateToServer = { navController.navigate("server") }
            )
        }
    }
}