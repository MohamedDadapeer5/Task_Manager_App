package com.pharma.taskmanager.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

/**
 * Navigation extensions for type-safe navigation throughout the app.
 * These extension functions provide a clean API for navigation actions.
 */

// Navigation to specific screens
fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    navigate(Screen.Home.route, navOptions)
}

fun NavController.navigateToTaskList(navOptions: NavOptions? = null) {
    navigate(Screen.TaskList.route, navOptions)
}

fun NavController.navigateToTaskDetail(taskId: Int, navOptions: NavOptions? = null) {
    navigate(Screen.TaskDetail.createTaskDetailRoute(taskId), navOptions)
}

fun NavController.navigateToTaskCreate(navOptions: NavOptions? = null) {
    navigate(Screen.TaskCreate.route, navOptions)
}

fun NavController.navigateToEditTask(taskId: Int, navOptions: NavOptions? = null) {
    navigate("edit_task/$taskId", navOptions)
}

// Common navigation patterns
fun NavController.navigateAndClearBackStack(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            inclusive = true
        }
    }
}

fun NavController.navigateAndPopTo(route: String, popToRoute: String, inclusive: Boolean = false) {
    navigate(route) {
        popUpTo(popToRoute) {
            this.inclusive = inclusive
        }
    }
}

// Safe back navigation
fun NavController.safePopBackStack(): Boolean {
    return if (previousBackStackEntry != null) {
        popBackStack()
    } else {
        false
    }
}

// Navigate up with fallback
fun NavController.navigateUpOrBack(fallbackRoute: String) {
    if (!navigateUp()) {
        navigate(fallbackRoute) {
            popUpTo(graph.startDestinationId)
        }
    }
}

/**
 * Navigation result handling
 */
object NavigationKeys {
    const val TASK_CREATED = "task_created"
    const val TASK_UPDATED = "task_updated"
    const val TASK_DELETED = "task_deleted"
}

// Set navigation result
fun NavController.setNavigationResult(key: String, value: Any) {
    previousBackStackEntry?.savedStateHandle?.set(key, value)
}

// Get navigation result
inline fun <reified T> NavController.getNavigationResult(key: String): T? {
    return currentBackStackEntry?.savedStateHandle?.get<T>(key)
}

// Remove navigation result after consumption
fun NavController.clearNavigationResult(key: String) {
    currentBackStackEntry?.savedStateHandle?.remove<Any>(key)
}