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
public final class GetTasksUseCase_Factory implements Factory<GetTasksUseCase> {
  private final Provider<TaskRepository> repositoryProvider;

  public GetTasksUseCase_Factory(Provider<TaskRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetTasksUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetTasksUseCase_Factory create(Provider<TaskRepository> repositoryProvider) {
    return new GetTasksUseCase_Factory(repositoryProvider);
  }

  public static GetTasksUseCase newInstance(TaskRepository repository) {
    return new GetTasksUseCase(repository);
  }
}
