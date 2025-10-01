package com.pharma.taskmanager.utils

import com.pharma.taskmanager.data.database.TaskConstants
import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class that demonstrates Hilt dependency injection.
 * This class is injected with repository and provides utility methods for tasks.
 */
@Singleton
class TaskHelper @Inject constructor(
    private val repository: TaskRepository,
    private val dateTimeUtils: DateTimeUtils
) {
    
    /**
     * Get tasks that are due today
     */
    fun getTasksDueToday(): Flow<List<TaskEntity>> {
        val todayStart = dateTimeUtils.getStartOfDay(dateTimeUtils.getCurrentTimestamp())
        val todayEnd = dateTimeUtils.getEndOfDay(dateTimeUtils.getCurrentTimestamp())
        
        return repository.getAllTasks().map { tasks ->
            tasks.filter { task ->
                task.dueDateTime != null && 
                task.dueDateTime >= todayStart && 
                task.dueDateTime <= todayEnd &&
                task.status == TaskConstants.STATUS_PENDING
            }
        }
    }
    
    /**
     * Get overdue tasks
     */
    fun getOverdueTasks(): Flow<List<TaskEntity>> {
        val now = dateTimeUtils.getCurrentTimestamp()
        
        return repository.getAllTasks().map { tasks ->
            tasks.filter { task ->
                task.dueDateTime != null && 
                task.dueDateTime < now &&
                task.status == TaskConstants.STATUS_PENDING
            }
        }
    }
    
    /**
     * Get task statistics
     */
    fun getTaskStatistics(): Flow<TaskStatistics> {
        return repository.getAllTasks().map { tasks ->
            TaskStatistics(
                total = tasks.size,
                pending = tasks.count { it.status == TaskConstants.STATUS_PENDING },
                completed = tasks.count { it.status == TaskConstants.STATUS_COMPLETED },
                highPriority = tasks.count { it.priority == TaskConstants.PRIORITY_HIGH },
                overdue = tasks.count { task ->
                    task.dueDateTime != null && 
                    dateTimeUtils.isOverdue(task.dueDateTime) &&
                    task.status == TaskConstants.STATUS_PENDING
                },
                dueToday = tasks.count { task ->
                    task.dueDateTime != null &&
                    dateTimeUtils.isDueToday(task.dueDateTime) &&
                    task.status == TaskConstants.STATUS_PENDING
                }
            )
        }
    }
    
    /**
     * Format task due date for display
     */
    fun formatTaskDueDate(task: TaskEntity): String {
        return when {
            task.dueDateTime == null -> "No due date"
            dateTimeUtils.isOverdue(task.dueDateTime) -> "Overdue (${dateTimeUtils.formatDate(task.dueDateTime)})"
            dateTimeUtils.isDueToday(task.dueDateTime) -> "Due today"
            dateTimeUtils.isDueTomorrow(task.dueDateTime) -> "Due tomorrow"
            else -> "Due ${dateTimeUtils.formatDate(task.dueDateTime)}"
        }
    }
    
    /**
     * Get priority label with color indication
     */
    fun getPriorityDisplay(priority: Int): PriorityDisplay {
        return when (priority) {
            TaskConstants.PRIORITY_HIGH -> PriorityDisplay("High", "#F44336")
            TaskConstants.PRIORITY_MEDIUM -> PriorityDisplay("Medium", "#FF9800")
            TaskConstants.PRIORITY_LOW -> PriorityDisplay("Low", "#4CAF50")
            else -> PriorityDisplay("Unknown", "#9E9E9E")
        }
    }
}

data class TaskStatistics(
    val total: Int,
    val pending: Int,
    val completed: Int,
    val highPriority: Int,
    val overdue: Int,
    val dueToday: Int
)

data class PriorityDisplay(
    val label: String,
    val colorHex: String
)