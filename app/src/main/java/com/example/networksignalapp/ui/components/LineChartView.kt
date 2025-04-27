package com.example.networksignalapp.ui.components

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathEffect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.networksignalapp.ui.theme.Blue
import com.example.networksignalapp.ui.theme.Green
import com.example.networksignalapp.ui.theme.Red
import kotlin.math.abs

@Composable
fun LineChartView(
    modifier: Modifier = Modifier,
    data: List<Pair<String, Float>>,
    minValue: Float? = null,
    maxValue: Float? = null,
    lineColor: androidx.compose.ui.graphics.Color = Blue,
    fillColor: androidx.compose.ui.graphics.Color? = null,
    showPercentage: Boolean = false,
    isDdbm: Boolean = false,
    percentChange: Float? = null,
    formatLabels: Boolean = true // Added parameter to control label formatting
) {
    // Calculate min and max values if not provided
    val calculatedMinValue = minValue ?: data.minOfOrNull { it.second }?.minus(10f) ?: -120f
    val calculatedMaxValue = maxValue ?: data.maxOfOrNull { it.second }?.plus(10f) ?: -50f
    val valueRange = abs(calculatedMaxValue - calculatedMinValue)

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val padding = 50f
        val chartWidth = canvasWidth - (padding * 2)
        val chartHeight = canvasHeight - (padding * 2)

        // Draw axes
        val axisPaint = Paint().apply {
            color = Color.GRAY
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }

        // X-axis
        drawContext.canvas.nativeCanvas.drawLine(
            padding, canvasHeight - padding,
            canvasWidth - padding, canvasHeight - padding,
            axisPaint
        )

        // Y-axis
        drawContext.canvas.nativeCanvas.drawLine(
            padding, padding,
            padding, canvasHeight - padding,
            axisPaint
        )

        // Draw grid lines
        val gridPaint = Paint().apply {
            color = Color.DKGRAY
            strokeWidth = 1f
            style = Paint.Style.STROKE
            pathEffect = android.graphics.DashPathEffect(floatArrayOf(5f, 5f), 0f)
        }

        // Horizontal grid lines
        val gridStep = chartHeight / 4
        for (i in 0..4) {
            val y = canvasHeight - padding - (i * gridStep)
            drawContext.canvas.nativeCanvas.drawLine(
                padding, y,
                canvasWidth - padding, y,
                gridPaint
            )

            // Draw grid value labels
            val gridValue = calculatedMinValue + (i * valueRange / 4)
            val displayValue = if (isDdbm) {
                "${gridValue.toInt()}dBm"
            } else {
                if (showPercentage) "${gridValue.toInt()}%" else gridValue.toInt().toString()
            }

            val valuePaint = Paint().apply {
                color = Color.LTGRAY
                textSize = 25f
                textAlign = Paint.Align.RIGHT
            }

            drawContext.canvas.nativeCanvas.drawText(
                displayValue,
                padding - 10f,
                y + 10f,
                valuePaint
            )
        }

        // Calculate x and y positions
        val xStep = chartWidth / (data.size - 1).coerceAtLeast(1)

        val points = data.mapIndexed { index, (label, value) ->
            val x = padding + (index * xStep)
            val normalizedValue = (value - calculatedMinValue) / valueRange
            val y = canvasHeight - padding - (normalizedValue * chartHeight)

            // Format timestamp label for better readability
            val displayLabel = if (formatLabels) {
                formatTimestamp(label)
            } else {
                label
            }

            // Draw x-axis labels
            val textPaint = Paint().apply {
                color = Color.WHITE
                textSize = 24f
                textAlign = Paint.Align.CENTER
            }

            // Draw labels at regular intervals to avoid overcrowding
            val labelInterval = (data.size / 5).coerceAtLeast(1)
            if (index % labelInterval == 0 || index == data.size - 1) {
                drawContext.canvas.nativeCanvas.drawText(
                    displayLabel,
                    x,
                    canvasHeight - padding + 30f,
                    textPaint
                )
            }

            Pair(x, y)
        }

        // Draw fill if fillColor is provided
        if (fillColor != null) {
            val path = Path()
            path.moveTo(points.first().first, canvasHeight - padding)
            points.forEach { (x, y) ->
                path.lineTo(x, y)
            }
            path.lineTo(points.last().first, canvasHeight - padding)
            path.close()

            val fillPaint = Paint().apply {
                color = fillColor.copy(alpha = 0.3f).toArgb()
                style = Paint.Style.FILL
            }

            drawContext.canvas.nativeCanvas.drawPath(path, fillPaint)
        }

        // Draw lines between points
        val linePaint = Paint().apply {
            color = lineColor.toArgb()
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }

        for (i in 0 until points.size - 1) {
            drawContext.canvas.nativeCanvas.drawLine(
                points[i].first, points[i].second,
                points[i + 1].first, points[i + 1].second,
                linePaint
            )
        }

        // Draw points
        val pointPaint = Paint().apply {
            color = lineColor.toArgb()
            strokeWidth = 8f
            style = Paint.Style.FILL
        }

        points.forEach { (x, y) ->
            drawContext.canvas.nativeCanvas.drawCircle(
                x, y, 6f, pointPaint
            )
        }

        // Draw current value and percentage change if provided
        if (data.isNotEmpty() && percentChange != null) {
            val lastValue = data.last().second
            val valuePaint = Paint().apply {
                color = Color.WHITE
                textSize = 35f
                textAlign = Paint.Align.LEFT
                isFakeBoldText = true
            }

            val percentPaint = Paint().apply {
                color = if (percentChange >= 0) Green.toArgb() else Red.toArgb()
                textSize = 25f
                textAlign = Paint.Align.LEFT
            }

            val yPosition = padding + 35f

            // Format the value based on the type
            val displayValue = if (isDdbm) {
                "${lastValue.toInt()}dBm"
            } else {
                if (showPercentage) "${lastValue.toInt()}%" else lastValue.toString()
            }

            drawContext.canvas.nativeCanvas.drawText(
                displayValue,
                padding,
                yPosition,
                valuePaint
            )

            val percentText = if (percentChange >= 0) {
                "This month +${percentChange}%"
            } else {
                "This month ${percentChange}%"
            }

            drawContext.canvas.nativeCanvas.drawText(
                percentText,
                padding,
                yPosition + 30f,
                percentPaint
            )
        }
    }
}

/**
 * Format timestamp to be more readable
 * Converts formats like "12:18:33" to "12:18" or "2023-04-27 12:18:33" to "04/27 12:18"
 */
private fun formatTimestamp(timestamp: String): String {
    return when {
        // Full datetime format: "2023-04-27 12:18:33"
        timestamp.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) -> {
            val parts = timestamp.split(" ")
            val dateParts = parts[0].split("-")
            val timeParts = parts[1].split(":")
            "${dateParts[1]}/${dateParts[2]} ${timeParts[0]}:${timeParts[1]}"
        }
        // Time only format: "12:18:33"
        timestamp.matches(Regex("\\d{2}:\\d{2}:\\d{2}")) -> {
            val parts = timestamp.split(":")
            "${parts[0]}:${parts[1]}"
        }
        // Already formatted or unknown format
        else -> timestamp
    }
}