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
public final class AlarmReminderScheduler_Factory implements Factory<AlarmReminderScheduler> {
  private final Provider<Context> contextProvider;

  public AlarmReminderScheduler_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AlarmReminderScheduler get() {
    return newInstance(contextProvider.get());
  }

  public static AlarmReminderScheduler_Factory create(Provider<Context> contextProvider) {
    return new AlarmReminderScheduler_Factory(contextProvider);
  }

  public static AlarmReminderScheduler newInstance(Context context) {
    return new AlarmReminderScheduler(context);
  }
}
