package com.pharma.taskmanager.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    
    private const val DATE_FORMAT = "MMM dd, yyyy"
    private const val TIME_FORMAT = "hh:mm a"
    private const val DATE_TIME_FORMAT = "MMM dd, yyyy hh:mm a"
    
    private val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    private val timeFormatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    private val dateTimeFormatter = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
    
    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
    
    fun formatDate(timestamp: Long): String {
        return dateFormatter.format(Date(timestamp))
    }
    
    fun formatTime(timestamp: Long): String {
        return timeFormatter.format(Date(timestamp))
    }
    
    fun formatDateTime(timestamp: Long): String {
        return dateTimeFormatter.format(Date(timestamp))
    }
    
    fun formatRelativeTime(timestamp: Long): String {
        val now = getCurrentTimestamp()
        val diff = now - timestamp
        
        return when {
            diff < 0 -> "In the future"
            diff < 60 * 1000 -> "Just now"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} minutes ago"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)} days ago"
            else -> formatDate(timestamp)
        }
    }
    
    fun isOverdue(dueDateTime: Long?): Boolean {
        if (dueDateTime == null) return false
        return dueDateTime < getCurrentTimestamp()
    }
    
    fun isDueToday(dueDateTime: Long?): Boolean {
        if (dueDateTime == null) return false
        
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val todayYear = calendar.get(Calendar.YEAR)
        
        calendar.timeInMillis = dueDateTime
        val dueDay = calendar.get(Calendar.DAY_OF_YEAR)
        val dueYear = calendar.get(Calendar.YEAR)
        
        return today == dueDay && todayYear == dueYear
    }
    
    fun isDueTomorrow(dueDateTime: Long?): Boolean {
        if (dueDateTime == null) return false
        
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = calendar.get(Calendar.DAY_OF_YEAR)
        val tomorrowYear = calendar.get(Calendar.YEAR)
        
        calendar.timeInMillis = dueDateTime
        val dueDay = calendar.get(Calendar.DAY_OF_YEAR)
        val dueYear = calendar.get(Calendar.YEAR)
        
        return tomorrow == dueDay && tomorrowYear == dueYear
    }
    
    fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun getEndOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}