package com.example.networksignalapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.networksignalapp.ui.theme.Blue
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simplified date range selector component
 */
@Composable
fun DateRangeSelector(
    onApply: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var startMonth by remember { mutableStateOf("") }
    var endMonth by remember { mutableStateOf("") }

    val years = (selectedYear-2..selectedYear+2).map { it.toString() }
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

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
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Date Range",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Close")
                }
            }

            // Year selection
            Column {
                Text(
                    text = "Select Year",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(years) { year ->
                        val isSelected = year == selectedYear.toString()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) Blue else Color.Transparent
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Blue else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedYear = year.toInt() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = year,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Month range selection
            Column {
                Text(
                    text = "Select Month Range",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.height(230.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(months) { month ->
                        val isStartMonth = month == startMonth
                        val isEndMonth = month == endMonth
                        val isInRange = if (startMonth.isNotEmpty() && endMonth.isNotEmpty()) {
                            val startIdx = months.indexOf(startMonth)
                            val endIdx = months.indexOf(endMonth)
                            val currentIdx = months.indexOf(month)

                            currentIdx in startIdx..endIdx
                        } else {
                            false
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.2f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isStartMonth || isEndMonth -> Blue
                                        isInRange -> Blue.copy(alpha = 0.3f)
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    width = 1.dp,
                                    color = when {
                                        isStartMonth || isEndMonth || isInRange -> Blue
                                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (startMonth.isEmpty() || (startMonth.isNotEmpty() && endMonth.isNotEmpty())) {
                                        // Start new selection
                                        startMonth = month
                                        endMonth = ""
                                    } else {
                                        // Complete the selection
                                        val startIdx = months.indexOf(startMonth)
                                        val currentIdx = months.indexOf(month)

                                        if (currentIdx >= startIdx) {
                                            endMonth = month
                                        } else {
                                            // Handle reverse selection
                                            endMonth = startMonth
                                            startMonth = month
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = month,
                                color = when {
                                    isStartMonth || isEndMonth -> Color.White
                                    isInRange -> Blue
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                fontWeight = when {
                                    isStartMonth || isEndMonth -> FontWeight.Bold
                                    else -> FontWeight.Normal
                                }
                            )
                        }
                    }
                }
            }

            // Selection preview
            if (startMonth.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Blue.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "From: $startMonth $selectedYear")

                        Text(
                            text = "To: ${if (endMonth.isEmpty()) "Select end month" else "$endMonth $selectedYear"}"
                        )
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        if (startMonth.isNotEmpty() && endMonth.isNotEmpty()) {
                            val rangeText = "$startMonth - $endMonth $selectedYear"
                            onApply(rangeText)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = startMonth.isNotEmpty() && endMonth.isNotEmpty()
                ) {
                    Text("Apply")
                }
            }
        }
    }
}