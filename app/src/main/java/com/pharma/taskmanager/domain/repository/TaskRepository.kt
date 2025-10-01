package com.pharma.taskmanager.domain.repository

import com.pharma.taskmanager.data.database.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    
    // Observing tasks with Flow
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>>
    
    fun getTasksByPriority(priority: Int): Flow<List<TaskEntity>>
    
    fun getPendingTasks(): Flow<List<TaskEntity>>
    
    fun getCompletedTasks(): Flow<List<TaskEntity>>
    
    fun getDueTasks(timestamp: Long): Flow<List<TaskEntity>>
    
    fun getTasksWithReminders(): Flow<List<TaskEntity>>
    
    fun searchTasks(query: String): Flow<List<TaskEntity>>
    
    // Suspend functions for CRUD operations
    suspend fun addTask(task: TaskEntity): Long
    
    suspend fun updateTask(task: TaskEntity)
    
    suspend fun deleteTask(task: TaskEntity)
    
    suspend fun getTaskById(id: Int): TaskEntity?
    
    // Additional operations
    suspend fun updateTaskStatus(id: Int, status: String)
    
    suspend fun deleteCompletedTasks()
    
    suspend fun getTaskCountByStatus(status: String): Int
}