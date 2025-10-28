package com.pharma.taskmanager.utils

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pharma.taskmanager.workers.TaskReminderWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for managing WorkManager tasks for task reminders
 */
@Singleton
class ReminderScheduler @Inject constructor(
    private val context: Context,
    private val alarmReminderScheduler: AlarmReminderScheduler
) {
    
    private val workManager = WorkManager.getInstance(context)
    
    /**
     * Schedule a reminder for a task - ALARM-FIRST APPROACH
     */
    fun scheduleReminder(taskId: Int, reminderTime: Long) {
        val currentTime = System.currentTimeMillis()
        val delay = reminderTime - currentTime
        
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        Log.d(TAG, "⏰ SCHEDULING REMINDER (ALARM-FIRST) for task $taskId")
        Log.d(TAG, "📅 Current time: ${dateFormat.format(Date(currentTime))}")
        Log.d(TAG, "🎯 Reminder time: ${dateFormat.format(Date(reminderTime))}")
        Log.d(TAG, "⏱️ Delay: ${delay}ms (${delay/1000}s) (${delay/60000}m)")
        
        // Always cancel existing reminders first
        cancelReminder(taskId)
        
        if (delay > 0) {
            // PRIMARY: Schedule with AlarmManager first (most reliable like alarm clock)
            Log.d(TAG, "🚨 SCHEDULING WITH ALARMMANAGER (PRIMARY)")
            alarmReminderScheduler.scheduleExactReminder(taskId, reminderTime)
            
            // SECONDARY: Also schedule with WorkManager as backup
            val inputData = Data.Builder()
                .putInt("task_id", taskId)
                .putLong("reminder_time", reminderTime)
                .putBoolean("from_alarm", false)
                .build()
            
            val reminderWork = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("task_reminder_$taskId")
                .build()
            
            workManager.enqueue(reminderWork)
            Log.d(TAG, "✅ WorkManager backup scheduled for task $taskId")
            
            Log.d(TAG, "🔔 ALARM-FIRST SYSTEM: Primary alarm + WorkManager backup at ${dateFormat.format(Date(reminderTime))}")
            
        } else {
            // Time has passed - trigger immediately
            Log.w(TAG, "⚠️ Reminder time has passed, triggering immediately")
            triggerImmediateReminder(taskId)
        }
    }
    
    /**
     * Schedule a backup reminder 30 seconds after the main one
     */
    private fun scheduleBackupReminder(taskId: Int, backupTime: Long) {
        val currentTime = System.currentTimeMillis()
        val delay = backupTime - currentTime
        
        if (delay > 0) {
            val inputData = Data.Builder()
                .putInt("task_id", taskId)
                .putLong("reminder_time", backupTime)
                .putBoolean("from_alarm", false)
                .putBoolean("is_backup", true)
                .build()
            
            val backupWork = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag("backup_reminder_$taskId")
                .build()
            
            workManager.enqueue(backupWork)
            Log.d(TAG, "🔄 Backup reminder scheduled for task $taskId")
        }
    }
    
    /**
     * Trigger an immediate reminder for a task
     */
    private fun triggerImmediateReminder(taskId: Int) {
        Log.d(TAG, "⚡ TRIGGERING IMMEDIATE REMINDER for task $taskId")
        
        // IMMEDIATE: Trigger alarm right now (1 second delay)
        alarmReminderScheduler.scheduleExactReminder(taskId, System.currentTimeMillis() + 1000)
        
        // BACKUP: Also use WorkManager immediately
        val inputData = Data.Builder()
            .putInt("task_id", taskId)
            .putLong("reminder_time", System.currentTimeMillis())
            .putBoolean("immediate_trigger", true)
            .build()
        
        val immediateWork = OneTimeWorkRequestBuilder<TaskReminderWorker>()
            .setInputData(inputData)
            .addTag("immediate_reminder_$taskId")
            .build()
        
        workManager.enqueue(immediateWork)
        Log.d(TAG, "✅ DUAL IMMEDIATE TRIGGER: Alarm + WorkManager both scheduled")
    }
    
    /**
     * Cancel a scheduled reminder for a task
     */
    fun cancelReminder(taskId: Int) {
        workManager.cancelAllWorkByTag("task_reminder_$taskId")
        workManager.cancelAllWorkByTag("backup_reminder_$taskId")
        workManager.cancelAllWorkByTag("immediate_reminder_$taskId")
        alarmReminderScheduler.cancelReminder(taskId)
        Log.d(TAG, "🚫 Cancelled all reminders (WorkManager + AlarmManager) for task $taskId")
    }

    /**
     * Stop any active/persisting reminder for a task. This cancels scheduled triggers
     * and sends a STOP intent to the foreground PersistentReminderService so any
     * currently ringing reminder will stop immediately.
     */
    fun stopActiveReminder(taskId: Int) {
        // Cancel scheduled reminders first
        cancelReminder(taskId)

        try {
            val stopIntent = android.content.Intent(context, com.pharma.taskmanager.services.PersistentReminderService::class.java).apply {
                action = "STOP_REMINDER"
                putExtra("task_id", taskId)
            }
            // Use startService to deliver the intent to the service (no binding needed)
            context.startService(stopIntent)
            Log.d(TAG, "🛑 Sent STOP_REMINDER to PersistentReminderService for task $taskId")
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Failed to send STOP_REMINDER intent: ${e.message}")
        }
    }
    
    /**
     * Update a reminder - cancels the old one and schedules a new one
     */
    fun updateReminder(taskId: Int, newReminderTime: Long) {
        cancelReminder(taskId)
        scheduleReminder(taskId, newReminderTime)
    }
    
    /**
     * Force trigger a reminder notification now (for testing or overdue reminders)
     */
    fun triggerReminderNow(taskId: Int) {
        Log.d(TAG, "🚨 Force triggering reminder NOW for task $taskId")
        triggerImmediateReminder(taskId)
    }
    
    /**
     * Test the complete notification system with a very short delay (5 seconds)
     */
    fun testNotificationSystem(taskId: Int) {
        val testTime = System.currentTimeMillis() + 5000 // 5 seconds from now
        Log.d(TAG, "🧪 Testing notification system with 5-second delay for task $taskId")
        scheduleReminder(taskId, testTime)
    }
    
    /**
     * Check if a reminder should have been triggered and trigger it if overdue
     */
    fun checkAndTriggerOverdueReminder(taskId: Int, reminderTime: Long) {
        val currentTime = System.currentTimeMillis()
        if (reminderTime <= currentTime) {
            Log.d(TAG, "⚠️ Reminder is overdue for task $taskId, triggering now")
            triggerImmediateReminder(taskId)
        } else {
            Log.d(TAG, "⏰ Reminder is scheduled for future, scheduling normally")
            scheduleReminder(taskId, reminderTime)
        }
    }
    
    /**
     * Cancel all reminders (useful for cleanup)
     */
    fun cancelAllReminders() {
        workManager.cancelAllWorkByTag("task_reminder")
    }
    
    companion object {
        private const val TAG = "ReminderScheduler"
    }
}