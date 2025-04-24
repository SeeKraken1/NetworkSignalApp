package com.example.networksignalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.networksignalapp.viewmodel.NetworkSignalViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: NetworkSignalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetworkSignalAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NetworkSignalApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun NetworkSignalApp(viewModel: NetworkSignalViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "overview"
    ) {
        composable("overview") {
            SignalOverviewScreen(
                viewModel = viewModel,
                onNavigateToServer = { navController.navigate("server") },
                onNavigateToStatistics = { navController.navigate("statistics") }
            )
        }
        composable("server") {
            ServerScreen(
                viewModel = viewModel,
                onNavigateToOverview = { navController.navigate("overview") },
                onNavigateToStatistics = { navController.navigate("statistics") }
            )
        }
        composable("statistics") {
            NetworkStatisticsScreen(
                viewModel = viewModel,
                onNavigateToOverview = { navController.navigate("overview") },
                onNavigateToServer = { navController.navigate("server") }
            )
        }
    }
}