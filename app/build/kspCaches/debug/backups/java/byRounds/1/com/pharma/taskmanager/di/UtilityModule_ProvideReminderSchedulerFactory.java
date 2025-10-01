package com.pharma.taskmanager.di;

import android.content.Context;
import com.pharma.taskmanager.utils.AlarmReminderScheduler;
import com.pharma.taskmanager.utils.ReminderScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class UtilityModule_ProvideReminderSchedulerFactory implements Factory<ReminderScheduler> {
  private final Provider<Context> contextProvider;

  private final Provider<AlarmReminderScheduler> alarmReminderSchedulerProvider;

  public UtilityModule_ProvideReminderSchedulerFactory(Provider<Context> contextProvider,
      Provider<AlarmReminderScheduler> alarmReminderSchedulerProvider) {
    this.contextProvider = contextProvider;
    this.alarmReminderSchedulerProvider = alarmReminderSchedulerProvider;
  }

  @Override
  public ReminderScheduler get() {
    return provideReminderScheduler(contextProvider.get(), alarmReminderSchedulerProvider.get());
  }

  public static UtilityModule_ProvideReminderSchedulerFactory create(
      Provider<Context> contextProvider,
      Provider<AlarmReminderScheduler> alarmReminderSchedulerProvider) {
    return new UtilityModule_ProvideReminderSchedulerFactory(contextProvider, alarmReminderSchedulerProvider);
  }

  public static ReminderScheduler provideReminderScheduler(Context context,
      AlarmReminderScheduler alarmReminderScheduler) {
    return Preconditions.checkNotNullFromProvides(UtilityModule.INSTANCE.provideReminderScheduler(context, alarmReminderScheduler));
  }
}
