package com.example.networksignalapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.networksignalapp.ui.theme.Blue
import java.util.*
import java.text.SimpleDateFormat

@Composable
fun CalendarView(
    onApply: () -> Unit
) {
    var selectedDate by remember { mutableStateOf<Date?>(Date()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select a date range:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            MonthCalendar(
                year = 2023,
                month = Calendar.JANUARY,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            MonthCalendar(
                year = 2023,
                month = Calendar.FEBRUARY,
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it }
            )

            Button(
                onClick = onApply,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Apply")
            }
        }
    }
}

@Composable
fun MonthCalendar(
    year: Int,
    month: Int,
    selectedDate: Date?,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month)

    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        // Month and year header
        Text(
            text = "$monthName $year",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week header
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Create calendar grid
        var dayCounter = 1
        val rows = (daysInMonth + firstDayOfWeek + 6) / 7 // Calculate number of rows needed

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    if (row == 0 && col < firstDayOfWeek || dayCounter > daysInMonth) {
                        // Empty cell
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        // Create date for this cell
                        val date = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, dayCounter)
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.time

                        // Check if this date is selected
                        val isSelected = selectedDate?.let { sel ->
                            val selCal = Calendar.getInstance().apply { time = sel }
                            selCal.get(Calendar.YEAR) == year &&
                                    selCal.get(Calendar.MONTH) == month &&
                                    selCal.get(Calendar.DAY_OF_MONTH) == dayCounter
                        } ?: false

                        // Date cell
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .border(
                                    width = if (isSelected) 0.dp else 1.dp,
                                    color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayCounter.toString(),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        dayCounter++
                    }
                }
            }
        }
    }
}