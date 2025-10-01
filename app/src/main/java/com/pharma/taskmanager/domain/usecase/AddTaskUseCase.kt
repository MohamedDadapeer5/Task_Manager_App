package com.pharma.taskmanager.domain.usecase

import com.pharma.taskmanager.data.database.TaskConstants
import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    
    suspend operator fun invoke(
        title: String,
        description: String? = null,
        dueDateTime: Long? = null,
        priority: Int = TaskConstants.PRIORITY_MEDIUM,
        reminderTime: Long? = null
    ): Result<Long> {
        return try {
            // Validate input
            if (title.isBlank()) {
                return Result.failure(IllegalArgumentException("Title cannot be empty"))
            }
            
            if (!TaskConstants.isValidPriority(priority)) {
                return Result.failure(IllegalArgumentException("Invalid priority value"))
            }
            
            // Create task entity
            val task = TaskEntity(
                title = title.trim(),
                description = description?.trim(),
                dueDateTime = dueDateTime,
                priority = priority,
                status = TaskConstants.STATUS_PENDING,
                reminderTime = reminderTime
            )
            
            val taskId = repository.addTask(task)
            Result.success(taskId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun invoke(task: TaskEntity): Result<Long> {
        return try {
            // Validate task
            if (task.title.isBlank()) {
                return Result.failure(IllegalArgumentException("Title cannot be empty"))
            }
            
            if (!TaskConstants.isValidPriority(task.priority)) {
                return Result.failure(IllegalArgumentException("Invalid priority value"))
            }
            
            if (!TaskConstants.isValidStatus(task.status)) {
                return Result.failure(IllegalArgumentException("Invalid status value"))
            }
            
            val taskId = repository.addTask(task)
            Result.success(taskId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}