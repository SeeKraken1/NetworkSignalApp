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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import com.example.networksignalapp.ui.theme.Blue
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple, crash-proof date range selector
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    onApply: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear-2..currentYear+2).map { it.toString() }

    var selectedStartMonth by remember { mutableStateOf("") }
    var selectedEndMonth by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf(currentYear.toString()) }

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

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
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
                        val isSelected = year == selectedYear

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
                                .clickable { selectedYear = year },
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
                        val isStartMonth = month == selectedStartMonth
                        val isEndMonth = month == selectedEndMonth
                        val isInRange = if (selectedStartMonth.isNotEmpty() && selectedEndMonth.isNotEmpty()) {
                            val startIdx = months.indexOf(selectedStartMonth)
                            val endIdx = months.indexOf(selectedEndMonth)
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
                                    if (selectedStartMonth.isEmpty() || (selectedStartMonth.isNotEmpty() && selectedEndMonth.isNotEmpty())) {
                                        // Start new selection
                                        selectedStartMonth = month
                                        selectedEndMonth = ""
                                    } else {
                                        // Complete the selection
                                        val startIdx = months.indexOf(selectedStartMonth)
                                        val currentIdx = months.indexOf(month)

                                        if (currentIdx >= startIdx) {
                                            selectedEndMonth = month
                                        } else {
                                            // Handle reverse selection
                                            selectedEndMonth = selectedStartMonth
                                            selectedStartMonth = month
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
            if (selectedStartMonth.isNotEmpty()) {
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
                        Text(text = "From: $selectedStartMonth $selectedYear")

                        Text(
                            text = "To: ${if (selectedEndMonth.isEmpty()) "Select end month" else "$selectedEndMonth $selectedYear"}"
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
                        if (selectedStartMonth.isNotEmpty() && selectedEndMonth.isNotEmpty()) {
                            val rangeText = "$selectedStartMonth - $selectedEndMonth $selectedYear"
                            onApply(rangeText)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = selectedStartMonth.isNotEmpty() && selectedEndMonth.isNotEmpty()
                ) {
                    Text("Apply")
                }
            }
        }
    }
}