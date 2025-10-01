package com.pharma.taskmanager.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String? = null,
    val dueDateTime: Long? = null, // Timestamp
    val priority: Int = 2, // 1=Low, 2=Medium, 3=High
    val status: String = "pending", // "pending", "completed"
    val reminderTime: Long? = null, // Timestamp for reminder
    val createdAt: Long = System.currentTimeMillis() // Creation timestamp
)