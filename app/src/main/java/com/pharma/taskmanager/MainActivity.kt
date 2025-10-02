package com.pharma.taskmanager

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.pharma.taskmanager.navigation.TaskManagerNavGraph
import com.pharma.taskmanager.ui.theme.PersonalTaskManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import androidx.core.app.NotificationManagerCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Emits task IDs from notification taps so the composable can navigate accordingly
    private val taskIdEvents = MutableSharedFlow<Int>(replay = 0, extraBufferCapacity = 1)
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.w("MainActivity", "Notification permission denied")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Request notification permission for Android 13+
        requestNotificationPermission()
        
        // Request exact alarm permission for Android 12+
        requestExactAlarmPermission()
        
        // If the app was launched from a notification with a task_id, emit it
        val initialTaskId = intent?.getIntExtra("task_id", -1) ?: -1
        if (initialTaskId > 0) {
            Log.d("MainActivity", "Launching from notification, task_id=$initialTaskId")
            // Stop persistent reminder service and clear notification immediately
            try {
                val stopIntent = Intent(this, com.pharma.taskmanager.services.PersistentReminderService::class.java).apply { action = "STOP_REMINDER" }
                startService(stopIntent)
                NotificationManagerCompat.from(this).cancel(initialTaskId)
            } catch (e: Exception) {
                Log.w("MainActivity", "Failed to stop service or cancel notification: ${e.message}")
            }
            taskIdEvents.tryEmit(initialTaskId)
        }
        
        setContent {
            PersonalTaskManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskManagerApp(taskIdEvents = taskIdEvents)
                }
            }
        }
    }
    
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val taskId = intent?.getIntExtra("task_id", -1) ?: -1
        if (taskId > 0) {
            Log.d("MainActivity", "Received new intent with task_id=$taskId")
            try {
                val stopIntent = Intent(this, com.pharma.taskmanager.services.PersistentReminderService::class.java).apply { action = "STOP_REMINDER" }
                startService(stopIntent)
                NotificationManagerCompat.from(this).cancel(taskId)
            } catch (e: Exception) {
                Log.w("MainActivity", "Failed to stop service or cancel notification: ${e.message}")
            }
            // Emit so the composable layer can navigate to the task detail
            taskIdEvents.tryEmit(taskId)
        }
    }
    
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        
        // Check if notification channels are enabled
        checkNotificationChannelSettings()
    }
    
    private fun checkNotificationChannelSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
            val channel = notificationManager.getNotificationChannel("task_reminders")
            
            if (channel != null) {
                Log.d("MainActivity", "Notification channel importance: ${channel.importance}")
                Log.d("MainActivity", "Can show badge: ${channel.canShowBadge()}")
                Log.d("MainActivity", "Can bypass DND: ${channel.canBypassDnd()}")
                
                if (channel.importance < android.app.NotificationManager.IMPORTANCE_HIGH) {
                    Log.w("MainActivity", "Notification channel importance is too low for heads-up notifications")
                }
            } else {
                Log.w("MainActivity", "Notification channel not found")
            }
        }
        
        // Check overall notification settings
        val notificationManagerCompat = androidx.core.app.NotificationManagerCompat.from(this)
        Log.d("MainActivity", "Notifications enabled: ${notificationManagerCompat.areNotificationsEnabled()}")
    }
    
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("MainActivity", "⚠️ Exact alarm permission not granted")
                // Open settings for user to grant permission
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                try {
                    startActivity(intent)
                    Log.d("MainActivity", "📱 Opened exact alarm settings")
                } catch (e: Exception) {
                    Log.e("MainActivity", "Failed to open exact alarm settings", e)
                }
            } else {
                Log.d("MainActivity", "✅ Exact alarm permission already granted")
            }
        }
    }
}

@Composable
fun TaskManagerApp(taskIdEvents: Flow<Int>? = null) {
    val navController = rememberNavController()
    
    // Listen for task_id events (from notification taps) and navigate to Task Detail
    androidx.compose.runtime.LaunchedEffect(taskIdEvents) {
        taskIdEvents?.collect { taskId ->
            Log.d("MainActivity", "Navigating to Task Detail for task_id=$taskId from event")
            navController.navigate(com.pharma.taskmanager.navigation.Screen.TaskDetail.createTaskDetailRoute(taskId))
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        TaskManagerNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Preview function for the main app
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TaskManagerAppSystemPreview() {
    PersonalTaskManagerTheme {
        TaskManagerApp()
    }
}

@Preview(showBackground = true)
@Composable
fun TaskManagerAppPreview() {
    PersonalTaskManagerTheme {
        TaskManagerApp()
    }
}