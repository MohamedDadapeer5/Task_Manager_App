package com.pharma.taskmanager.data.database

object TaskConstants {
    
    // Priority levels
    const val PRIORITY_LOW = 1
    const val PRIORITY_MEDIUM = 2
    const val PRIORITY_HIGH = 3
    
    // Status values
    const val STATUS_PENDING = "pending"
    const val STATUS_COMPLETED = "completed"
    
    // Helper functions
    fun getPriorityLabel(priority: Int): String {
        return when (priority) {
            PRIORITY_LOW -> "Low"
            PRIORITY_MEDIUM -> "Medium"
            PRIORITY_HIGH -> "High"
            else -> "Medium"
        }
    }
    
    fun getPriorityFromLabel(label: String): Int {
        return when (label.lowercase()) {
            "low" -> PRIORITY_LOW
            "medium" -> PRIORITY_MEDIUM
            "high" -> PRIORITY_HIGH
            else -> PRIORITY_MEDIUM
        }
    }
    
    fun getStatusLabel(status: String): String {
        return when (status) {
            STATUS_PENDING -> "Pending"
            STATUS_COMPLETED -> "Completed"
            else -> "Pending"
        }
    }
    
    fun isValidPriority(priority: Int): Boolean {
        return priority in PRIORITY_LOW..PRIORITY_HIGH
    }
    
    fun isValidStatus(status: String): Boolean {
        return status in listOf(STATUS_PENDING, STATUS_COMPLETED)
    }
}