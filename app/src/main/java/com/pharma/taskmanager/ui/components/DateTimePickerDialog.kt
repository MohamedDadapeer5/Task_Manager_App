package com.pharma.taskmanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pharma.taskmanager.utils.DateTimeUtils
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    onDateTimeSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    initialDateTime: Long? = null
) {
    val calendar = Calendar.getInstance()
    initialDateTime?.let { calendar.timeInMillis = it }
    
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableIntStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf(calendar.get(Calendar.MINUTE)) }
    
    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isCompact = screenWidth < 600.dp
    val isLandscape = screenWidth > screenHeight
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(if (isCompact) 0.95f else 0.8f)
                .fillMaxHeight(if (isLandscape) 0.95f else 0.8f)
                .padding(if (isCompact) 8.dp else 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(if (isCompact) 12.dp else 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Date & Time",
                    style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = if (isCompact) 12.dp else 16.dp)
                )
                
                // Date input fields
                Text(
                    text = "Date Selection",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Date input fields - responsive layout
                if (isLandscape && !isCompact) {
                    // Landscape layout - horizontal arrangement
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        DateInputFields(
                            selectedYear = selectedYear,
                            selectedMonth = selectedMonth,
                            selectedDay = selectedDay,
                            onYearChange = { selectedYear = it },
                            onMonthChange = { selectedMonth = it },
                            onDayChange = { selectedDay = it },
                            isCompact = isCompact
                        )
                    }
                } else {
                    // Portrait or compact layout - grid arrangement
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DateInputFields(
                                selectedYear = selectedYear,
                                selectedMonth = selectedMonth,
                                selectedDay = selectedDay,
                                onYearChange = { selectedYear = it },
                                onMonthChange = { selectedMonth = it },
                                onDayChange = { selectedDay = it },
                                isCompact = isCompact,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                
                // Quick date selection - responsive layout
                Text(
                    text = "Quick Selection",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (isCompact) {
                    // Compact layout - vertical arrangement
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            QuickDateButton(
                                text = "Today",
                                onClick = {
                                    val today = Calendar.getInstance()
                                    selectedYear = today.get(Calendar.YEAR)
                                    selectedMonth = today.get(Calendar.MONTH)
                                    selectedDay = today.get(Calendar.DAY_OF_MONTH)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            QuickDateButton(
                                text = "Tomorrow",
                                onClick = {
                                    val tomorrow = Calendar.getInstance()
                                    tomorrow.add(Calendar.DAY_OF_MONTH, 1)
                                    selectedYear = tomorrow.get(Calendar.YEAR)
                                    selectedMonth = tomorrow.get(Calendar.MONTH)
                                    selectedDay = tomorrow.get(Calendar.DAY_OF_MONTH)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        QuickDateButton(
                            text = "Next Week",
                            onClick = {
                                val nextWeek = Calendar.getInstance()
                                nextWeek.add(Calendar.WEEK_OF_YEAR, 1)
                                selectedYear = nextWeek.get(Calendar.YEAR)
                                selectedMonth = nextWeek.get(Calendar.MONTH)
                                selectedDay = nextWeek.get(Calendar.DAY_OF_MONTH)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    // Regular layout - horizontal arrangement
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        QuickDateButton(
                            text = "Today",
                            onClick = {
                                val today = Calendar.getInstance()
                                selectedYear = today.get(Calendar.YEAR)
                                selectedMonth = today.get(Calendar.MONTH)
                                selectedDay = today.get(Calendar.DAY_OF_MONTH)
                            }
                        )
                        QuickDateButton(
                            text = "Tomorrow",
                            onClick = {
                                val tomorrow = Calendar.getInstance()
                                tomorrow.add(Calendar.DAY_OF_MONTH, 1)
                                selectedYear = tomorrow.get(Calendar.YEAR)
                                selectedMonth = tomorrow.get(Calendar.MONTH)
                                selectedDay = tomorrow.get(Calendar.DAY_OF_MONTH)
                            }
                        )
                        QuickDateButton(
                            text = "Next Week",
                            onClick = {
                                val nextWeek = Calendar.getInstance()
                                nextWeek.add(Calendar.WEEK_OF_YEAR, 1)
                                selectedYear = nextWeek.get(Calendar.YEAR)
                                selectedMonth = nextWeek.get(Calendar.MONTH)
                                selectedDay = nextWeek.get(Calendar.DAY_OF_MONTH)
                            }
                        )
                    }
                }
                
                // Time selection - responsive layout
                Text(
                    text = "Time",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                TimeSelectionSection(
                    selectedHour = selectedHour,
                    selectedMinute = selectedMinute,
                    onHourChange = { selectedHour = it },
                    onMinuteChange = { selectedMinute = it },
                    isCompact = isCompact,
                    modifier = Modifier.padding(bottom = if (isCompact) 12.dp else 16.dp)
                )
                
                // Preview
                val previewCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                
                Text(
                    text = "Selected: ${DateTimeUtils.formatDateTime(previewCalendar.timeInMillis)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Action buttons - responsive layout
                if (isCompact) {
                    // Compact layout - full width buttons stacked
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                onDateTimeSelected(previewCalendar.timeInMillis)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Select")
                        }
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel")
                        }
                    }
                } else {
                    // Regular layout - horizontal arrangement
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onDateTimeSelected(previewCalendar.timeInMillis)
                            }
                        ) {
                            Text("Select")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateInputFields(
    selectedYear: Int,
    selectedMonth: Int,
    selectedDay: Int,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit,
    onDayChange: (Int) -> Unit,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    val fieldWidth = if (isCompact) 70.dp else 80.dp
    val fieldSpacing = if (isCompact) 6.dp else 8.dp
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(fieldSpacing),
        modifier = modifier
    ) {
        // Year
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("Year", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = selectedYear.toString(),
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(4)
                    if (digits.isNotEmpty()) {
                        val parsed = digits.toIntOrNull()
                        if (parsed != null && parsed >= 2020 && parsed <= 2030) {
                            onYearChange(parsed)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
        
        // Month
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("Month", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = (selectedMonth + 1).toString().padStart(2, '0'),
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(2)
                    if (digits.isNotEmpty()) {
                        val parsed = digits.toIntOrNull()
                        if (parsed != null && parsed in 1..12) {
                            onMonthChange(parsed - 1) // Calendar months are 0-based
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
        
        // Day
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text("Day", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = selectedDay.toString().padStart(2, '0'),
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(2)
                    if (digits.isNotEmpty()) {
                        val parsed = digits.toIntOrNull()
                        if (parsed != null && parsed in 1..31) {
                            onDayChange(parsed)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
private fun QuickDateButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TimeSelectionSection(
    selectedHour: Int,
    selectedMinute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(if (isCompact) 8.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // Hour selection
        TimeInputField(
            label = "Hour",
            value = selectedHour,
            range = 0..23,
            onValueChange = onHourChange,
            isCompact = isCompact,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = ":",
            style = if (isCompact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge
        )
        
        // Minute selection
        TimeInputField(
            label = "Minute",
            value = selectedMinute,
            range = 0..59,
            onValueChange = onMinuteChange,
            isCompact = isCompact,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TimeInputField(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    isCompact: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Decrement button
            IconButton(
                onClick = { 
                    val newValue = if (value == range.first) range.last else value - 1
                    onValueChange(newValue)
                },
                modifier = Modifier.size(if (isCompact) 36.dp else 40.dp)
            ) {
                Text("-", style = MaterialTheme.typography.titleMedium)
            }

            // Value input field
            OutlinedTextField(
                value = value.toString().padStart(2, '0'),
                onValueChange = { input ->
                    val digits = input.filter { it.isDigit() }.take(2)
                    if (digits.isNotEmpty()) {
                        val parsed = digits.toIntOrNull()
                        if (parsed != null && parsed in range) {
                            onValueChange(parsed)
                        }
                    }
                },
                modifier = Modifier.width(if (isCompact) 60.dp else 72.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleMedium,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            // Increment button
            IconButton(
                onClick = { 
                    val newValue = if (value == range.last) range.first else value + 1
                    onValueChange(newValue)
                },
                modifier = Modifier.size(if (isCompact) 36.dp else 40.dp)
            ) {
                Text("+", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}