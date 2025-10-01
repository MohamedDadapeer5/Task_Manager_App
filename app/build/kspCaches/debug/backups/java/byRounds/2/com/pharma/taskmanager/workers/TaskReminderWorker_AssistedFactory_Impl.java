package com.pharma.taskmanager.workers;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class TaskReminderWorker_AssistedFactory_Impl implements TaskReminderWorker_AssistedFactory {
  private final TaskReminderWorker_Factory delegateFactory;

  TaskReminderWorker_AssistedFactory_Impl(TaskReminderWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public TaskReminderWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<TaskReminderWorker_AssistedFactory> create(
      TaskReminderWorker_Factory delegateFactory) {
    return InstanceFactory.create(new TaskReminderWorker_AssistedFactory_Impl(delegateFactory));
  }
}
