package com.pharma.taskmanager.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    @Query("SELECT * FROM tasks ORDER BY dueDateTime ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): TaskEntity?
    
    // Filter tasks by status
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY dueDateTime ASC")
    fun getTasksByStatus(status: String): Flow<List<TaskEntity>>
    
    // Filter tasks by priority
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY dueDateTime ASC")
    fun getTasksByPriority(priority: Int): Flow<List<TaskEntity>>
    
    // Filter tasks by status and priority
    @Query("SELECT * FROM tasks WHERE status = :status AND priority = :priority ORDER BY dueDateTime ASC")
    fun getTasksByStatusAndPriority(status: String, priority: Int): Flow<List<TaskEntity>>
    
    // Get tasks due today or overdue
    @Query("SELECT * FROM tasks WHERE dueDateTime IS NOT NULL AND dueDateTime <= :timestamp AND status = 'pending' ORDER BY dueDateTime ASC")
    fun getDueTasks(timestamp: Long): Flow<List<TaskEntity>>
    
    // Get tasks with reminders
    @Query("SELECT * FROM tasks WHERE reminderTime IS NOT NULL ORDER BY reminderTime ASC")
    fun getTasksWithReminders(): Flow<List<TaskEntity>>
    
    // Get tasks with reminders due at or before specified time
    @Query("SELECT * FROM tasks WHERE reminderTime IS NOT NULL AND reminderTime <= :timestamp AND status = 'pending'")
    suspend fun getTasksWithRemindersDue(timestamp: Long): List<TaskEntity>
    
    // Search tasks by title or description
    @Query("SELECT * FROM tasks WHERE title LIKE :searchQuery OR description LIKE :searchQuery ORDER BY dueDateTime ASC")
    fun searchTasks(searchQuery: String): Flow<List<TaskEntity>>
    
    // Get pending tasks
    @Query("SELECT * FROM tasks WHERE status = 'pending' ORDER BY priority DESC, dueDateTime ASC")
    fun getPendingTasks(): Flow<List<TaskEntity>>
    
    // Get completed tasks
    @Query("SELECT * FROM tasks WHERE status = 'completed' ORDER BY dueDateTime DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>
    
    // Update task status
    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: Int, status: String)
    
    // Delete all completed tasks
    @Query("DELETE FROM tasks WHERE status = 'completed'")
    suspend fun deleteCompletedTasks()
    
    // Get task count by status
    @Query("SELECT COUNT(*) FROM tasks WHERE status = :status")
    suspend fun getTaskCountByStatus(status: String): Int
}