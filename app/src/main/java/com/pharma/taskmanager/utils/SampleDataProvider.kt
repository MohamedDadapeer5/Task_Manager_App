package com.pharma.taskmanager.utils

import com.pharma.taskmanager.data.database.TaskConstants
import com.pharma.taskmanager.data.database.TaskEntity
import java.util.*

/**
 * Provides sample data for testing the application
 */
object SampleDataProvider {
    
    fun getSampleTasks(): List<TaskEntity> {
        val currentTime = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        
        return listOf(
            TaskEntity(
                id = 0,
                title = "Buy groceries",
                description = "Get milk, bread, eggs, and fruits from the supermarket",
                dueDateTime = currentTime + (2 * 60 * 60 * 1000), // 2 hours from now
                priority = TaskConstants.PRIORITY_HIGH,
                status = TaskConstants.STATUS_PENDING,
                reminderTime = currentTime + (1 * 60 * 60 * 1000), // 1 hour from now
                createdAt = currentTime - (1 * 24 * 60 * 60 * 1000) // 1 day ago
            ),
            TaskEntity(
                id = 0,
                title = "Finish project report",
                description = "Complete the quarterly project report and submit to manager",
                dueDateTime = currentTime + (24 * 60 * 60 * 1000), // Tomorrow
                priority = TaskConstants.PRIORITY_HIGH,
                status = TaskConstants.STATUS_PENDING,
                reminderTime = currentTime + (20 * 60 * 60 * 1000), // 20 hours from now
                createdAt = currentTime - (2 * 24 * 60 * 60 * 1000) // 2 days ago
            ),
            TaskEntity(
                id = 0,
                title = "Exercise",
                description = "30 minutes of cardio workout",
                dueDateTime = null,
                priority = TaskConstants.PRIORITY_MEDIUM,
                status = TaskConstants.STATUS_PENDING,
                reminderTime = null,
                createdAt = currentTime - (12 * 60 * 60 * 1000) // 12 hours ago
            ),
            TaskEntity(
                id = 0,
                title = "Call dentist",
                description = "Schedule appointment for dental checkup",
                dueDateTime = currentTime - (2 * 60 * 60 * 1000), // 2 hours ago (overdue)
                priority = TaskConstants.PRIORITY_MEDIUM,
                status = TaskConstants.STATUS_PENDING,
                reminderTime = null,
                createdAt = currentTime - (3 * 24 * 60 * 60 * 1000) // 3 days ago
            ),
            TaskEntity(
                id = 0,
                title = "Read book",
                description = "Finish reading 'The Art of Programming'",
                dueDateTime = null,
                priority = TaskConstants.PRIORITY_LOW,
                status = TaskConstants.STATUS_COMPLETED,
                reminderTime = null,
                createdAt = currentTime - (5 * 24 * 60 * 60 * 1000) // 5 days ago
            ),
            TaskEntity(
                id = 0,
                title = "Team meeting",
                description = "Weekly team standup meeting",
                dueDateTime = currentTime + (3 * 60 * 60 * 1000), // 3 hours from now
                priority = TaskConstants.PRIORITY_MEDIUM,
                status = TaskConstants.STATUS_PENDING,
                reminderTime = currentTime + (2 * 60 * 60 * 1000), // 2 hours from now
                createdAt = currentTime - (6 * 60 * 60 * 1000) // 6 hours ago
            )
        )
    }
}