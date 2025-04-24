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
import com.example.networksignalapp.ui.theme.Green
import com.example.networksignalapp.ui.theme.Red

@Composable
fun BarChartView(
    modifier: Modifier = Modifier,
    data: List<Pair<String, Float>>,
    maxValue: Float? = null,
    barColor: androidx.compose.ui.graphics.Color = Blue,
    showPercentage: Boolean = false,
    animate: Boolean = false
) {
    val calculatedMaxValue = maxValue ?: data.maxOfOrNull { it.second } ?: 100f

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
            color = barColor.toArgb()
            style = Paint.Style.FILL
        }

        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 30f
            textAlign = Paint.Align.CENTER
        }

        val valuePaint = Paint().apply {
            color = Color.WHITE
            textSize = 25f
            textAlign = Paint.Align.CENTER
        }

        // Calculate bar width and spacing
        val barCount = data.size
        val barWidth = chartWidth / (barCount * 2)
        val spacing = barWidth / 2

        // Draw grid lines
        val gridPaint = Paint().apply {
            color = Color.DKGRAY
            strokeWidth = 1f
            style = Paint.Style.STROKE
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
            val gridValue = (i * calculatedMaxValue) / 4
            drawContext.canvas.nativeCanvas.drawText(
                if (showPercentage) "${gridValue.toInt()}%" else gridValue.toInt().toString(),
                padding - 25f,
                y + 10f,
                valuePaint
            )
        }

        // Draw bars and labels
        data.forEachIndexed { index, (label, value) ->
            val x = padding + spacing + (index * (barWidth + spacing) * 2)
            val barHeight = (value / calculatedMaxValue) * chartHeight
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

            // Draw value above bar
            drawContext.canvas.nativeCanvas.drawText(
                if (showPercentage) "${value.toInt()}%" else value.toString(),
                x + (barWidth / 2),
                top - 10f,
                valuePaint
            )
        }
    }
}