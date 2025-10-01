package com.pharma.taskmanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pharma.taskmanager.data.database.TaskConstants
import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.domain.usecase.TaskUseCases
import com.pharma.taskmanager.utils.DateTimeUtils
import com.pharma.taskmanager.utils.ReminderScheduler
import com.pharma.taskmanager.utils.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskUseCases: TaskUseCases,
    private val reminderScheduler: ReminderScheduler,
    private val notificationHelper: NotificationHelper
) : ViewModel() {
    
    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Filter state
    private val _currentFilter = MutableStateFlow<String?>(null)
    val currentFilter: StateFlow<String?> = _currentFilter.asStateFlow()
    
    // Tasks flows
    val allTasks = taskUseCases.getTasks()
    val pendingTasks = taskUseCases.getTasks.getPendingTasks()
    val completedTasks = taskUseCases.getTasks.getCompletedTasks()
    val tasksWithReminders = taskUseCases.getTasks.getTasksWithReminders()
    
    // Filtered tasks based on current filter
    val filteredTasks = combine(allTasks, currentFilter) { tasks, filter ->
        when (filter) {
            TaskConstants.STATUS_PENDING -> tasks.filter { it.status == TaskConstants.STATUS_PENDING }
            TaskConstants.STATUS_COMPLETED -> tasks.filter { it.status == TaskConstants.STATUS_COMPLETED }
            "high_priority" -> tasks.filter { it.priority == TaskConstants.PRIORITY_HIGH }
            "medium_priority" -> tasks.filter { it.priority == TaskConstants.PRIORITY_MEDIUM }
            "low_priority" -> tasks.filter { it.priority == TaskConstants.PRIORITY_LOW }
            "overdue" -> tasks.filter { task ->
                task.dueDateTime != null && DateTimeUtils.isOverdue(task.dueDateTime) && 
                task.status == TaskConstants.STATUS_PENDING
            }
            "due_today" -> tasks.filter { task ->
                task.dueDateTime != null && DateTimeUtils.isDueToday(task.dueDateTime) && 
                task.status == TaskConstants.STATUS_PENDING
            }
            else -> tasks
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    
    // Statistics
    val taskStats = allTasks.map { tasks ->
        TaskStats(
            total = tasks.size,
            pending = tasks.count { it.status == TaskConstants.STATUS_PENDING },
            completed = tasks.count { it.status == TaskConstants.STATUS_COMPLETED },
            highPriority = tasks.count { it.priority == TaskConstants.PRIORITY_HIGH },
            overdue = tasks.count { task ->
                task.dueDateTime != null && DateTimeUtils.isOverdue(task.dueDateTime) && 
                task.status == TaskConstants.STATUS_PENDING
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), TaskStats())
    
    // Create a new task
    fun createTask(
        title: String,
        description: String? = null,
        dueDateTime: Long? = null,
        priority: Int = TaskConstants.PRIORITY_MEDIUM,
        reminderTime: Long? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = taskUseCases.addTask(
                    title = title,
                    description = description,
                    dueDateTime = dueDateTime,
                    priority = priority,
                    reminderTime = reminderTime
                )
                
                result.fold(
                    onSuccess = { taskId -> 
                        // Schedule reminder if set
                        reminderTime?.let { reminder ->
                            reminderScheduler.scheduleReminder(taskId.toInt(), reminder)
                        }
                    },
                    onFailure = { error -> _error.value = "Failed to create task: ${error.message}" }
                )
            } catch (e: Exception) {
                _error.value = "Failed to create task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Delete a task
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // Cancel reminder if it exists
                if (task.reminderTime != null) {
                    reminderScheduler.cancelReminder(task.id)
                }
                
                val result = taskUseCases.deleteTask(task)
                result.fold(
                    onSuccess = { /* Task deleted successfully */ },
                    onFailure = { error -> _error.value = "Failed to delete task: ${error.message}" }
                )
            } catch (e: Exception) {
                _error.value = "Failed to delete task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Toggle task completion
    fun toggleTaskCompletion(taskId: Int, currentStatus: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = taskUseCases.updateTask.toggleTaskCompletion(taskId)
                result.fold(
                    onSuccess = { /* Status toggled successfully */ },
                    onFailure = { error -> _error.value = "Failed to toggle task status: ${error.message}" }
                )
            } catch (e: Exception) {
                _error.value = "Failed to toggle task status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Update an existing task
    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = taskUseCases.updateTask(task)
                result.fold(
                    onSuccess = { /* Task updated successfully */ },
                    onFailure = { error -> _error.value = "Failed to update task: ${error.message}" }
                )
            } catch (e: Exception) {
                _error.value = "Failed to update task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Update task reminder
    fun updateTaskReminder(task: TaskEntity, newReminderTime: Long?) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                android.util.Log.d("TaskViewModel", "Updating reminder for task ${task.id}: ${task.title}")
                android.util.Log.d("TaskViewModel", "Old reminder: ${task.reminderTime}")
                android.util.Log.d("TaskViewModel", "New reminder: $newReminderTime")
                
                // Update the task with new reminder time
                val updatedTask = task.copy(reminderTime = newReminderTime)
                val result = taskUseCases.updateTask(updatedTask)
                
                result.fold(
                    onSuccess = {
                        android.util.Log.d("TaskViewModel", "Task updated successfully in database")
                        // Update the reminder scheduling
                        if (newReminderTime != null) {
                            android.util.Log.d("TaskViewModel", "Scheduling new reminder for ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(newReminderTime))}")
                            reminderScheduler.checkAndTriggerOverdueReminder(task.id, newReminderTime)
                        } else {
                            android.util.Log.d("TaskViewModel", "Cancelling existing reminder")
                            reminderScheduler.cancelReminder(task.id)
                        }
                    },
                    onFailure = { error -> 
                        android.util.Log.e("TaskViewModel", "Failed to update task: ${error.message}")
                        _error.value = "Failed to update reminder: ${error.message}" 
                    }
                )
            } catch (e: Exception) {
                _error.value = "Failed to update reminder: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Trigger overdue reminder manually
    fun triggerOverdueReminder(taskId: Int, reminderTime: Long) {
        viewModelScope.launch {
            android.util.Log.d("TaskViewModel", "Manually triggering overdue reminder for task $taskId")
            reminderScheduler.checkAndTriggerOverdueReminder(taskId, reminderTime)
        }
    }
    
    // Trigger test notification immediately
    fun triggerTestNotification(taskTitle: String) {
        viewModelScope.launch {
            android.util.Log.d("TaskViewModel", "Triggering test notification for: $taskTitle")
            notificationHelper.triggerTestNotification(taskTitle, "This is a test notification - if you see this, notifications are working!")
        }
    }
    
    // Test the complete reminder system with 5-second delay
    fun testReminderSystem(taskId: Int) {
        viewModelScope.launch {
            android.util.Log.d("TaskViewModel", "Testing complete reminder system for task $taskId")
            reminderScheduler.testNotificationSystem(taskId)
        }
    }
    
    // Check and trigger all overdue reminders
    fun checkAllOverdueReminders() {
        viewModelScope.launch {
            try {
                android.util.Log.d("TaskViewModel", "Checking for overdue reminders...")
                val allTasks = taskUseCases.getTasks().first()
                val currentTime = System.currentTimeMillis()
                
                allTasks.forEach { task ->
                    if (task.reminderTime != null && 
                        task.reminderTime!! <= currentTime && 
                        task.status == TaskConstants.STATUS_PENDING) {
                        
                        android.util.Log.d("TaskViewModel", "Found overdue reminder for task ${task.id}: ${task.title}")
                        // Use both approaches for maximum reliability
                        reminderScheduler.triggerReminderNow(task.id)
                        notificationHelper.showTaskReminder(task.title, task.description ?: "Task reminder", task.id.toLong())
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("TaskViewModel", "Error checking overdue reminders: ${e.message}", e)
            }
        }
    }
    
    // Force trigger notification for specific task (direct approach)
    fun forceNotificationForTask(task: TaskEntity) {
        viewModelScope.launch {
            android.util.Log.d("TaskViewModel", "Force triggering notification for task: ${task.title}")
            notificationHelper.showTaskReminder(task.title, task.description ?: "Task reminder", task.id.toLong())
        }
    }
    
    // Set filter
    fun setFilter(filter: String?) {
        _currentFilter.value = filter
    }
    
    // Clear all completed tasks
    fun clearCompletedTasks() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = taskUseCases.deleteTask.deleteCompletedTasks()
                result.fold(
                    onSuccess = { /* Completed tasks cleared successfully */ },
                    onFailure = { error -> _error.value = "Failed to clear completed tasks: ${error.message}" }
                )
            } catch (e: Exception) {
                _error.value = "Failed to clear completed tasks: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Search tasks
    fun searchTasks(query: String): Flow<List<TaskEntity>> {
        return if (query.isBlank()) {
            allTasks
        } else {
            taskUseCases.getTasks.searchTasks(query)
        }
    }
    
    // Get task by ID (suspend function for screen usage)
    suspend fun getTaskById(id: Int): TaskEntity? {
        return try {
            val result = taskUseCases.getTaskById(id)
            result.fold(
                onSuccess = { task -> task },
                onFailure = { error -> 
                    _error.value = "Failed to get task: ${error.message}"
                    null 
                }
            )
        } catch (e: Exception) {
            _error.value = "Failed to get task: ${e.message}"
            null
        }
    }
    
    // Get task by ID as Flow for reactive UI
    fun getTaskByIdFlow(id: Int): Flow<TaskEntity?> = flow {
        emit(getTaskById(id))
    }
    
    // Clear error
    fun clearError() {
        _error.value = null
    }
    
    // Create a test reminder that triggers in 10 seconds (for debugging)
    fun createTestReminder() {
        val currentTime = System.currentTimeMillis()
        val reminderTime = currentTime + (10 * 1000) // 10 seconds from now
        
        createTask(
            title = "🧪 Test Reminder - 10 seconds", 
            description = "This is a test task to verify that notifications work correctly with vibration and sound",
            dueDateTime = currentTime + (30 * 60 * 1000), // 30 minutes from now
            priority = TaskConstants.PRIORITY_HIGH,
            reminderTime = reminderTime
        )
    }
}

data class TaskStats(
    val total: Int = 0,
    val pending: Int = 0,
    val completed: Int = 0,
    val highPriority: Int = 0,
    val overdue: Int = 0
)