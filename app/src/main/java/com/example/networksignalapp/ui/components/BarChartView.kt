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
fun BarChartView(modifier: Modifier = Modifier) {
    // Sample data for the bar chart
    val data = when {
        // For connectivity time per operator
        true -> listOf(
            Pair("Verizon", 1.2f),
            Pair("T-Mobile", 1.5f),
            Pair("AT&T", 0.8f)
        )
        // For other charts, you can add different data sets
        else -> listOf(
            Pair("4G", 65f),
            Pair("3G", 45f),
            Pair("2G", 30f)
        )
    }
    
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
        
        // Draw bars
        val barPaint = Paint().apply {
            color = Blue.toArgb()
            style = Paint.Style.FILL
        }
        
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }
        
        // Calculate bar width and spacing
        val barCount = data.size
        val barWidth = chartWidth / (barCount * 2)
        val spacing = barWidth / 2
        
        // Find the maximum value for scaling
        val maxValue = data.maxOf { it.second }
        
        // Draw bars and labels
        data.forEachIndexed { index, (label, value) ->
            val x = padding + spacing + (index * (barWidth + spacing) * 2)
            val barHeight = (value / maxValue) * chartHeight
            val top = canvasHeight - padding - barHeight
            
            // Draw bar
            drawContext.canvas.nativeCanvas.drawRect(
                x, top,
                x + barWidth, canvasHeight - padding,
                barPaint
            )
            
            // Draw x-axis label
            drawContext.canvas.nativeCanvas.drawText(
                label,
                x + (barWidth / 2),
                canvasHeight - padding + 30f,
                textPaint
            )
        }
    }
}

