package com.pharma.taskmanager.utils;

import com.pharma.taskmanager.domain.repository.TaskRepository;
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
public final class TaskHelper_Factory implements Factory<TaskHelper> {
  private final Provider<TaskRepository> repositoryProvider;

  private final Provider<DateTimeUtils> dateTimeUtilsProvider;

  public TaskHelper_Factory(Provider<TaskRepository> repositoryProvider,
      Provider<DateTimeUtils> dateTimeUtilsProvider) {
    this.repositoryProvider = repositoryProvider;
    this.dateTimeUtilsProvider = dateTimeUtilsProvider;
  }

  @Override
  public TaskHelper get() {
    return newInstance(repositoryProvider.get(), dateTimeUtilsProvider.get());
  }

  public static TaskHelper_Factory create(Provider<TaskRepository> repositoryProvider,
      Provider<DateTimeUtils> dateTimeUtilsProvider) {
    return new TaskHelper_Factory(repositoryProvider, dateTimeUtilsProvider);
  }

  public static TaskHelper newInstance(TaskRepository repository, DateTimeUtils dateTimeUtils) {
    return new TaskHelper(repository, dateTimeUtils);
  }
}
