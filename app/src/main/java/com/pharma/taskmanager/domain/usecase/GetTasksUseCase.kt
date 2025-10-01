package com.pharma.taskmanager.domain.usecase

import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    
    operator fun invoke(): Flow<List<TaskEntity>> {
        return repository.getAllTasks()
    }
    
    fun getPendingTasks(): Flow<List<TaskEntity>> {
        return repository.getPendingTasks()
    }
    
    fun getCompletedTasks(): Flow<List<TaskEntity>> {
        return repository.getCompletedTasks()
    }
    
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>> {
        return repository.getTasksByStatus(status)
    }
    
    fun getTasksByPriority(priority: Int): Flow<List<TaskEntity>> {
        return repository.getTasksByPriority(priority)
    }
    
    fun getDueTasks(timestamp: Long): Flow<List<TaskEntity>> {
        return repository.getDueTasks(timestamp)
    }
    
    fun getTasksWithReminders(): Flow<List<TaskEntity>> {
        return repository.getTasksWithReminders()
    }
    
    fun searchTasks(query: String): Flow<List<TaskEntity>> {
        return repository.searchTasks(query)
    }
}