package com.pharma.taskmanager.domain.usecase;

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
public final class TaskUseCases_Factory implements Factory<TaskUseCases> {
  private final Provider<GetTasksUseCase> getTasksProvider;

  private final Provider<AddTaskUseCase> addTaskProvider;

  private final Provider<UpdateTaskUseCase> updateTaskProvider;

  private final Provider<DeleteTaskUseCase> deleteTaskProvider;

  private final Provider<GetTaskByIdUseCase> getTaskByIdProvider;

  public TaskUseCases_Factory(Provider<GetTasksUseCase> getTasksProvider,
      Provider<AddTaskUseCase> addTaskProvider, Provider<UpdateTaskUseCase> updateTaskProvider,
      Provider<DeleteTaskUseCase> deleteTaskProvider,
      Provider<GetTaskByIdUseCase> getTaskByIdProvider) {
    this.getTasksProvider = getTasksProvider;
    this.addTaskProvider = addTaskProvider;
    this.updateTaskProvider = updateTaskProvider;
    this.deleteTaskProvider = deleteTaskProvider;
    this.getTaskByIdProvider = getTaskByIdProvider;
  }

  @Override
  public TaskUseCases get() {
    return newInstance(getTasksProvider.get(), addTaskProvider.get(), updateTaskProvider.get(), deleteTaskProvider.get(), getTaskByIdProvider.get());
  }

  public static TaskUseCases_Factory create(Provider<GetTasksUseCase> getTasksProvider,
      Provider<AddTaskUseCase> addTaskProvider, Provider<UpdateTaskUseCase> updateTaskProvider,
      Provider<DeleteTaskUseCase> deleteTaskProvider,
      Provider<GetTaskByIdUseCase> getTaskByIdProvider) {
    return new TaskUseCases_Factory(getTasksProvider, addTaskProvider, updateTaskProvider, deleteTaskProvider, getTaskByIdProvider);
  }

  public static TaskUseCases newInstance(GetTasksUseCase getTasks, AddTaskUseCase addTask,
      UpdateTaskUseCase updateTask, DeleteTaskUseCase deleteTask, GetTaskByIdUseCase getTaskById) {
    return new TaskUseCases(getTasks, addTask, updateTask, deleteTask, getTaskById);
  }
}
