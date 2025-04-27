package com.example.networksignalapp.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Enum to track which step of date selection we're in
 */
enum class CalendarMode {
    YEAR, MONTH, DAY
}

/**
 * Simple date range class
 */
data class DateRange(
    val startDate: Date? = null,
    val endDate: Date? = null
)

/**
 * A backward-compatible calendar view for selecting date ranges
 */
@Composable
fun SequentialCalendarView(
    onDateRangeSelected: (DateRange) -> Unit,
    onDismiss: () -> Unit = {}
) {
    var calendarMode by remember { mutableStateOf(CalendarMode.YEAR) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf<Int?>(null) }
    var dateRange by remember { mutableStateOf(DateRange()) }
    var isSelectingEndDate by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with back button when needed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.width(48.dp)) {
                    if (calendarMode != CalendarMode.YEAR) {
                        IconButton(onClick = {
                            when (calendarMode) {
                                CalendarMode.MONTH -> calendarMode = CalendarMode.YEAR
                                CalendarMode.DAY -> {
                                    if (isSelectingEndDate && dateRange.startDate != null) {
                                        isSelectingEndDate = false
                                    } else {
                                        calendarMode = CalendarMode.MONTH
                                    }
                                }
                                else -> {}
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }

                Text(
                    text = when (calendarMode) {
                        CalendarMode.YEAR -> "Select Year"
                        CalendarMode.MONTH -> "Select Month ($selectedYear)"
                        CalendarMode.DAY -> {
                            if (isSelectingEndDate) {
                                "Select End Date"
                            } else {
                                "Select Start Date"
                            }
                        }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Box(Modifier.width(48.dp))
            }

            // Selection status text
            if (dateRange.startDate != null) {
                val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = primaryColor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Selected Range:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "From: ${dateRange.startDate?.let { dateFormatter.format(it) } ?: "Not selected"}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Text(
                                text = "To: ${dateRange.endDate?.let { dateFormatter.format(it) } ?: "Not selected"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Content based on selection mode
            when (calendarMode) {
                CalendarMode.YEAR -> YearSelector(
                    selectedYear = selectedYear,
                    onYearSelected = {
                        selectedYear = it
                        calendarMode = CalendarMode.MONTH
                    }
                )

                CalendarMode.MONTH -> MonthSelector(
                    selectedYear = selectedYear,
                    onMonthSelected = {
                        selectedMonth = it
                        calendarMode = CalendarMode.DAY
                    }
                )

                CalendarMode.DAY -> DaySelector(
                    year = selectedYear,
                    month = selectedMonth!!,
                    selectedStartDate = dateRange.startDate,
                    selectedEndDate = dateRange.endDate,
                    onDateSelected = { selectedDate ->
                        if (!isSelectingEndDate) {
                            // Selecting start date
                            dateRange = DateRange(startDate = selectedDate)
                            isSelectingEndDate = true
                        } else {
                            // Selecting end date
                            val startDate = dateRange.startDate!!

                            // Ensure end date is after start date
                            if (selectedDate.before(startDate)) {
                                dateRange = DateRange(startDate = selectedDate, endDate = startDate)
                            } else {
                                dateRange = dateRange.copy(endDate = selectedDate)
                            }

                            // Both dates selected
                            isSelectingEndDate = false
                        }
                    }
                )
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        if (dateRange.startDate != null && dateRange.endDate != null) {
                            onDateRangeSelected(dateRange)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = dateRange.startDate != null && dateRange.endDate != null
                ) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
fun YearSelector(
    selectedYear: Int,
    onYearSelected: (Int) -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 5..currentYear + 5).toList()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(years) { year ->
            YearItem(
                year = year,
                isSelected = year == selectedYear,
                onYearSelected = onYearSelected
            )
        }
    }
}

@Composable
fun YearItem(
    year: Int,
    isSelected: Boolean,
    onYearSelected: (Int) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) primaryColor else surfaceColor)
            .border(
                width = 1.dp,
                color = if (isSelected) primaryColor else onSurfaceColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onYearSelected(year) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = year.toString(),
            color = if (isSelected) onPrimaryColor else onSurfaceColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

@Composable
fun MonthSelector(
    selectedYear: Int,
    onMonthSelected: (Int) -> Unit
) {
    // Month index in Calendar is 0-based (0 for January)
    val months = listOf(
        Pair("January", 0),
        Pair("February", 1),
        Pair("March", 2),
        Pair("April", 3),
        Pair("May", 4),
        Pair("June", 5),
        Pair("July", 6),
        Pair("August", 7),
        Pair("September", 8),
        Pair("October", 9),
        Pair("November", 10),
        Pair("December", 11)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(months) { (name, index) ->
            MonthItem(
                name = name,
                index = index,
                onMonthSelected = onMonthSelected
            )
        }
    }
}

@Composable
fun MonthItem(
    name: String,
    index: Int,
    onMonthSelected: (Int) -> Unit
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = onSurfaceColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onMonthSelected(index) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.substring(0, 3),
            color = onSurfaceColor,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun DaySelector(
    year: Int,
    month: Int,
    selectedStartDate: Date?,
    selectedEndDate: Date?,
    onDateSelected: (Date) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    // Create calendar for the specified year/month
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1) // Start from the 1st day
    }

    val monthName = SimpleDateFormat("MMMM").format(calendar.time)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Month and year header
        Text(
            text = "$monthName $year",
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceColor.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Create calendar grid with days
        val dayItems = mutableListOf<Date?>()

        // Add empty slots for days before the first day of month
        repeat(firstDayOfWeek) {
            dayItems.add(null)
        }

        // Add days of the month
        for (day in 1..daysInMonth) {
            val date = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            dayItems.add(date)
        }

        // Calculate rows needed
        val rows = (dayItems.size + 6) / 7

        // Fill remaining slots in the last row
        val totalCells = rows * 7
        while (dayItems.size < totalCells) {
            dayItems.add(null)
        }

        // Display days in rows
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 7) {
                    val index = row * 7 + col
                    val date = dayItems.getOrNull(index)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            val calendar = Calendar.getInstance().apply { time = date }
                            val day = calendar.get(Calendar.DAY_OF_MONTH)

                            // Check if date is selected
                            val isStartDate = selectedStartDate != null &&
                                    isSameDay(date, selectedStartDate)
                            val isEndDate = selectedEndDate != null &&
                                    isSameDay(date, selectedEndDate)
                            val isInRange = selectedStartDate != null &&
                                    selectedEndDate != null &&
                                    date.after(selectedStartDate) &&
                                    date.before(selectedEndDate)

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isStartDate || isEndDate -> primaryColor
                                            isInRange -> primaryColor.copy(alpha = 0.3f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = when {
                                            isStartDate || isEndDate || isInRange -> primaryColor
                                            else -> Color.Transparent
                                        },
                                        shape = CircleShape
                                    )
                                    .clickable { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    color = when {
                                        isStartDate || isEndDate -> onPrimaryColor
                                        isInRange -> primaryColor
                                        else -> onSurfaceColor
                                    },
                                    fontWeight = when {
                                        isStartDate || isEndDate -> FontWeight.Bold
                                        else -> FontWeight.Normal
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper to compare if two dates are the same day
private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}