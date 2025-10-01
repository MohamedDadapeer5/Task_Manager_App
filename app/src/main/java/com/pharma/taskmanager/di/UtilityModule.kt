package com.pharma.taskmanager.di

import android.content.Context
import com.pharma.taskmanager.utils.AlarmReminderScheduler
import com.pharma.taskmanager.utils.DateTimeUtils
import com.pharma.taskmanager.utils.ReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides utility classes and helper dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {
    
    /**
     * Provides DateTimeUtils as a singleton.
     * Even though DateTimeUtils is an object, we can provide it through Hilt
     * for consistent dependency injection pattern.
     */
    @Provides
    @Singleton
    fun provideDateTimeUtils(): DateTimeUtils {
        return DateTimeUtils
    }
    
    /**
     * Provides AlarmReminderScheduler as a singleton.
     */
    @Provides
    @Singleton
    fun provideAlarmReminderScheduler(@ApplicationContext context: Context): AlarmReminderScheduler {
        return AlarmReminderScheduler(context)
    }
    
    /**
     * Provides ReminderScheduler as a singleton.
     */
    @Provides
    @Singleton
    fun provideReminderScheduler(
        @ApplicationContext context: Context,
        alarmReminderScheduler: AlarmReminderScheduler
    ): ReminderScheduler {
        return ReminderScheduler(context, alarmReminderScheduler)
    }
}

// Note: TaskHelper is automatically provided by Hilt because it has an @Inject constructor
// No need to explicitly provide it in a module