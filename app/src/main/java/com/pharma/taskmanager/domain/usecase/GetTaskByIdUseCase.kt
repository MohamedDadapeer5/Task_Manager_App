package com.pharma.taskmanager.domain.usecase

import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    
    suspend operator fun invoke(id: Int): Result<TaskEntity?> {
        return try {
            if (id <= 0) {
                return Result.failure(IllegalArgumentException("Invalid task ID: $id"))
            }
            
            val task = repository.getTaskById(id)
            Result.success(task)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getTaskOrThrow(id: Int): Result<TaskEntity> {
        return try {
            if (id <= 0) {
                return Result.failure(IllegalArgumentException("Invalid task ID: $id"))
            }
            
            val task = repository.getTaskById(id)
            if (task == null) {
                Result.failure(IllegalArgumentException("Task with ID $id not found"))
            } else {
                Result.success(task)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getMultipleTasksById(ids: List<Int>): Result<List<TaskEntity>> {
        return try {
            val tasks = mutableListOf<TaskEntity>()
            
            for (id in ids) {
                if (id > 0) {
                    val task = repository.getTaskById(id)
                    if (task != null) {
                        tasks.add(task)
                    }
                }
            }
            
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun taskExists(id: Int): Result<Boolean> {
        return try {
            if (id <= 0) {
                return Result.success(false)
            }
            
            val task = repository.getTaskById(id)
            Result.success(task != null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}