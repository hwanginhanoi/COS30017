// TaskApp.kt
package com.luutuanhoang.assignment3.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.luutuanhoang.assignment3.TaskApplication
import com.luutuanhoang.assignment3.ui.screens.AddEditTaskScreen
import com.luutuanhoang.assignment3.ui.screens.CompletedTasksScreen
import com.luutuanhoang.assignment3.ui.screens.IndexScreen
import com.luutuanhoang.assignment3.ui.screens.StatisticsScreen
import com.luutuanhoang.assignment3.ui.screens.TaskListScreen
import com.luutuanhoang.assignment3.ui.screens.TodayTasksScreen

// TaskApp.kt (updated)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskApp(application: TaskApplication = LocalContext.current.applicationContext as TaskApplication) {
    val navController = rememberNavController()
    val taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(application.repository)
    )

    NavHost(navController = navController, startDestination = "index") {
        composable("index") {
            IndexScreen(
                onNavigateToTaskList = { navController.navigate("taskList") },
                onNavigateToCompletedTasks = { navController.navigate("completedTasks") },
                onNavigateToStatistics = { navController.navigate("statistics") },
                onNavigateToTodayTasks = { navController.navigate("todayTasks") }            )
        }

        composable("taskList") {
            TaskListScreen(
                taskViewModel = taskViewModel,
                onAddTask = { navController.navigate("addTask") },
                onEditTask = { taskId -> navController.navigate("editTask/$taskId") },
                onNavigateBack = { navController.navigate("index") {
                    popUpTo("index") { inclusive = true }
                }}
            )
        }

        composable("addTask") {
            AddEditTaskScreen(
                taskViewModel = taskViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            "editTask/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
            AddEditTaskScreen(
                taskViewModel = taskViewModel,
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("statistics") {
            StatisticsScreen(
                taskViewModel = taskViewModel,
                onNavigateBack = { navController.navigate("index") {
                    popUpTo("index") { inclusive = true }
                }}
            )
        }

        composable("completedTasks") {
            CompletedTasksScreen(
                taskViewModel = taskViewModel,
                onNavigateBack = { navController.navigate("index") {
                    popUpTo("index") { inclusive = true }
                }}
            )
        }

        composable("todayTasks") {
            TodayTasksScreen(
                taskViewModel = taskViewModel,
                onEditTask = { taskId ->
                    navController.navigate("editTask/$taskId")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}