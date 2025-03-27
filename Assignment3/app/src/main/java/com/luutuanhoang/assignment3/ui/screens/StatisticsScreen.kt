package com.luutuanhoang.assignment3.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import com.luutuanhoang.assignment3.data.TaskPriority
import com.luutuanhoang.assignment3.ui.TaskViewModel
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    taskViewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    val allTasks by taskViewModel.allTasks.collectAsState(initial = emptyList())

    // Calculate statistics
    val totalTasks = allTasks.size
    val completedTasks = allTasks.count { it.isCompleted }
    val incompleteTasks = totalTasks - completedTasks
    val completionRate = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks * 100).toInt() else 0

    // Priority breakdown
    val highPriorityTasks = allTasks.count { it.priority == TaskPriority.HIGH }
    val mediumPriorityTasks = allTasks.count { it.priority == TaskPriority.MEDIUM }
    val lowPriorityTasks = allTasks.count { it.priority == TaskPriority.LOW }

    // Time-based statistics
    val tasksCompletedThisWeek = allTasks.count {
        it.isCompleted && it.completedDate?.isAfter(LocalDateTime.now().minusDays(7)) == true
    }

    val tasksCompletedToday = allTasks.count {
        it.isCompleted && it.completedDate?.toLocalDate()?.isEqual(LocalDate.now()) == true
    }

    // Prepare data for charts
    val completionData = if (totalTasks > 0) {
        listOf(
            PieChartData("Completed", completedTasks.toFloat(), Color(0xFF4CAF50)),
            PieChartData("Incomplete", incompleteTasks.toFloat(), Color(0xFFFF5722))
        )
    } else {
        listOf(PieChartData("No Tasks", 1f, Color.Gray))
    }

    val priorityData = if (totalTasks > 0) {
        listOf(
            PieChartData("High", highPriorityTasks.toFloat(), Color(0xFFE53935)),
            PieChartData("Medium", mediumPriorityTasks.toFloat(), Color(0xFFFFA726)),
            PieChartData("Low", lowPriorityTasks.toFloat(), Color(0xFF66BB6A))
        ).filter { it.value > 0 } // Only display segments with tasks
    } else {
        listOf(PieChartData("No Tasks", 1f, Color.Gray))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Statistics",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary stats card
            // Summary stats card
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Task Summary",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox(
                                title = "Total Tasks",
                                value = totalTasks.toString(),
                                icon = Icons.AutoMirrored.Filled.Assignment,
                                color = MaterialTheme.colorScheme.primary
                            )

                            StatBox(
                                title = "Completion Rate",
                                value = "$completionRate%",
                                icon = Icons.AutoMirrored.Filled.TrendingUp,
                                color = Color(0xFF4CAF50)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatBox(
                                title = "Completed Today",
                                value = tasksCompletedToday.toString(),
                                icon = Icons.Default.Today,
                                color = Color(0xFF2196F3)
                            )

                            StatBox(
                                title = "This Week",
                                value = tasksCompletedThisWeek.toString(),
                                icon = Icons.Default.DateRange,
                                color = Color(0xFF9C27B0)
                            )
                        }
                    }
                }
            }

            // Task Completion Chart
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Task Completion",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Pie chart
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                PieChart(
                                    data = completionData,
                                    showLabels = true
                                )
                            }

                            // Text statistics
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                StatisticItem(
                                    label = "Completed Tasks",
                                    value = "$completedTasks",
                                    color = Color(0xFF4CAF50)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                StatisticItem(
                                    label = "Incomplete Tasks",
                                    value = "$incompleteTasks",
                                    color = Color(0xFFFF5722)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                StatisticItem(
                                    label = "Completion Rate",
                                    value = "$completionRate%",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Priority Breakdown Chart
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Priority Breakdown",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Pie chart
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                PieChart(
                                    data = priorityData,
                                    showLabels = true
                                )
                            }

                            // Text statistics
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                StatisticItem(
                                    label = "High Priority",
                                    value = "$highPriorityTasks",
                                    color = Color(0xFFE53935)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                StatisticItem(
                                    label = "Medium Priority",
                                    value = "$mediumPriorityTasks",
                                    color = Color(0xFFFFA726)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                StatisticItem(
                                    label = "Low Priority",
                                    value = "$lowPriorityTasks",
                                    color = Color(0xFF66BB6A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .width(120.dp) // Fixed width to ensure consistent sizing
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color.copy(alpha = 0.1f), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StatisticItem(
    label: String,
    value: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}

@Composable
fun PieChart(
    data: List<PieChartData>,
    showLabels: Boolean = false
) {
    val total = data.sumOf { it.value.toDouble() }
    var startAngle = 0f

    Canvas(modifier = Modifier.size(200.dp)) {
        val canvasSize = size.minDimension
        val radius = canvasSize / 2.2f
        val centerX = size.width / 2
        val centerY = size.height / 2

        data.forEach { pieSlice ->
            val sweepAngle = 360f * (pieSlice.value / total.toFloat())

            // Draw slice
            drawArc(
                color = pieSlice.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = Size(canvasSize, canvasSize),
                topLeft = Offset(
                    (size.width - canvasSize) / 2,
                    (size.height - canvasSize) / 2
                )
            )

            // Draw outline
            drawArc(
                color = Color.White,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                style = Stroke(width = 2f),
                size = Size(canvasSize, canvasSize),
                topLeft = Offset(
                    (size.width - canvasSize) / 2,
                    (size.height - canvasSize) / 2
                )
            )

            if (showLabels && pieSlice.value / total.toFloat() > 0.05f) {
                // Calculate label position
                val middleAngle = startAngle + sweepAngle / 2
                val angleInRadians = middleAngle * PI / 180f
                val labelRadius = radius * 0.7f // Position labels inside the chart

                val labelX = centerX + cos(angleInRadians).toFloat() * labelRadius
                val labelY = centerY + sin(angleInRadians).toFloat() * labelRadius

                // Calculate percentage
                val percentage = (pieSlice.value / total.toFloat() * 100).toInt()

                // Draw percentage label
                drawContext.canvas.nativeCanvas.drawText(
                    "$percentage%",
                    labelX,
                    labelY,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 12.sp.toPx()
                        isFakeBoldText = true
                    }
                )
            }

            startAngle += sweepAngle
        }
    }
}

data class PieChartData(
    val label: String,
    val value: Float,
    val color: Color
)