package com.luutuanhoang.assignment3.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.luutuanhoang.assignment3.data.Task
import com.luutuanhoang.assignment3.ui.TaskViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayTasksScreen(
    taskViewModel: TaskViewModel,
    onEditTask: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val allTasks by taskViewModel.allTasks.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // Track tasks being completed or deleted with animation
    val tasksBeingCompleted = remember { mutableStateListOf<Int>() }
    val tasksBeingDeleted = remember { mutableStateListOf<Int>() }

    // Filter tasks due today
    val today = LocalDate.now()
    val todayTasks = allTasks.filter { task ->
        !task.isCompleted &&
                task.dueDate?.toLocalDate()?.isEqual(today) == true
    }

    // Group tasks by time of day
    val morningTasks = todayTasks.filter { it.dueDate?.hour in 5..11 }
    val eveningTasks = todayTasks.filter { it.dueDate?.hour in 12..17 }
    val nightTasks = todayTasks.filter {
        val hour = it.dueDate?.hour ?: 0
        hour >= 18 || hour < 5
    }

    val groupedTasks = listOf<Pair<String, List<Task>>>(
        "Morning (5 AM - 11 AM)" to morningTasks,
        "Evening (12 PM - 5 PM)" to eveningTasks,
        "Night (6 PM - 4 AM)" to nightTasks
    ).filter { (_, tasks) -> tasks.isNotEmpty() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Today's Tasks",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (todayTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks due today!",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    groupedTasks.forEach { (timePeriod, tasks) ->
                        item {
                            Text(
                                text = timePeriod,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(
                            items = tasks,
                            key = { task -> task.id }
                        ) { task ->
                            val isBeingCompleted = tasksBeingCompleted.contains(task.id)
                            val isBeingDeleted = tasksBeingDeleted.contains(task.id)
                            val isAnimating = isBeingCompleted || isBeingDeleted

                            AnimatedVisibility(
                                visible = !isAnimating,
                                exit = when {
                                    isBeingCompleted -> slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> fullWidth },
                                        animationSpec = tween(durationMillis = 500)
                                    ) + fadeOut(
                                        animationSpec = tween(durationMillis = 500)
                                    )
                                    else -> slideOutHorizontally(
                                        targetOffsetX = { fullWidth -> -fullWidth },
                                        animationSpec = tween(durationMillis = 500)
                                    ) + fadeOut(
                                        animationSpec = tween(durationMillis = 500)
                                    )
                                }
                            ) {
                                TaskItem(
                                    task = task,
                                    onTaskClick = { onEditTask(task.id) },
                                    onToggleCompleted = {
                                        tasksBeingCompleted.add(task.id)
                                        scope.launch {
                                            delay(500)
                                            taskViewModel.updateTask(task.copy(
                                                isCompleted = true,
                                                completedDate = LocalDateTime.now()
                                            ))
                                            tasksBeingCompleted.remove(task.id)
                                        }
                                    },
                                    onDeleteTask = {
                                        tasksBeingDeleted.add(task.id)
                                        scope.launch {
                                            delay(500)
                                            taskViewModel.deleteTask(task)
                                            tasksBeingDeleted.remove(task.id)
                                        }
                                    }
                                )
                            }

                            if (!isAnimating) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}