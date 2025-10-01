package com.pharma.taskmanager.di

import android.content.Context
import androidx.room.Room
import com.pharma.taskmanager.data.database.TaskDao
import com.pharma.taskmanager.data.database.TaskManagerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * Test module that replaces DatabaseModule for instrumented/UI tests.
 * This module provides an in-memory database for Android testing purposes.
 * 
 * This is specifically for androidTest directory and instrumented tests.
 * Key differences from unit test version:
 * - Runs on actual Android device/emulator
 * - Can test full Android components integration
 * - Uses real Android Context
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {
    
    /**
     * Provides an in-memory Room database for instrumented testing.
     * This database is destroyed when the test process ends.
     * 
     * @param context Instrumented test application context
     * @return In-memory TaskManagerDatabase instance
     */
    @Provides
    @Singleton
    fun provideTaskManagerDatabase(
        @ApplicationContext context: Context
    ): TaskManagerDatabase {
        return Room.inMemoryDatabaseBuilder(
            context.applicationContext,
            TaskManagerDatabase::class.java
        )
            .allowMainThreadQueries() // Allow database operations on main thread for testing
            .fallbackToDestructiveMigration() // Ensure clean state between tests
            .build()
    }
    
    /**
     * Provides TaskDao from the test database.
     * 
     * @param database The test TaskManagerDatabase instance
     * @return TaskDao for test database operations
     */
    @Provides
    fun provideTaskDao(database: TaskManagerDatabase): TaskDao {
        return database.taskDao()
    }
    
    /**
     * Provides a test version of NotificationHelper for instrumented tests.
     * Uses real Android context but can be configured for testing behavior.
     * 
     * @param context Instrumented test application context
     * @return NotificationHelper instance for testing
     */
    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ): com.pharma.taskmanager.utils.NotificationHelper {
        return com.pharma.taskmanager.utils.NotificationHelper(context)
    }
}