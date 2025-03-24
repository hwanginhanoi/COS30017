// TaskListScreen.kt
package com.luutuanhoang.assignment3.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.luutuanhoang.assignment3.data.Task
import com.luutuanhoang.assignment3.ui.TaskViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.graphics.Color
import com.luutuanhoang.assignment3.data.TaskPriority
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    taskViewModel: TaskViewModel,
    onAddTask: () -> Unit,
    onEditTask: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val allTasks by taskViewModel.allTasks.collectAsState(initial = emptyList())
    val uncompletedTasks = allTasks.filter { !it.isCompleted }
    val scope = rememberCoroutineScope()

    // Track tasks being completed or deleted with animation
    val tasksBeingCompleted = remember { mutableStateListOf<Int>() }
    val tasksBeingDeleted = remember { mutableStateListOf<Int>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Active Tasks",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uncompletedTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks to complete!",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 60.dp),
                    state = rememberLazyListState()
                ) {
                    items(
                        items = uncompletedTasks,
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

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val contentAlpha = if (isPressed) 0.6f else 1f

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 24.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onAddTask
                    )
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add new task",
                    color = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskClick: () -> Unit,
    onToggleCompleted: () -> Unit,
    onDeleteTask: () -> Unit
) {
    // Define more distinct priority colors
    val cardColor = when (task.priority) {
        TaskPriority.HIGH -> Color(0xFFF8D7DA) // Light red background
        TaskPriority.MEDIUM -> Color(0xFFFFF3CD) // Light yellow background
        TaskPriority.LOW -> Color(0xFFD1E7DD) // Light green background
    }

    // Text color that corresponds to priority
    val priorityTextColor = when (task.priority) {
        TaskPriority.HIGH -> Color(0xFFDC3545) // Darker red for text
        TaskPriority.MEDIUM -> Color(0xFFFF9800) // Darker orange for text
        TaskPriority.LOW -> Color(0xFF28A745) // Darker green for text
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTaskClick),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Task,
                contentDescription = null,
                tint = priorityTextColor,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize * 1.2f
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize * 1.1f
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Priority: ${task.priority}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.1f,
                        fontWeight = FontWeight.Bold
                    ),
                    color = priorityTextColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Due: ${task.dueDate}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.1f,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = onToggleCompleted,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Mark as Completed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = onDeleteTask,
                modifier = Modifier.size(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}