package com.pharma.taskmanager.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pharma.taskmanager.MainActivity
import com.pharma.taskmanager.R
import com.pharma.taskmanager.data.database.TaskConstants
import com.pharma.taskmanager.domain.repository.TaskRepository
import com.pharma.taskmanager.utils.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Hilt-enabled WorkManager worker for task reminders.
 * This worker is automatically injected with dependencies through Hilt.
 */
@HiltWorker
class TaskReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val taskRepository: TaskRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        val currentTime = System.currentTimeMillis()
        val taskId = inputData.getInt("task_id", -1)
        val fromAlarm = inputData.getBoolean("from_alarm", false)
        
        Log.d(TAG, "🔔 TaskReminderWorker started at $currentTime")
        Log.d(TAG, "📋 Task ID: $taskId")
        Log.d(TAG, "⏰ Triggered from: ${if (fromAlarm) "AlarmManager" else "WorkManager"}")
        
        if (taskId == -1) {
            Log.e(TAG, "❌ Invalid task ID: $taskId")
            return Result.failure()
        }
        
        Log.d(TAG, "📋 Processing reminder for task ID: $taskId")
        
        try {
            val task = taskRepository.getTaskById(taskId)
            if (task == null) {
                Log.e(TAG, "❌ Task not found for ID: $taskId")
                return Result.failure()
            }
            
            Log.d(TAG, "✅ Found task: '${task.title}', status: ${task.status}")
            Log.d(TAG, "📝 Task description: ${task.description ?: "No description"}")
            
            if (task.status == TaskConstants.STATUS_PENDING) {
                Log.d(TAG, "🚨 Task is PENDING - Starting persistent reminder with vibration and sound")
                notificationHelper.showTaskReminder(task.title, task.description ?: "", taskId.toLong())
                Log.d(TAG, "✅ Persistent reminder started successfully for task: ${task.title}")
            } else {
                Log.d(TAG, "⏭️ Task status is '${task.status}' - not pending, skipping notification")
            }
            
            return Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error processing task reminder: ${e.message}", e)
            return Result.failure()
        }
    }
    
    private fun showNotification(title: String, description: String) {
        Log.d(TAG, "Creating notification for: $title")
        
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        
        // Trigger vibration first
        triggerVibration()
        
        // Get notification sound
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        Log.d(TAG, "Using sound URI: $soundUri")
        
        // Create intent to open the app when notification is tapped
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("task_id", inputData.getInt("task_id", -1))
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            inputData.getInt("task_id", 0),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(applicationContext, com.pharma.taskmanager.services.PersistentReminderService::class.java).apply {
            action = "STOP_REMINDER"
        }
        val stopPendingIntent = PendingIntent.getService(
            applicationContext,
            inputData.getInt("task_id", 0) + 1000,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build the notification
        val notificationId = System.currentTimeMillis().toInt()
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⏰ Task Reminder")
            .setContentText("$title - $description")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$title\n\n$description"))
            .setPriority(NotificationCompat.PRIORITY_MAX) // Maximum priority for immediate attention
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Enable sound, vibration, and lights
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 1000, 500, 1000, 500, 1000)) // Stronger vibration pattern
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM) // Use ALARM for more attention
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(false)
            .addAction(R.drawable.ic_notification, "Stop Reminder", stopPendingIntent)
            .setFullScreenIntent(pendingIntent, true) // Make it a heads-up notification
            .build()
        
        try {
            // Show the notification
            if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
                notificationManager.notify(notificationId, notification)
                Log.d(TAG, "Notification shown with ID: $notificationId")
            } else {
                Log.w(TAG, "Notifications are disabled")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show notification", e)
        }
    }
    
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for task reminders"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
                setVibrationPattern(longArrayOf(0, 1000, 500, 1000, 500, 1000))
                setSound(soundUri, audioAttributes)
                lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            }
            
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created")
        }
    }
    
    private fun triggerVibration() {
        try {
            val vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 500, 250, 500)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12 and above
                val vibratorManager = applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8.0 and above
                val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, -1))
            } else {
                // Below Android 8.0
                @Suppress("DEPRECATION")
                val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                @Suppress("DEPRECATION")
                vibrator.vibrate(vibrationPattern, -1)
            }
        } catch (e: Exception) {
            // Vibration failed, but notification will still show
            e.printStackTrace()
        }
    }
    
    companion object {
        const val CHANNEL_ID = "task_reminders"
        private const val TAG = "TaskReminderWorker"
    }
}