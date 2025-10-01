package com.pharma.taskmanager.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pharma.taskmanager.workers.TaskReminderWorker

/**
 * BroadcastReceiver that handles alarm-based reminders
 * This receives the alarm intent and triggers the notification
 */
class ReminderBroadcastReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "🚨🚨🚨 ALARM REMINDER RECEIVED - BACKGROUND TRIGGER! 🚨🚨🚨")
        
        val taskId = intent.getIntExtra("task_id", -1)
        val reminderTime = intent.getLongExtra("reminder_time", 0L)
        val isImmediate = intent.getBooleanExtra("immediate_trigger", false)
        
        Log.d(TAG, "📋 Task ID: $taskId")
        Log.d(TAG, "⏰ Reminder time: $reminderTime")
        Log.d(TAG, "⚡ Immediate trigger: $isImmediate")
        Log.d(TAG, "🎯 App may be closed - triggering background alarm!")
        
        if (taskId == -1) {
            Log.e(TAG, "❌ Invalid task ID received")
            return
        }
        
        try {
            // PRIMARY: Trigger the notification worker immediately
            val inputData = Data.Builder()
                .putInt("task_id", taskId)
                .putLong("reminder_time", reminderTime)
                .putBoolean("from_alarm", true)
                .putBoolean("background_trigger", true)
                .build()
            
            val reminderWork = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInputData(inputData)
                .addTag("alarm_reminder_$taskId")
                .build()
            
            WorkManager.getInstance(context).enqueue(reminderWork)
            Log.d(TAG, "✅ PRIMARY: WorkManager reminder triggered from alarm")
            
            // BACKUP: Also start a foreground service for persistent notification
            val serviceIntent = Intent(context, com.pharma.taskmanager.services.PersistentReminderService::class.java).apply {
                putExtra("task_id", taskId)
                putExtra("reminder_time", reminderTime)
                putExtra("from_alarm", true)
            }
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Log.d(TAG, "✅ BACKUP: Foreground service started for persistent notification")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error processing alarm reminder: ${e.message}", e)
        }
    }
    
    companion object {
        private const val TAG = "ReminderBroadcastReceiver"
    }
}