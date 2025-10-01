package com.pharma.taskmanager.ui.viewmodel;

import com.pharma.taskmanager.domain.usecase.TaskUseCases;
import com.pharma.taskmanager.utils.NotificationHelper;
import com.pharma.taskmanager.utils.ReminderScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class TaskViewModel_Factory implements Factory<TaskViewModel> {
  private final Provider<TaskUseCases> taskUseCasesProvider;

  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  public TaskViewModel_Factory(Provider<TaskUseCases> taskUseCasesProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    this.taskUseCasesProvider = taskUseCasesProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
    this.notificationHelperProvider = notificationHelperProvider;
  }

  @Override
  public TaskViewModel get() {
    return newInstance(taskUseCasesProvider.get(), reminderSchedulerProvider.get(), notificationHelperProvider.get());
  }

  public static TaskViewModel_Factory create(Provider<TaskUseCases> taskUseCasesProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    return new TaskViewModel_Factory(taskUseCasesProvider, reminderSchedulerProvider, notificationHelperProvider);
  }

  public static TaskViewModel newInstance(TaskUseCases taskUseCases,
      ReminderScheduler reminderScheduler, NotificationHelper notificationHelper) {
    return new TaskViewModel(taskUseCases, reminderScheduler, notificationHelper);
  }
}
