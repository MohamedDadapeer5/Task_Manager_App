package com.pharma.taskmanager.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TaskManagerDatabase : RoomDatabase() {
    
    abstract fun taskDao(): TaskDao
    
    companion object {
        const val DATABASE_NAME = "task_manager_database"
        
        // Singleton instance
        @Volatile
        private var INSTANCE: TaskManagerDatabase? = null
        
        fun getDatabase(context: Context): TaskManagerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskManagerDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // For development - remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}