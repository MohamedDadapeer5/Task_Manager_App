package com.pharma.taskmanager

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.pharma.taskmanager.data.database.TaskConstants
import com.pharma.taskmanager.domain.repository.TaskRepository
import com.pharma.taskmanager.utils.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class TaskManagerApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    @Inject
    lateinit var taskRepository: TaskRepository
    
    @Inject
    lateinit var reminderScheduler: ReminderScheduler
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    
    override fun onCreate() {
        super.onCreate()
        Log.d("TaskManagerApp", "🚀 App starting - checking for overdue reminders")
        checkOverdueRemindersOnStartup()
    }
    
    private fun checkOverdueRemindersOnStartup() {
        applicationScope.launch {
            try {
                Log.d("TaskManagerApp", "🔍 Checking for overdue reminders on app startup...")
                val allTasks = taskRepository.getAllTasks().first()
                val currentTime = System.currentTimeMillis()
                var overdueCount = 0
                
                allTasks.forEach { task ->
                    if (task.reminderTime != null && 
                        task.reminderTime!! <= currentTime && 
                        task.status == TaskConstants.STATUS_PENDING) {
                        
                        Log.d("TaskManagerApp", "⚠️ Found overdue reminder: ${task.title} (${task.reminderTime})")
                        reminderScheduler.triggerReminderNow(task.id)
                        overdueCount++
                    }
                }
                
                if (overdueCount > 0) {
                    Log.d("TaskManagerApp", "🚨 Triggered $overdueCount overdue reminders on startup")
                } else {
                    Log.d("TaskManagerApp", "✅ No overdue reminders found")
                }
            } catch (e: Exception) {
                Log.e("TaskManagerApp", "❌ Error checking overdue reminders: ${e.message}", e)
            }
        }
    }
}