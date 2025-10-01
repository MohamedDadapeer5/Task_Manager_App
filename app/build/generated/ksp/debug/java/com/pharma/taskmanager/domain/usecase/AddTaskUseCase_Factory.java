package com.pharma.taskmanager.domain.usecase;

import com.pharma.taskmanager.domain.repository.TaskRepository;
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
public final class AddTaskUseCase_Factory implements Factory<AddTaskUseCase> {
  private final Provider<TaskRepository> repositoryProvider;

  public AddTaskUseCase_Factory(Provider<TaskRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AddTaskUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static AddTaskUseCase_Factory create(Provider<TaskRepository> repositoryProvider) {
    return new AddTaskUseCase_Factory(repositoryProvider);
  }

  public static AddTaskUseCase newInstance(TaskRepository repository) {
    return new AddTaskUseCase(repository);
  }
}
