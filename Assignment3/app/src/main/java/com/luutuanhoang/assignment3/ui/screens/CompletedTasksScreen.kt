package com.luutuanhoang.assignment3.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.luutuanhoang.assignment3.data.Task
import com.luutuanhoang.assignment3.data.TaskPriority
import com.luutuanhoang.assignment3.ui.TaskViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedTasksScreen(
    taskViewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    val allTasks by taskViewModel.allTasks.collectAsState(initial = emptyList())
    val completedTasks = allTasks.filter { it.isCompleted }

    // Group completed tasks by time period
    val now = LocalDateTime.now()
    val today = now.toLocalDate()
    val yesterday = today.minusDays(1)
    val startOfWeek = today.minusDays(today.dayOfWeek.value - 1L)
    val startOfMonth = today.withDayOfMonth(1)
    val startOfYear = today.withDayOfYear(1)

    val groupedTasks = completedTasks.groupBy { task ->
        val completedDate = task.completedDate?.toLocalDate() ?: return@groupBy "Unknown"

        when {
            completedDate.isEqual(today) -> "Today"
            completedDate.isEqual(yesterday) -> "Yesterday"
            completedDate.isAfter(startOfWeek) -> "This Week"
            completedDate.isAfter(startOfMonth) -> "This Month"
            completedDate.isAfter(startOfYear) -> "This Year"
            else -> completedDate.year.toString()
        }
    }.toSortedMap(compareBy {
        when (it) {
            "Today" -> 1
            "Yesterday" -> 2
            "This Week" -> 3
            "This Month" -> 4
            "This Year" -> 5
            else -> 1000 + (it.toIntOrNull() ?: 999)
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Completed Tasks",
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
            if (completedTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No completed tasks yet!",
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
                            CompletedTaskItem(
                                task = task,
                                onDeleteTask = { taskViewModel.deleteTask(task) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CompletedTaskItem(
    task: Task,
    onDeleteTask: () -> Unit
) {
    // Define priority colors
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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

                val completedDateStr = task.completedDate?.format(
                    DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")
                ) ?: "Unknown"

                Text(
                    text = "Completed: $completedDateStr",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.1f,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
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