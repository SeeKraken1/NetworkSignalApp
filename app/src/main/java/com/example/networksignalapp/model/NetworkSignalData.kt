package com.example.networksignalapp.model

data class NetworkSignalData(
    val signalStrength: String = "-106dBm",
    val networkType: String = "3G",
    val operator: String = "T-Mobile",
    val signalPower: String = "-95dBm",
    val sinrSnr: String = "20dB",
    val frequencyBand: String = "Band 66",
    val cellId: String = "1234567",
    val timeStamp: String = "2022-02-13 12:34:56",
    val downloadSpeed: String = "12.4 Mbps",
    val uploadSpeed: String = "8.3 Mbps",
    val ping: String = "109 ms",
    val jitter: String = "9 ms",
    val packetLoss: String = "0%"
)

data class SignalHistoryData(
    val date: String,
    val value: Float
)

data class DeviceData(
    val id: Int,
    val name: String,
    val ip: String,
    val mac: String,
    val iconRes: Int
)

data class NetworkStatisticsData(
    val averageConnectivity: String = "98%",
    val timeInNetworkType: Map<String, Float> = mapOf(
        "4G" to 40f,
        "3G" to 30f,
        "2G" to 30f
    ),
    val operatorTime: Map<String, Float> = mapOf(
        "Verizon" to 1.2f,
        "T-Mobile" to 1.5f,
        "AT&T" to 0.8f
    ),
    val signalPowerByType: Map<String, Float> = mapOf(
        "4G" to -65f,
        "3G" to -85f,
        "2G" to -100f
    ),
    val snrByType: Map<String, Float> = mapOf(
        "4G" to 12f,
        "3G" to 8f,
        "2G" to 5f
    )
)