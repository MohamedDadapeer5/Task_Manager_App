package com.pharma.taskmanager.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.pharma.taskmanager.receivers.ReminderBroadcastReceiver
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AlarmManager-based reminder system for critical task reminders
 * This is more reliable than WorkManager for time-critical notifications
 */
@Singleton
class AlarmReminderScheduler @Inject constructor(
    private val context: Context
) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    fun scheduleExactReminder(taskId: Int, reminderTime: Long) {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        Log.d(TAG, "🚨 SCHEDULING EXACT ALARM REMINDER")
        Log.d(TAG, "📋 Task ID: $taskId")
        Log.d(TAG, "📅 Current time: ${dateFormat.format(Date(currentTime))}")
        Log.d(TAG, "⏰ Reminder time: ${dateFormat.format(Date(reminderTime))}")
        Log.d(TAG, "⏱️ Time until reminder: ${(reminderTime - currentTime)/1000} seconds")
        
        if (reminderTime <= currentTime) {
            Log.w(TAG, "❌ Reminder time is in the past, triggering immediately")
            // Trigger immediately if time has passed
            triggerImmediateReminder(taskId)
            return
        }
        
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("task_id", taskId)
            putExtra("reminder_time", reminderTime)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId, // Use taskId as request code for uniqueness
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        try {
            Log.d(TAG, "🚨 SCHEDULING ALARM TO WAKE DEVICE (like alarm clock)")
            
            // Check if we can schedule exact alarms
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, // WAKEUP ensures device wakes up
                        reminderTime,
                        pendingIntent
                    )
                    Log.d(TAG, "✅ EXACT WAKEUP ALARM SCHEDULED (Android 12+) - WILL WAKE DEVICE")
                } else {
                    Log.e(TAG, "❌ Cannot schedule exact alarms - permission not granted")
                    // Fallback to approximate alarm but still wake up
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, // Still use WAKEUP
                        reminderTime,
                        pendingIntent
                    )
                    Log.d(TAG, "⚠️ APPROXIMATE WAKEUP ALARM SCHEDULED - WILL STILL WAKE DEVICE")
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, // WAKEUP ensures device wakes up
                    reminderTime,
                    pendingIntent
                )
                Log.d(TAG, "✅ EXACT WAKEUP ALARM SCHEDULED (Android 6+) - WILL WAKE DEVICE")
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP, // WAKEUP ensures device wakes up
                    reminderTime,
                    pendingIntent
                )
                Log.d(TAG, "✅ EXACT WAKEUP ALARM SCHEDULED (Legacy) - WILL WAKE DEVICE")
            }
            
            Log.d(TAG, "🔔 ALARM SET TO TRIGGER EVEN IF:")
            Log.d(TAG, "   📱 App is closed")
            Log.d(TAG, "   😴 Device is sleeping")
            Log.d(TAG, "   🔕 Device is in silent mode")
            Log.d(TAG, "   🔋 Device is in power saving")
            
        } catch (e: Exception) {
            Log.e(TAG, "💥 Failed to schedule alarm", e)
        }
    }
    
    fun cancelReminder(taskId: Int) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        Log.d(TAG, "🚫 Alarm cancelled for task $taskId")
    }
    
    private fun triggerImmediateReminder(taskId: Int) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("task_id", taskId)
            putExtra("immediate_trigger", true)
        }
        context.sendBroadcast(intent)
        Log.d(TAG, "⚡ Immediate reminder triggered for task $taskId")
    }
    
    companion object {
        private const val TAG = "AlarmReminderScheduler"
    }
}