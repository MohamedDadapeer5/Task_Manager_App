package com.pharma.taskmanager.data.database

data class TaskFilter(
    val status: String? = null,
    val priority: Int? = null,
    val hasDueDate: Boolean? = null,
    val hasReminder: Boolean? = null,
    val searchQuery: String? = null,
    val isOverdue: Boolean? = null
) {
    companion object {
        fun pending() = TaskFilter(status = TaskConstants.STATUS_PENDING)
        fun completed() = TaskFilter(status = TaskConstants.STATUS_COMPLETED)
        fun highPriority() = TaskFilter(priority = TaskConstants.PRIORITY_HIGH)
        fun mediumPriority() = TaskFilter(priority = TaskConstants.PRIORITY_MEDIUM)
        fun lowPriority() = TaskFilter(priority = TaskConstants.PRIORITY_LOW)
        fun withReminders() = TaskFilter(hasReminder = true)
        fun overdue() = TaskFilter(isOverdue = true)
    }
}