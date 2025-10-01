package com.pharma.taskmanager.domain.usecase

import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    
    suspend operator fun invoke(task: TaskEntity): Result<Unit> {
        return try {
            // Check if task exists
            val existingTask = repository.getTaskById(task.id)
            if (existingTask == null) {
                return Result.failure(IllegalArgumentException("Task with ID ${task.id} not found"))
            }
            
            repository.deleteTask(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteById(taskId: Int): Result<Unit> {
        return try {
            // Get task first to ensure it exists
            val existingTask = repository.getTaskById(taskId)
            if (existingTask == null) {
                return Result.failure(IllegalArgumentException("Task with ID $taskId not found"))
            }
            
            repository.deleteTask(existingTask)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteCompletedTasks(): Result<Unit> {
        return try {
            repository.deleteCompletedTasks()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteMultipleTasks(tasks: List<TaskEntity>): Result<Unit> {
        return try {
            tasks.forEach { task ->
                val existingTask = repository.getTaskById(task.id)
                if (existingTask != null) {
                    repository.deleteTask(task)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteTasksByIds(taskIds: List<Int>): Result<Unit> {
        return try {
            taskIds.forEach { taskId ->
                val existingTask = repository.getTaskById(taskId)
                if (existingTask != null) {
                    repository.deleteTask(existingTask)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}