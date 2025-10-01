package com.pharma.taskmanager;

import androidx.hilt.work.HiltWorkerFactory;
import com.pharma.taskmanager.domain.repository.TaskRepository;
import com.pharma.taskmanager.utils.ReminderScheduler;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class TaskManagerApplication_MembersInjector implements MembersInjector<TaskManagerApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<TaskRepository> taskRepositoryProvider;

  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  public TaskManagerApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<TaskRepository> taskRepositoryProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.taskRepositoryProvider = taskRepositoryProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
  }

  public static MembersInjector<TaskManagerApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<TaskRepository> taskRepositoryProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    return new TaskManagerApplication_MembersInjector(workerFactoryProvider, taskRepositoryProvider, reminderSchedulerProvider);
  }

  @Override
  public void injectMembers(TaskManagerApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectTaskRepository(instance, taskRepositoryProvider.get());
    injectReminderScheduler(instance, reminderSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.pharma.taskmanager.TaskManagerApplication.workerFactory")
  public static void injectWorkerFactory(TaskManagerApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.pharma.taskmanager.TaskManagerApplication.taskRepository")
  public static void injectTaskRepository(TaskManagerApplication instance,
      TaskRepository taskRepository) {
    instance.taskRepository = taskRepository;
  }

  @InjectedFieldSignature("com.pharma.taskmanager.TaskManagerApplication.reminderScheduler")
  public static void injectReminderScheduler(TaskManagerApplication instance,
      ReminderScheduler reminderScheduler) {
    instance.reminderScheduler = reminderScheduler;
  }
}
