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
 * Test module that replaces DatabaseModule for unit/integration tests.
 * This module provides an in-memory database for testing purposes.
 * 
 * Key features:
 * - Uses @TestInstallIn to replace the production DatabaseModule
 * - Provides in-memory database that's destroyed after tests
 * - Allows main thread queries for simplified testing
 * - Maintains singleton scope for test consistency
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {
    
    /**
     * Provides an in-memory Room database for testing.
     * This database is destroyed when the test process ends.
     * 
     * @param context Test application context
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
     * Provides a test version of NotificationHelper.
     * This can be a mock or a real instance depending on your testing needs.
     * 
     * @param context Test application context
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

/**
 * Alternative approach: Create fake implementations for testing
 */
/*
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class TestRepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindTestTaskRepository(
        fakeTaskRepository: FakeTaskRepository
    ): TaskRepository
}

// You would need to create FakeTaskRepository that implements TaskRepository
// This approach is useful when you want to test with fake data instead of a real database
*/