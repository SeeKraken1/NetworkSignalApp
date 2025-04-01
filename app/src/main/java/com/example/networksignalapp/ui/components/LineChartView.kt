package com.example.networksignalapp.ui.components

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.networksignalapp.ui.theme.Blue

@Composable
fun LineChartView(modifier: Modifier = Modifier) {
    // Sample data for signal strength over time
    val data = listOf(
        Pair("1/1", -110f),
        Pair("1/15", -105f),
        Pair("1/30", -106f)
    )
    
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
        
        // Draw data points and lines
        val pointPaint = Paint().apply {
            color = Blue.toArgb()
            strokeWidth = 8f
            style = Paint.Style.FILL
        }
        
        val linePaint = Paint().apply {
            color = Blue.toArgb()
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }
        
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }
        
        // Calculate x and y positions
        val xStep = chartWidth / (data.size - 1)
        val minValue = -120f // Minimum dBm value
        val maxValue = -50f  // Maximum dBm value
        val valueRange = maxValue - minValue
        
        val points = data.mapIndexed { index, (label, value) ->
            val x = padding + (index * xStep)
            val normalizedValue = (value - minValue) / valueRange
            val y = canvasHeight - padding - (normalizedValue * chartHeight)
            
            // Draw x-axis labels
            drawContext.canvas.nativeCanvas.drawText(
                label,
                x,
                canvasHeight - padding + 30f,
                textPaint
            )
            
            Pair(x, y)
        }
        
        // Draw lines between points
        for (i in 0 until points.size - 1) {
            drawContext.canvas.nativeCanvas.drawLine(
                points[i].first, points[i].second,
                points[i + 1].first, points[i + 1].second,
                linePaint
            )
        }
        
        // Draw points
        points.forEach { (x, y) ->
            drawContext.canvas.nativeCanvas.drawCircle(
                x, y, 6f, pointPaint
            )
        }
    }
}

