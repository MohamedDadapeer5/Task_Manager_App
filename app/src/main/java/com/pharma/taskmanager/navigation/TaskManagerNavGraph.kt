package com.pharma.taskmanager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pharma.taskmanager.ui.screens.home.HomeScreen
import com.pharma.taskmanager.ui.screens.tasks.TaskListScreen
import com.pharma.taskmanager.ui.screens.tasks.TaskDetailScreen
import com.pharma.taskmanager.ui.screens.tasks.TaskCreateScreen
import com.pharma.taskmanager.ui.screens.tasks.AddEditTaskScreen

@Composable
fun TaskManagerNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTasks = {
                    navController.navigate(Screen.TaskList.route)
                }
            )
        }
        
        // Task List Screen
        composable(Screen.TaskList.route) {
            TaskListScreen(
                onNavigateToCreateTask = {
                    navController.navigate(Screen.TaskCreate.route)
                },
                onNavigateToTaskDetail = { taskId ->
                    navController.navigate(Screen.TaskDetail.createTaskDetailRoute(taskId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Task Detail Screen with taskId parameter
        composable(
            route = "${Screen.TaskDetail.route}/{taskId}",
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEdit = { editTaskId ->
                    navController.navigate("edit_task/$editTaskId")
                }
            )
        }
        
        // Task Create Screen
        composable(Screen.TaskCreate.route) {
            TaskCreateScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Legacy Edit Task Screen (keeping for backward compatibility)
        composable("edit_task/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            AddEditTaskScreen(
                taskId = taskId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}