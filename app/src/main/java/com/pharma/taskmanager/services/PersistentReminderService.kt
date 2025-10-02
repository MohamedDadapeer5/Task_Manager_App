package com.pharma.taskmanager.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pharma.taskmanager.MainActivity
import com.pharma.taskmanager.R
import com.pharma.taskmanager.domain.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Persistent foreground service that rings and vibrates for 1 minute
 * when a task reminder is triggered - WORKS EVEN WHEN APP IS CLOSED
 */
@AndroidEntryPoint
class PersistentReminderService : Service() {
    
    @Inject
    lateinit var taskRepository: TaskRepository
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    companion object {
        private const val TAG = "PersistentReminderService"
        private const val NOTIFICATION_ID = 9999
        private const val CHANNEL_ID = "persistent_reminder_channel"
        private const val REMINDER_DURATION = 60000L // 1 minute
        private const val VIBRATION_INTERVAL = 2000L // Every 2 seconds
        private const val SOUND_INTERVAL = 3000L // Every 3 seconds
    }
    
    private val handler = Handler(Looper.getMainLooper())
    private var vibrator: Vibrator? = null
    private var reminderRunnable: Runnable? = null
    private var stopServiceRunnable: Runnable? = null
    private var currentTaskId: Int? = null
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "🚨 PersistentReminderService created")
        
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "🔥🔥🔥 PersistentReminderService BACKGROUND ALARM TRIGGERED! 🔥🔥🔥")
        
        val taskId = intent?.getIntExtra("task_id", -1) ?: -1
        
        if (intent?.action == "STOP_REMINDER") {
            Log.d(TAG, "🛑 Stop reminder action received")
            // If a task_id was provided, cancel that notification explicitly
            val stopId = intent.getIntExtra("task_id", -1)
            if (stopId > 0) {
                try { NotificationManagerCompat.from(this).cancel(stopId) } catch (_: Exception) {}
            }
            stopReminder()
            return START_NOT_STICKY
        }
        
        if (taskId == -1) {
            Log.e(TAG, "❌ Invalid task ID, stopping service")
            stopSelf()
            return START_NOT_STICKY
        }
        
    currentTaskId = taskId
    Log.d(TAG, "📋 Fetching task details from database for ID: $taskId")
        
        // Fetch task details from database
        serviceScope.launch {
            try {
                val task = taskRepository.getTaskById(taskId)
                val taskTitle = task?.title ?: "Task Reminder"
                val taskDescription = task?.description ?: ""
                
                Log.d(TAG, "✅ Task found: $taskTitle")
                Log.d(TAG, "🚨 STARTING PERSISTENT ALARM-LIKE REMINDER (like alarm clock)")
                
                // Start foreground service with notification
                startForeground(NOTIFICATION_ID, createPersistentNotification(taskId, taskTitle, taskDescription))
                
                // Start continuous reminder pattern (ring + vibrate like alarm)
                // Stop any previous vibration runnable before starting a new one
                stopReminderInternal()
                startContinuousReminder(taskId, taskTitle)
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error fetching task: ${e.message}", e)
                // Fallback with basic notification
                startForeground(NOTIFICATION_ID, createPersistentNotification(taskId, "Task Reminder", ""))
                stopReminderInternal()
                startContinuousReminder(taskId, "Task Reminder")
            }
        }
        
        // Auto-stop after 1 minute
        stopServiceRunnable = Runnable {
            Log.d(TAG, "⏰ 1 minute elapsed - stopping persistent reminder")
            stopReminder()
        }
        handler.postDelayed(stopServiceRunnable!!, REMINDER_DURATION)
        
        return START_NOT_STICKY
    }

    private fun stopReminder() {
        try {
            // Cancel callbacks and vibration immediately
            stopReminderInternal()
            // Remove auto-stop callback if pending
            stopServiceRunnable?.let { handler.removeCallbacks(it) }
            stopServiceRunnable = null
            // Cancel any standard notification that may be showing for this task
            currentTaskId?.let { id ->
                try { NotificationManagerCompat.from(this).cancel(id) } catch (_: Exception) {}
            }
            // Stop foreground and service
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            Log.d(TAG, "✅ Reminder fully stopped")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error stopping reminder: ${e.message}")
            stopSelf()
        }
    }

    private fun stopReminderInternal() {
        try {
            reminderRunnable?.let { handler.removeCallbacks(it) }
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Error removing reminder runnable: ${e.message}")
        }
        reminderRunnable = null
        try {
            vibrator?.cancel()
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Error cancelling vibrator: ${e.message}")
        }
    }
    
    private fun startContinuousReminder(taskId: Int, taskTitle: String) {
        Log.d(TAG, "🔄 Starting continuous reminder pattern")
        
        reminderRunnable = object : Runnable {
            override fun run() {
                try {
                    // Strong vibration
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(
                            VibrationEffect.createWaveform(
                                longArrayOf(0, 500, 200, 500, 200, 500),
                                intArrayOf(0, 255, 0, 255, 0, 255),
                                -1
                            )
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(longArrayOf(0, 500, 200, 500, 200, 500), -1)
                    }
                    
                    Log.d(TAG, "📳 Vibration triggered for persistent reminder")
                    
                    // Continue the pattern
                    handler.postDelayed(this, VIBRATION_INTERVAL)
                    
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Error in continuous reminder: ${e.message}")
                }
            }
        }
        
        // Start immediately
        handler.post(reminderRunnable!!)
    }
    
    private fun createPersistentNotification(taskId: Int, taskTitle: String, taskDescription: String = ""): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("task_id", taskId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val stopIntent = Intent(this, PersistentReminderService::class.java).apply {
            action = "STOP_REMINDER"
            putExtra("task_id", taskId)
        }
        
        val stopPendingIntent = PendingIntent.getService(
            this,
            taskId + 1000,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🚨 URGENT TASK REMINDER")
            .setContentText(taskTitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "⏰ ACTIVE REMINDER: $taskTitle\n\n" +
                "🔔 This reminder is ringing and vibrating\n" +
                "⏱️ Will continue for 1 minute\n" +
                "👆 Tap to view task or dismiss"
            ))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_notification,
                "View Task",
                pendingIntent
            )
            .addAction(
                R.drawable.ic_notification,
                "Stop Reminder",
                stopPendingIntent
            )
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .build()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Persistent Task Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Continuous reminders that ring and vibrate for 1 minute"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setShowBadge(true)
                enableLights(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            
            Log.d(TAG, "✅ Persistent reminder notification channel created")
        }
    }
    
    override fun onDestroy() {
        Log.d(TAG, "🛑 PersistentReminderService destroyed")
        
        // Clean up handlers and stop vibration immediately
        stopReminderInternal()
        stopServiceRunnable?.let { handler.removeCallbacks(it) }
        stopServiceRunnable = null
        
        super.onDestroy()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}