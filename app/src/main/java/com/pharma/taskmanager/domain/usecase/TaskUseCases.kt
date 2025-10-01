package com.pharma.taskmanager.domain.usecase

import javax.inject.Inject

data class TaskUseCases @Inject constructor(
    val getTasks: GetTasksUseCase,
    val addTask: AddTaskUseCase,
    val updateTask: UpdateTaskUseCase,
    val deleteTask: DeleteTaskUseCase,
    val getTaskById: GetTaskByIdUseCase
)