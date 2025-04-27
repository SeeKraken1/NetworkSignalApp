package com.example.networksignalapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = Blue,
    secondary = LightBlue,
    tertiary = LBlue,
    background = Black,
    surface = DarkGray,
    surfaceVariant = LightGray,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White,
    primaryContainer = Blue.copy(alpha = 0.2f),
    secondaryContainer = LightBlue.copy(alpha = 0.2f)
)

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = Blue,
    secondary = LightBlue,
    tertiary = Gray,
    background = White,
    surface = Gray,
    surfaceVariant = LightGray.copy(alpha = 0.3f),
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Black,
    onSurface = Black,
    primaryContainer = Blue.copy(alpha = 0.1f),
    secondaryContainer = LightBlue.copy(alpha = 0.1f)
)

@Composable
fun NetworkSignalAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Explicitly avoid using dynamicColor to prevent ambiguity
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Update status bar color
            window.statusBarColor = colorScheme.background.toArgb()

            // Update system bars appearance
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}