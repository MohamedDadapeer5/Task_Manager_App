package com.pharma.taskmanager.workers;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.pharma.taskmanager.domain.repository.TaskRepository;
import com.pharma.taskmanager.utils.NotificationHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class TaskReminderWorker_Factory {
  private final Provider<TaskRepository> taskRepositoryProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  public TaskReminderWorker_Factory(Provider<TaskRepository> taskRepositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    this.taskRepositoryProvider = taskRepositoryProvider;
    this.notificationHelperProvider = notificationHelperProvider;
  }

  public TaskReminderWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, taskRepositoryProvider.get(), notificationHelperProvider.get());
  }

  public static TaskReminderWorker_Factory create(Provider<TaskRepository> taskRepositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    return new TaskReminderWorker_Factory(taskRepositoryProvider, notificationHelperProvider);
  }

  public static TaskReminderWorker newInstance(Context context, WorkerParameters workerParams,
      TaskRepository taskRepository, NotificationHelper notificationHelper) {
    return new TaskReminderWorker(context, workerParams, taskRepository, notificationHelper);
  }
}
