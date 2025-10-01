package com.pharma.taskmanager.di;

import android.content.Context;
import com.pharma.taskmanager.utils.AlarmReminderScheduler;
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
public final class UtilityModule_ProvideAlarmReminderSchedulerFactory implements Factory<AlarmReminderScheduler> {
  private final Provider<Context> contextProvider;

  public UtilityModule_ProvideAlarmReminderSchedulerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AlarmReminderScheduler get() {
    return provideAlarmReminderScheduler(contextProvider.get());
  }

  public static UtilityModule_ProvideAlarmReminderSchedulerFactory create(
      Provider<Context> contextProvider) {
    return new UtilityModule_ProvideAlarmReminderSchedulerFactory(contextProvider);
  }

  public static AlarmReminderScheduler provideAlarmReminderScheduler(Context context) {
    return Preconditions.checkNotNullFromProvides(UtilityModule.INSTANCE.provideAlarmReminderScheduler(context));
  }
}
