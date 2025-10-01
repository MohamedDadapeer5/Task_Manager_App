package com.pharma.taskmanager.data.repository

import com.pharma.taskmanager.data.database.TaskDao
import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.domain.repository.TaskRepository as TaskRepositoryInterface
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepositoryInterface {
    
    // Basic CRUD operations - implementing interface methods
    override suspend fun addTask(task: TaskEntity): Long = taskDao.insertTask(task)
    
    override suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    
    override suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    
    override fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAllTasks()
    
    override suspend fun getTaskById(id: Int): TaskEntity? = taskDao.getTaskById(id)
    
    // Filter operations - implementing interface methods
    override fun getTasksByStatus(status: String): Flow<List<TaskEntity>> = taskDao.getTasksByStatus(status)
    
    override fun getTasksByPriority(priority: Int): Flow<List<TaskEntity>> = taskDao.getTasksByPriority(priority)
    
    fun getTasksByStatusAndPriority(status: String, priority: Int): Flow<List<TaskEntity>> = 
        taskDao.getTasksByStatusAndPriority(status, priority)
    
    // Convenience methods - implementing interface methods
    override fun getPendingTasks(): Flow<List<TaskEntity>> = taskDao.getPendingTasks()
    
    override fun getCompletedTasks(): Flow<List<TaskEntity>> = taskDao.getCompletedTasks()
    
    // Due and reminder tasks - implementing interface methods
    override fun getDueTasks(timestamp: Long): Flow<List<TaskEntity>> = taskDao.getDueTasks(timestamp)
    
    override fun getTasksWithReminders(): Flow<List<TaskEntity>> = taskDao.getTasksWithReminders()
    
    suspend fun getTasksWithRemindersDue(timestamp: Long): List<TaskEntity> = 
        taskDao.getTasksWithRemindersDue(timestamp)
    
    // Search functionality - implementing interface methods
    override fun searchTasks(searchQuery: String): Flow<List<TaskEntity>> = 
        taskDao.searchTasks("%$searchQuery%")
    
    // Status updates - implementing interface methods
    override suspend fun updateTaskStatus(id: Int, status: String) = taskDao.updateTaskStatus(id, status)
    
    suspend fun markTaskAsCompleted(id: Int) = taskDao.updateTaskStatus(id, "completed")
    
    suspend fun markTaskAsPending(id: Int) = taskDao.updateTaskStatus(id, "pending")
    
    // Bulk operations - implementing interface methods
    override suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()
    
    // Statistics - implementing interface methods
    override suspend fun getTaskCountByStatus(status: String): Int = taskDao.getTaskCountByStatus(status)
    
    suspend fun getPendingTaskCount(): Int = taskDao.getTaskCountByStatus("pending")
    
    suspend fun getCompletedTaskCount(): Int = taskDao.getTaskCountByStatus("completed")
    
    // Helper methods for priority
    companion object {
        const val PRIORITY_LOW = 1
        const val PRIORITY_MEDIUM = 2
        const val PRIORITY_HIGH = 3
        
        const val STATUS_PENDING = "pending"
        const val STATUS_COMPLETED = "completed"
    }
}