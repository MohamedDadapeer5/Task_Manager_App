package com.pharma.taskmanager.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pharma.taskmanager.data.database.TaskManagerDatabase
import com.pharma.taskmanager.data.database.TaskEntity
import com.pharma.taskmanager.workers.TaskReminderWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * BroadcastReceiver that handles alarm-based reminders
 * Improves behavior when multiple reminders fall at the same time by
 * selecting the highest-priority pending task to trigger immediately and
 * staggering the others so high-priority reminders ring first.
 */
class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "🚨 ALARM REMINDER RECEIVED - background trigger")

        // The alarm provides a reminder_time; if absent, use current time
        val intentReminderTime = intent.getLongExtra("reminder_time", 0L)
        val reminderTime = if (intentReminderTime <= 0L) System.currentTimeMillis() else intentReminderTime

        Log.d(TAG, "⏰ Reminder time from intent: $reminderTime")

        // Do DB work and scheduling on IO dispatcher
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = TaskManagerDatabase.getDatabase(context)
                // getTasksWithRemindersDue is a suspend DAO method
                val tasksDue: List<TaskEntity> = db.taskDao().getTasksWithRemindersDue(reminderTime)
                    .filter { it.status == "pending" }

                if (tasksDue.isEmpty()) {
                    Log.w(TAG, "No pending tasks found for reminder time: $reminderTime")
                    return@launch
                }

                // Sort by priority (higher first), then earliest due date, then createdAt
                val sorted = tasksDue.sortedWith(
                    compareByDescending<TaskEntity> { it.priority }
                        .thenBy { it.dueDateTime ?: Long.MAX_VALUE }
                        .thenBy { it.createdAt }
                )

                // Primary: trigger the highest-priority task immediately
                val first = sorted.first()
                triggerWorkAndServiceForTask(context, first, reminderTime)

                // For remaining tasks, schedule staggered WorkManager backups so they don't overlap
                // Stagger by 25 seconds per remaining task (keeps UX sensible)
                val staggerMs = 25_000L
                for ((index, task) in sorted.drop(1).withIndex()) {
                    val delay = (index + 1) * staggerMs
                    scheduleStaggeredWork(context, task.id, reminderTime, delay)
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Error processing alarm reminder: ${e.message}", e)
            }
        }
    }

    private fun triggerWorkAndServiceForTask(context: Context, task: TaskEntity, reminderTime: Long) {
        try {
            // PRIMARY: trigger WorkManager reminder immediately for the selected task
            val inputData = Data.Builder()
                .putInt("task_id", task.id)
                .putLong("reminder_time", reminderTime)
                .putBoolean("from_alarm", true)
                .putBoolean("background_trigger", true)
                .build()

            val reminderWork = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInputData(inputData)
                .addTag("alarm_reminder_${task.id}")
                .build()

            WorkManager.getInstance(context).enqueue(reminderWork)
            Log.d(TAG, "✅ PRIMARY: WorkManager reminder triggered from alarm for task ${task.id}")

            // BACKUP: also start persistent foreground service for a stronger alarm-like reminder
            val serviceIntent = Intent(context, com.pharma.taskmanager.services.PersistentReminderService::class.java).apply {
                putExtra("task_id", task.id)
                putExtra("reminder_time", reminderTime)
                putExtra("from_alarm", true)
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            Log.d(TAG, "✅ BACKUP: Foreground service started for persistent notification (task ${task.id})")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to trigger work/service for task ${task.id}: ${e.message}", e)
        }
    }

    private suspend fun scheduleStaggeredWork(context: Context, taskId: Int, reminderTime: Long, delayMs: Long) {
        withContext(Dispatchers.IO) {
            try {
                val inputData = Data.Builder()
                    .putInt("task_id", taskId)
                    .putLong("reminder_time", reminderTime)
                    .putBoolean("from_alarm", true)
                    .putBoolean("is_backup", true)
                    .build()

                val work = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                    .setInitialDelay(delayMs, java.util.concurrent.TimeUnit.MILLISECONDS)
                    .setInputData(inputData)
                    .addTag("staggered_reminder_$taskId")
                    .build()

                WorkManager.getInstance(context).enqueue(work)
                Log.d(TAG, "🔁 Scheduled staggered reminder for task $taskId with delay ${delayMs}ms")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Failed to schedule staggered work for task $taskId: ${e.message}", e)
            }
        }
    }

    companion object {
        private const val TAG = "ReminderBroadcastReceiver"
    }
}