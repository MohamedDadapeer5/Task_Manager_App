package com.pharma.taskmanager.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context 
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pharma.taskmanager.MainActivity
import com.pharma.taskmanager.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "task_reminders"
        private const val NOTIFICATION_ID = 1001
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                .build()
            
            val channel = NotificationChannel(
                CHANNEL_ID, 
                "Task Reminders", 
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Task reminder notifications - like alarm clock"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                // Strong vibration pattern like alarm
                setVibrationPattern(longArrayOf(0, 1000, 500, 1000, 500, 1000, 500, 1000))
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                setBypassDnd(true) // Bypass Do Not Disturb like alarm
            }
            notificationManager.createNotificationChannel(channel)
            android.util.Log.d("NotificationHelper", "🔔 Notification channel created with ALARM sound and strong vibration")
        }
    }

    fun showTaskReminder(taskTitle: String, taskDescription: String, taskId: Long, isCritical: Boolean = false) {
        android.util.Log.d("NotificationHelper", "🔔 Showing task reminder notification for: $taskTitle")
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("task_id", taskId.toInt())
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 
            taskId.toInt(), 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Stop action to halt persistent reminder service if running
        val stopIntent = Intent(context, com.pharma.taskmanager.services.PersistentReminderService::class.java).apply {
            action = "STOP_REMINDER"
        }
        val stopPendingIntent = PendingIntent.getService(
            context,
            taskId.toInt() + 1000,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = taskId.toInt()
        
        // Use alarm sound instead of notification sound
        val alarmSound = android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🚨 TASK REMINDER")
            .setContentText("$taskTitle - $taskDescription")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$taskTitle\n\n$taskDescription")
                .setBigContentTitle("🚨 TASK REMINDER"))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS) // Don't use DEFAULT_ALL, we'll set sound manually
            .setSound(alarmSound) // Use alarm sound like alarm clock
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(pendingIntent, true)
            .setOngoing(false) // Allow dismissal
            .setVibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000, 500, 1000)) // Longer vibration
            .addAction(R.drawable.ic_notification, "Stop Reminder", stopPendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
            android.util.Log.d("NotificationHelper", "✅ Notification displayed successfully for task: $taskTitle")
        } catch (e: Exception) {
            android.util.Log.e("NotificationHelper", "❌ Failed to show notification: ${e.message}", e)
            e.printStackTrace()
        }
    }
    
    /**
     * Trigger a test notification immediately for debugging
     */
    fun triggerTestNotification(taskTitle: String, message: String = "This is a test notification - if you see this, notifications are working!") {
        android.util.Log.d("NotificationHelper", "🧪 Triggering test notification")
        val testTaskId = System.currentTimeMillis()
        showTaskReminder(taskTitle, message, testTaskId, true)
    }
}