# Network Signal App

A modern Android application for monitoring and visualizing network signal information, built with Jetpack Compose.

## Features

- **Signal Overview Screen**: Displays current network signal strength, type, and detailed network information
- **Server Information**: Shows connected devices and network server information
- **Network Statistics**: Provides comprehensive statistics on connectivity, signal power, and SNR/SINR measurements
- **Speed Test**: Run network speed tests to measure download/upload speeds, ping, jitter, and packet loss
- **Interactive Charts**: Visualize signal history and network metrics with interactive charts

## Project Structure

The app follows MVVM (Model-View-ViewModel) architecture:

- **Model**: Data classes representing network signal information, device data, and statistics
- **View**: Jetpack Compose UI components for displaying network data
- **ViewModel**: Manages UI-related data, handles business logic, and interacts with repositories
- **Repository**: Provides data from various sources (in this case simulated)

### Key Components

- **NetworkSignalData**: Model class representing network signal information
- **NetworkSignalRepository**: Provides network data (simulated for demo purposes)
- **NetworkSignalViewModel**: Manages data for the UI components
- **BarChartView/LineChartView**: Custom chart components for data visualization

## Technical Details

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose
- **Android API Target**: 35 (Android 15)
- **Minimum Android Version**: API 24 (Android 7.0 Nougat)

## Installation

1. Clone this repository
2. Open the project in Android Studio (version 2022.3.1 or higher recommended)
3. Build and run on an emulator or physical device

## Screenshots

The app follows the design in the provided mockups with dark-themed UI, featuring:

- Network signal strength visualization
- Server information and connected devices list
- Statistical charts and data visualization
- Speed test functionality

## Permissions

The app requires the following permissions:
- `INTERNET`: For speed testing and data fetching
- `ACCESS_NETWORK_STATE`: To monitor network connectivity
- `ACCESS_WIFI_STATE`: To access WiFi information
- `READ_PHONE_STATE`: To access cellular network information
- `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`: For precise network tower information

## Implementation Notes

- The app uses simulated data for demonstration purposes
- In a production environment, the app would use Android's TelephonyManager, NetworkCapabilities, and other APIs to fetch real network data
- Chart components are custom implementations optimized for network data visualization
- The UI is optimized for both light and dark themes, but defaults to dark theme as per the design requirements

## Future Improvements

- Real-time network signal monitoring using Android's TelephonyManager
- Historical data persistence using Room database
- Notification system for poor signal quality
- Widget for home screen signal monitoring
- Automated speed tests based on location
