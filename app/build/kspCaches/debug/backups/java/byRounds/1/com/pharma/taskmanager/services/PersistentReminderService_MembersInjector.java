package com.pharma.taskmanager.services;

import com.pharma.taskmanager.domain.repository.TaskRepository;
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
public final class PersistentReminderService_MembersInjector implements MembersInjector<PersistentReminderService> {
  private final Provider<TaskRepository> taskRepositoryProvider;

  public PersistentReminderService_MembersInjector(
      Provider<TaskRepository> taskRepositoryProvider) {
    this.taskRepositoryProvider = taskRepositoryProvider;
  }

  public static MembersInjector<PersistentReminderService> create(
      Provider<TaskRepository> taskRepositoryProvider) {
    return new PersistentReminderService_MembersInjector(taskRepositoryProvider);
  }

  @Override
  public void injectMembers(PersistentReminderService instance) {
    injectTaskRepository(instance, taskRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.pharma.taskmanager.services.PersistentReminderService.taskRepository")
  public static void injectTaskRepository(PersistentReminderService instance,
      TaskRepository taskRepository) {
    instance.taskRepository = taskRepository;
  }
}
