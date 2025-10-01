package com.pharma.taskmanager.domain.usecase

import com.pharma.taskmanager.data.database.TaskConstants
import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    
    suspend operator fun invoke(task: TaskEntity): Result<Unit> {
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
            
            // Check if task exists
            val existingTask = repository.getTaskById(task.id)
            if (existingTask == null) {
                return Result.failure(IllegalArgumentException("Task with ID ${task.id} not found"))
            }
            
            repository.updateTask(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTaskStatus(taskId: Int, status: String): Result<Unit> {
        return try {
            if (!TaskConstants.isValidStatus(status)) {
                return Result.failure(IllegalArgumentException("Invalid status value"))
            }
            
            // Check if task exists
            val existingTask = repository.getTaskById(taskId)
            if (existingTask == null) {
                return Result.failure(IllegalArgumentException("Task with ID $taskId not found"))
            }
            
            repository.updateTaskStatus(taskId, status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun toggleTaskCompletion(taskId: Int): Result<Unit> {
        return try {
            // Get current task
            val existingTask = repository.getTaskById(taskId)
            if (existingTask == null) {
                return Result.failure(IllegalArgumentException("Task with ID $taskId not found"))
            }
            
            // Toggle status
            val newStatus = if (existingTask.status == TaskConstants.STATUS_PENDING) {
                TaskConstants.STATUS_COMPLETED
            } else {
                TaskConstants.STATUS_PENDING
            }
            
            repository.updateTaskStatus(taskId, newStatus)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTaskPriority(taskId: Int, priority: Int): Result<Unit> {
        return try {
            if (!TaskConstants.isValidPriority(priority)) {
                return Result.failure(IllegalArgumentException("Invalid priority value"))
            }
            
            // Get current task
            val existingTask = repository.getTaskById(taskId)
            if (existingTask == null) {
                return Result.failure(IllegalArgumentException("Task with ID $taskId not found"))
            }
            
            val updatedTask = existingTask.copy(priority = priority)
            repository.updateTask(updatedTask)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}