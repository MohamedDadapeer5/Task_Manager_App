package com.pharma.taskmanager.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pharma.taskmanager.data.database.TaskConstants
import com.pharma.taskmanager.ui.components.DateTimePickerDialog
import com.pharma.taskmanager.ui.viewmodel.TaskViewModel
import com.pharma.taskmanager.utils.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreateScreen(
    onNavigateBack: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenWidth < 600.dp
    val isExpanded = screenWidth >= 840.dp
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskConstants.PRIORITY_MEDIUM) }
    var dueDateTime by remember { mutableStateOf<Long?>(null) }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    
    // Dialog states
    var showDatePicker by remember { mutableStateOf(false) }
    var showReminderPicker by remember { mutableStateOf(false) }
    
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (title.isNotBlank()) {
                        viewModel.createTask(
                            title = title,
                            description = description.takeIf { it.isNotBlank() },
                            dueDateTime = dueDateTime,
                            priority = priority,
                            reminderTime = reminderTime
                        )
                        onNavigateBack()
                    }
                }
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Task")
            }
        }
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(
                    horizontal = if (isExpanded) 48.dp else if (isCompact) 12.dp else 16.dp,
                    vertical = if (isCompact) 12.dp else 16.dp
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(if (isCompact) 12.dp else 16.dp)
        ) {
            
            // Error display
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = "Error: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                isError = title.isBlank()
            )
            
            // Description Input
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                minLines = 3,
                maxLines = 5
            )
            
            // Priority Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Priority",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Column(Modifier.selectableGroup()) {
                        PriorityOption(
                            text = "High Priority",
                            selected = priority == TaskConstants.PRIORITY_HIGH,
                            onSelect = { priority = TaskConstants.PRIORITY_HIGH }
                        )
                        PriorityOption(
                            text = "Medium Priority",
                            selected = priority == TaskConstants.PRIORITY_MEDIUM,
                            onSelect = { priority = TaskConstants.PRIORITY_MEDIUM }
                        )
                        PriorityOption(
                            text = "Low Priority",
                            selected = priority == TaskConstants.PRIORITY_LOW,
                            onSelect = { priority = TaskConstants.PRIORITY_LOW }
                        )
                    }
                }
            }
            
            // Due Date Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Due Date",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = { showDatePicker = true }
                        ) {
                            Icon(
                                Icons.Default.DateRange, 
                                contentDescription = "Select Date",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text("Select Date")
                        }
                    }
                    
                    dueDateTime?.let { dateTime ->
                        Text(
                            text = "Due: ${DateTimeUtils.formatDateTime(dateTime)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        TextButton(
                            onClick = { dueDateTime = null }
                        ) {
                            Text("Remove Due Date")
                        }
                    }
                }
            }
            
            // Reminder Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reminder",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(
                            onClick = { showReminderPicker = true }
                        ) {
                            Icon(
                                Icons.Default.Notifications, 
                                contentDescription = "Set Reminder",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text("Set Reminder")
                        }
                    }
                    
                    reminderTime?.let { time ->
                        Text(
                            text = "Reminder: ${DateTimeUtils.formatDateTime(time)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        TextButton(
                            onClick = { reminderTime = null }
                        ) {
                            Text("Remove Reminder")
                        }
                    }
                }
            }
            
            // Save Instructions
            if (title.isBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Please enter a task title to save",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        
        // Date picker dialog
        if (showDatePicker) {
            DateTimePickerDialog(
                onDateTimeSelected = { selectedDateTime ->
                    dueDateTime = selectedDateTime
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false },
                initialDateTime = dueDateTime
            )
        }
        
        // Reminder picker dialog
        if (showReminderPicker) {
            DateTimePickerDialog(
                onDateTimeSelected = { selectedDateTime ->
                    reminderTime = selectedDateTime
                    showReminderPicker = false
                },
                onDismiss = { showReminderPicker = false },
                initialDateTime = reminderTime
            )
        }
    }
}

@Composable
private fun PriorityOption(
    text: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}