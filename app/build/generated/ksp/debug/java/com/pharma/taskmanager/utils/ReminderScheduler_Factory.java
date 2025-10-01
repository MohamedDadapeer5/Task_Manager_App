package com.pharma.taskmanager.utils;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class ReminderScheduler_Factory implements Factory<ReminderScheduler> {
  private final Provider<Context> contextProvider;

  private final Provider<AlarmReminderScheduler> alarmReminderSchedulerProvider;

  public ReminderScheduler_Factory(Provider<Context> contextProvider,
      Provider<AlarmReminderScheduler> alarmReminderSchedulerProvider) {
    this.contextProvider = contextProvider;
    this.alarmReminderSchedulerProvider = alarmReminderSchedulerProvider;
  }

  @Override
  public ReminderScheduler get() {
    return newInstance(contextProvider.get(), alarmReminderSchedulerProvider.get());
  }

  public static ReminderScheduler_Factory create(Provider<Context> contextProvider,
      Provider<AlarmReminderScheduler> alarmReminderSchedulerProvider) {
    return new ReminderScheduler_Factory(contextProvider, alarmReminderSchedulerProvider);
  }

  public static ReminderScheduler newInstance(Context context,
      AlarmReminderScheduler alarmReminderScheduler) {
    return new ReminderScheduler(context, alarmReminderScheduler);
  }
}
