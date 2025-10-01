package com.pharma.taskmanager.di

import android.content.Context
import androidx.room.Room
import com.pharma.taskmanager.data.database.TaskDao
import com.pharma.taskmanager.data.database.TaskManagerDatabase
import com.pharma.taskmanager.data.repository.TaskRepositoryImpl
import com.pharma.taskmanager.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides database-related dependencies.
 * This module is installed in SingletonComponent to ensure database instances
 * are singletons throughout the application lifecycle.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides the Room database instance.
     * @param context Application context for database creation
     * @return TaskManagerDatabase singleton instance
     */
    @Provides
    @Singleton
    fun provideTaskManagerDatabase(
        @ApplicationContext context: Context
    ): TaskManagerDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TaskManagerDatabase::class.java,
            TaskManagerDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // For development - remove in production
            .build()
    }
    
    /**
     * Provides the TaskDao from the database.
     * @param database The TaskManagerDatabase instance
     * @return TaskDao for database operations
     */
    @Provides
    fun provideTaskDao(database: TaskManagerDatabase): TaskDao {
        return database.taskDao()
    }
    
    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ): com.pharma.taskmanager.utils.NotificationHelper {
        return com.pharma.taskmanager.utils.NotificationHelper(context)
    }
}

/**
 * Hilt module that binds repository implementations to their interfaces.
 * This module uses @Binds to tell Hilt which implementation to use
 * when an interface is requested.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    /**
     * Binds TaskRepositoryImpl to TaskRepository interface.
     * @param taskRepositoryImpl The concrete implementation
     * @return TaskRepository interface
     */
    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository
}