package com.pharma.taskmanager.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import com.pharma.taskmanager.data.database.TaskConstants
import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.ui.viewmodel.TaskViewModel
import com.pharma.taskmanager.utils.DateTimeUtils
import com.pharma.taskmanager.ui.components.DateTimePickerDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    var task by remember { mutableStateOf<TaskEntity?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current
    
    // Local state for dialogs and actions
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReminderUpdateDialog by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableStateOf(0) }
    
    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Load task when screen is created or when refresh is triggered
    LaunchedEffect(taskId, refreshTrigger) {
        task = viewModel.getTaskById(taskId)
        // Also check for overdue reminders when screen loads
        viewModel.checkAllOverdueReminders()
        // Stop any ongoing persistent reminder when the task view is opened
        try {
            val stopIntent = android.content.Intent(context, com.pharma.taskmanager.services.PersistentReminderService::class.java).apply {
                action = "STOP_REMINDER"
            }
            context.startService(stopIntent)
        } catch (_: Exception) {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = task?.title ?: "Task Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (task != null) {
                        // Toggle completion action
                        IconButton(
                            onClick = { 
                                task?.let { currentTask ->
                                    viewModel.toggleTaskCompletion(currentTask.id, currentTask.status)
                                    
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = if (currentTask.status == TaskConstants.STATUS_PENDING) {
                                                "Task marked as completed"
                                            } else {
                                                "Task marked as pending"
                                            }
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (task?.status == TaskConstants.STATUS_COMPLETED) {
                                    Icons.Default.Undo
                                } else {
                                    Icons.Default.CheckCircle
                                },
                                contentDescription = if (task?.status == TaskConstants.STATUS_COMPLETED) {
                                    "Mark as pending"
                                } else {
                                    "Mark as completed"
                                },
                                tint = if (task?.status == TaskConstants.STATUS_COMPLETED) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    Color(0xFF4CAF50)
                                }
                            )
                        }
                        

                        
                        // Delete action
                        IconButton(
                            onClick = { showDeleteDialog = true }
                        ) {
                            Icon(
                                Icons.Default.Delete, 
                                contentDescription = "Delete Task",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (task != null) {
                FloatingActionButton(
                    onClick = { onNavigateToEdit(taskId) }
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Task")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading task details...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error loading task",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            task == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.TaskAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Task not found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "This task may have been deleted",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            else -> {
                TaskDetailContent(
                    task = task!!,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    onToggleComplete = { 
                        viewModel.toggleTaskCompletion(taskId, task!!.status)
                        task = task?.copy(
                            status = if (task!!.status == TaskConstants.STATUS_PENDING) {
                                TaskConstants.STATUS_COMPLETED
                            } else {
                                TaskConstants.STATUS_PENDING
                            }
                        )
                    },
                    onUpdateReminder = { showReminderUpdateDialog = true },
                    onClearReminder = {
                        // Clear reminder by updating the task and cancelling schedules
                        task?.let { currentTask ->
                            coroutineScope.launch {
                                viewModel.updateTaskReminder(currentTask, null)
                                // Refresh UI
                                refreshTrigger++
                                snackbarHostState.showSnackbar("Reminder removed")
                            }
                        }
                    }
                )
            }
        }
        
        // Delete confirmation dialog
        if (showDeleteDialog) {
            DeleteTaskDialog(
                onConfirm = {
                    task?.let { viewModel.deleteTask(it) }
                    showDeleteDialog = false
                    onNavigateBack()
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
        
        // Reminder update dialog
        if (showReminderUpdateDialog) {
            ReminderUpdateDialog(
                currentReminder = task?.reminderTime,
                onConfirm = { newReminderTime ->
                    task?.let { currentTask ->
                        coroutineScope.launch {
                            viewModel.updateTaskReminder(currentTask, newReminderTime)
                            showReminderUpdateDialog = false
                            refreshTrigger++ // Trigger task reload
                            
                            val message = if (newReminderTime != null) {
                                "Reminder updated for ${DateTimeUtils.formatDateTime(newReminderTime)}"
                            } else {
                                "Reminder removed"
                            }
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                },
                onDismiss = { showReminderUpdateDialog = false }
            )
        }
    }
}

@Composable
private fun TaskDetailContent(
    task: TaskEntity,
    modifier: Modifier = Modifier,
    onToggleComplete: () -> Unit,
    onUpdateReminder: () -> Unit,
    onClearReminder: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Task Header with Status
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (task.status == TaskConstants.STATUS_COMPLETED) {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Status row with completion toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onToggleComplete,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (task.status == TaskConstants.STATUS_COMPLETED) {
                                    Icons.Default.CheckCircle
                                } else {
                                    Icons.Default.RadioButtonUnchecked
                                },
                                contentDescription = if (task.status == TaskConstants.STATUS_COMPLETED) {
                                    "Mark as pending"
                                } else {
                                    "Mark as completed"
                                },
                                tint = if (task.status == TaskConstants.STATUS_COMPLETED) {
                                    Color(0xFF4CAF50)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = if (task.status == TaskConstants.STATUS_COMPLETED) "Completed" else "Pending",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (task.status == TaskConstants.STATUS_COMPLETED) {
                                Color(0xFF4CAF50)
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Priority indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(
                                    when (task.priority) {
                                        TaskConstants.PRIORITY_HIGH -> Color(0xFFF44336)
                                        TaskConstants.PRIORITY_MEDIUM -> Color(0xFFFF9800)
                                        TaskConstants.PRIORITY_LOW -> Color(0xFF4CAF50)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (task.priority) {
                                TaskConstants.PRIORITY_HIGH -> "High Priority"
                                TaskConstants.PRIORITY_MEDIUM -> "Medium Priority"
                                TaskConstants.PRIORITY_LOW -> "Low Priority"
                                else -> "Normal Priority"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Task title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (task.status == TaskConstants.STATUS_COMPLETED) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (task.status == TaskConstants.STATUS_COMPLETED) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    }
                )
            }
        }

        // Description Section
        if (!task.description.isNullOrBlank()) {
            DetailSection(
                title = "Description",
                icon = Icons.Default.Description
            ) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
        }

        // Due Date Section
        if (task.dueDateTime != null) {
            DetailSection(
                title = "Due Date & Time",
                icon = Icons.Default.Schedule
            ) {
                val isOverdue = DateTimeUtils.isOverdue(task.dueDateTime) && 
                               task.status == TaskConstants.STATUS_PENDING
                
                Column {
                    Text(
                        text = DateTimeUtils.formatDateTime(task.dueDateTime),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isOverdue) {
                            Color(0xFFF44336)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = if (isOverdue) FontWeight.Medium else FontWeight.Normal
                    )
                    
                    if (isOverdue) {
                        Text(
                            text = "⚠️ This task is overdue",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFF44336),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else if (DateTimeUtils.isDueToday(task.dueDateTime)) {
                        Text(
                            text = "📅 Due today",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFFF9800),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else if (DateTimeUtils.isDueTomorrow(task.dueDateTime)) {
                        Text(
                            text = "📅 Due tomorrow",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // Reminder Section
        if (task.reminderTime != null) {
            DetailSection(
                title = "Reminder",
                icon = Icons.Default.NotificationsActive
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = DateTimeUtils.formatDateTime(task.reminderTime),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (task.reminderTime > DateTimeUtils.getCurrentTimestamp()) {
                                "Reminder set"
                            } else {
                                "Reminder passed"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    
                    Row {
                        TextButton(onClick = onUpdateReminder) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Update")
                        }
                        TextButton(
                            onClick = onClearReminder,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Clear")
                        }
                    }
                }
            }
        }

        // Creation Info Section
        DetailSection(
            title = "Created",
            icon = Icons.Default.Info
        ) {
            Text(
                text = DateTimeUtils.formatDateTime(task.createdAt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Add some bottom spacing for FAB
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun DetailSection(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            content()
        }
    }
}

@Composable
private fun DeleteTaskDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Delete Task",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete this task? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ReminderUpdateDialog(
    currentReminder: Long?,
    onConfirm: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedDateTime by remember { 
        mutableStateOf(currentReminder ?: DateTimeUtils.getCurrentTimestamp()) 
    }
    var showDateTimePicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.NotificationAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Update Reminder",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = "Set a new reminder time for this task:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Current reminder display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Current reminder:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (currentReminder != null) {
                                DateTimeUtils.formatDateTime(currentReminder)
                            } else {
                                "No reminder set"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // New reminder selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "New reminder:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = DateTimeUtils.formatDateTime(selectedDateTime),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            TextButton(
                                onClick = { showDateTimePicker = true }
                            ) {
                                Text("Change")
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Remove reminder option
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onConfirm(null) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.NotificationsOff,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remove Reminder")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedDateTime) }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
    // Date/Time Picker Dialog
    if (showDateTimePicker) {
        DateTimePickerDialog(
            initialDateTime = selectedDateTime,
            onDateTimeSelected = { newDateTime ->
                selectedDateTime = newDateTime
                showDateTimePicker = false
            },
            onDismiss = { showDateTimePicker = false }
        )
    }
}