package com.pharma.taskmanager.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TaskList : Screen("task_list")
    object TaskDetail : Screen("task_detail") {
        fun createTaskDetailRoute(taskId: Int): String = "task_detail/$taskId"
    }
    object TaskCreate : Screen("task_create")
}