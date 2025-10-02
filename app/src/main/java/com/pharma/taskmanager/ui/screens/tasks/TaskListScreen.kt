package com.pharma.taskmanager.ui.screens.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pharma.taskmanager.data.database.TaskConstants
import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.ui.viewmodel.TaskViewModel
import com.pharma.taskmanager.utils.DateTimeUtils
import kotlinx.coroutines.launch

// Filter options enum
enum class TaskFilter(val displayName: String) {
    ALL("All"),
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    OVERDUE("Overdue"),
    COMPLETED("Completed")
}

// Task group data class
data class TaskGroup(
    val title: String,
    val tasks: List<TaskEntity>,
    val priority: Int // For sorting groups
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(
    onNavigateToCreateTask: () -> Unit,
    onNavigateToTaskDetail: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TaskViewModel = hiltViewModel()
) {
    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenWidth < 600.dp
    val isExpanded = screenWidth >= 840.dp
    
    // State management
    val allTasks by viewModel.allTasks.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Local state
    var selectedFilter by remember { mutableStateOf(TaskFilter.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    
    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    
    // Remember last action for undo functionality
    var lastAction by remember { mutableStateOf<TaskAction?>(null) }
    
    // Filter and group tasks
    val filteredAndGroupedTasks by remember {
        derivedStateOf {
            val currentTime = DateTimeUtils.getCurrentTimestamp()
            
            // Apply search filter
            val searchFiltered = if (searchQuery.isBlank()) {
                allTasks
            } else {
                allTasks.filter { task ->
                    task.title.contains(searchQuery, ignoreCase = true) ||
                    task.description?.contains(searchQuery, ignoreCase = true) == true
                }
            }
            
            // Apply status/date filters
            val filtered = when (selectedFilter) {
                TaskFilter.ALL -> searchFiltered
                TaskFilter.TODAY -> searchFiltered.filter { task ->
                    task.dueDateTime != null && DateTimeUtils.isDueToday(task.dueDateTime) &&
                    task.status == TaskConstants.STATUS_PENDING
                }
                TaskFilter.TOMORROW -> searchFiltered.filter { task ->
                    task.dueDateTime != null && DateTimeUtils.isDueTomorrow(task.dueDateTime) &&
                    task.status == TaskConstants.STATUS_PENDING
                }
                TaskFilter.OVERDUE -> searchFiltered.filter { task ->
                    task.dueDateTime != null && DateTimeUtils.isOverdue(task.dueDateTime) &&
                    task.status == TaskConstants.STATUS_PENDING
                }
                TaskFilter.COMPLETED -> searchFiltered.filter { task ->
                    task.status == TaskConstants.STATUS_COMPLETED
                }
            }
            
            // Group tasks by due date
            if (selectedFilter == TaskFilter.ALL) {
                groupTasksByDueDate(filtered, currentTime)
            } else {
                // For specific filters, show as single group
                listOf(TaskGroup(selectedFilter.displayName, filtered, 0))
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    if (showSearch) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search tasks...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { 
                                    showSearch = false
                                    searchQuery = ""
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        )
                    } else {
                        Text("Tasks (${allTasks.size})")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!showSearch) {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        // Filter button removed per request
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTask
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Task")
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter chips - responsive layout
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = if (isExpanded) 24.dp else if (isCompact) 8.dp else 16.dp, 
                        vertical = if (isCompact) 6.dp else 8.dp
                    ),
                horizontalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
            ) {
                items(TaskFilter.values()) { filter ->
                    FilterChip(
                        onClick = { selectedFilter = filter },
                        label = { 
                            Text(
                                text = filter.displayName,
                                style = if (isCompact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
                            )
                        },
                        selected = selectedFilter == filter,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .height(if (isCompact) 28.dp else 32.dp)
                    )
                }
            }
            
            // Content based on state
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Loading tasks...")
                    }
                }
                
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error loading tasks",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = error ?: "Unknown error",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                
                filteredAndGroupedTasks.all { it.tasks.isEmpty() } -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isNotBlank()) {
                                    "No tasks found for \"$searchQuery\""
                                } else {
                                    when (selectedFilter) {
                                        TaskFilter.ALL -> "No tasks yet. Create your first task!"
                                        TaskFilter.TODAY -> "No tasks due today"
                                        TaskFilter.TOMORROW -> "No tasks due tomorrow"
                                        TaskFilter.OVERDUE -> "No overdue tasks"
                                        TaskFilter.COMPLETED -> "No completed tasks"
                                    }
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = if (isExpanded) 24.dp else if (isCompact) 8.dp else 16.dp,
                            vertical = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(if (isCompact) 6.dp else 8.dp)
                    ) {
                        filteredAndGroupedTasks.forEach { group ->
                            if (group.tasks.isNotEmpty()) {
                                // Sticky header for each group
                                stickyHeader {
                                    GroupHeader(
                                        title = group.title,
                                        taskCount = group.tasks.size
                                    )
                                }
                                
                                // Tasks in this group
                                items(
                                    items = group.tasks,
                                    key = { task -> task.id }
                                ) { task ->
                                    TaskItem(
                                        task = task,
                                        onTaskClick = { onNavigateToTaskDetail(task.id) },
                                        onToggleComplete = { 
                                            lastAction = TaskAction.ToggleComplete(task, task.status)
                                            viewModel.toggleTaskCompletion(task.id, task.status)
                                            
                                            // Show snackbar with undo
                                            coroutineScope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = if (task.status == TaskConstants.STATUS_PENDING) {
                                                        "Task marked as completed"
                                                    } else {
                                                        "Task marked as pending"
                                                    },
                                                    actionLabel = "Undo",
                                                    duration = SnackbarDuration.Short
                                                )
                                                
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    // Undo the action
                                                    lastAction?.let { action ->
                                                        when (action) {
                                                            is TaskAction.ToggleComplete -> {
                                                                viewModel.toggleTaskCompletion(action.task.id, action.originalStatus)
                                                            }
                                                            is TaskAction.Delete -> {
                                                                viewModel.createTask(
                                                                    title = action.task.title,
                                                                    description = action.task.description,
                                                                    dueDateTime = action.task.dueDateTime,
                                                                    priority = action.task.priority,
                                                                    reminderTime = action.task.reminderTime
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                        onDeleteTask = {
                                            lastAction = TaskAction.Delete(task)
                                            viewModel.deleteTask(task)
                                            
                                            // Show snackbar with undo
                                            coroutineScope.launch {
                                                val result = snackbarHostState.showSnackbar(
                                                    message = "Task deleted",
                                                    actionLabel = "Undo",
                                                    duration = SnackbarDuration.Short
                                                )
                                                
                                                if (result == SnackbarResult.ActionPerformed) {
                                                    // Recreate the deleted task
                                                    viewModel.createTask(
                                                        title = task.title,
                                                        description = task.description,
                                                        dueDateTime = task.dueDateTime,
                                                        priority = task.priority,
                                                        reminderTime = task.reminderTime
                                                    )
                                                }
                                            }
                                        },
                                        modifier = Modifier.animateItemPlacement()
                                    )
                                }
                            }
                        }
                        
                        // Add some bottom padding for FAB
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

// Sealed class for tracking actions that can be undone
sealed class TaskAction {
    data class ToggleComplete(val task: TaskEntity, val originalStatus: String) : TaskAction()
    data class Delete(val task: TaskEntity) : TaskAction()
}

@Composable
private fun GroupHeader(
    title: String,
    taskCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "$taskCount tasks",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TaskItem(
    task: TaskEntity,
    onTaskClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDeleteTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Get screen configuration for responsive design
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val isCompact = screenWidth < 600.dp
    val isExpanded = screenWidth >= 840.dp
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = if (isExpanded) 24.dp else if (isCompact) 8.dp else 16.dp,
                vertical = if (isCompact) 3.dp else 4.dp
            )
            .clickable { onTaskClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (task.status == TaskConstants.STATUS_COMPLETED) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 12.dp else 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Completion checkbox
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)
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
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Task content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (task.status == TaskConstants.STATUS_COMPLETED) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (task.status == TaskConstants.STATUS_COMPLETED) {
                        TextDecoration.LineThrough
                    } else {
                        TextDecoration.None
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Description (if exists)
                task.description?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                // Due date and priority row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Due date
                    task.dueDateTime?.let { dueTime ->
                        val isOverdue = DateTimeUtils.isOverdue(dueTime) && 
                                      task.status == TaskConstants.STATUS_PENDING
                        
                        Text(
                            text = when {
                                DateTimeUtils.isDueToday(dueTime) -> "Due today"
                                DateTimeUtils.isDueTomorrow(dueTime) -> "Due tomorrow"
                                isOverdue -> "Overdue"
                                else -> "Due ${DateTimeUtils.formatDate(dueTime)}"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = when {
                                isOverdue -> Color(0xFFF44336)
                                DateTimeUtils.isDueToday(dueTime) -> Color(0xFFFF9800)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Priority indicator
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
                }
            }
            
            // Quick actions
            Column {
                IconButton(
                    onClick = onDeleteTask,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete task",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Helper function to group tasks by due date
private fun groupTasksByDueDate(tasks: List<TaskEntity>, currentTime: Long): List<TaskGroup> {
    val groups = mutableListOf<TaskGroup>()
    
    // Separate tasks by categories
    val overdueTasks = mutableListOf<TaskEntity>()
    val todayTasks = mutableListOf<TaskEntity>()
    val tomorrowTasks = mutableListOf<TaskEntity>()
    val futureTasks = mutableListOf<TaskEntity>()
    val completedTasks = mutableListOf<TaskEntity>()
    val noDueDateTasks = mutableListOf<TaskEntity>()
    
    tasks.forEach { task ->
        when {
            task.status == TaskConstants.STATUS_COMPLETED -> completedTasks.add(task)
            task.dueDateTime == null -> noDueDateTasks.add(task)
            DateTimeUtils.isOverdue(task.dueDateTime) -> overdueTasks.add(task)
            DateTimeUtils.isDueToday(task.dueDateTime) -> todayTasks.add(task)
            DateTimeUtils.isDueTomorrow(task.dueDateTime) -> tomorrowTasks.add(task)
            else -> futureTasks.add(task)
        }
    }
    
    // Add groups in priority order
    if (overdueTasks.isNotEmpty()) {
        groups.add(TaskGroup("Overdue", overdueTasks.sortedBy { it.dueDateTime }, 0))
    }
    if (todayTasks.isNotEmpty()) {
        groups.add(TaskGroup("Today", todayTasks.sortedBy { it.dueDateTime }, 1))
    }
    if (tomorrowTasks.isNotEmpty()) {
        groups.add(TaskGroup("Tomorrow", tomorrowTasks.sortedBy { it.dueDateTime }, 2))
    }
    if (futureTasks.isNotEmpty()) {
        groups.add(TaskGroup("Upcoming", futureTasks.sortedBy { it.dueDateTime }, 3))
    }
    if (noDueDateTasks.isNotEmpty()) {
        groups.add(TaskGroup("No Due Date", noDueDateTasks.sortedBy { it.title }, 4))
    }
    if (completedTasks.isNotEmpty()) {
        groups.add(TaskGroup("Completed", completedTasks.sortedByDescending { it.dueDateTime }, 5))
    }
    
    return groups
}