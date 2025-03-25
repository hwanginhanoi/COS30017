// AddEditTaskScreen.kt
package com.luutuanhoang.assignment3.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luutuanhoang.assignment3.data.Task
import com.luutuanhoang.assignment3.data.TaskPriority
import com.luutuanhoang.assignment3.ui.TaskViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskViewModel: TaskViewModel,
    taskId: Int = -1,
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var task by remember { mutableStateOf(Task(0, "", "", TaskPriority.MEDIUM, null, false)) }
    var isEditing by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    val dateTimeFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a") }

    // For time picker
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var selectedHour by remember { mutableIntStateOf(12) }
    var selectedMinute by remember { mutableIntStateOf(0) }

    // Validation states
    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var dueDateError by remember { mutableStateOf<String?>(null) }

    // Form validity check
    val isFormValid by remember {
        derivedStateOf {
            task.title.isNotBlank() && titleError == null &&
                    descriptionError == null && dueDateError == null
        }
    }

    LaunchedEffect(taskId) {
        if (taskId != -1) {
            taskViewModel.getTaskById(taskId)?.let {
                task = it
                isEditing = true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateFields() {
        titleError = when {
            task.title.isBlank() -> "Title cannot be empty"
            task.title.length < 3 -> "Title must be at least 3 characters"
            else -> null
        }

        descriptionError = when {
            task.description.length > 500 -> "Description is too long (max 500 characters)"
            else -> null
        }

        val dueDate = task.dueDate
        dueDateError = when {
            dueDate != null && dueDate.isBefore(LocalDateTime.now()) && !task.isCompleted ->
                "Due date cannot be in the past for incomplete tasks"
            else -> null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Task" else "Add Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = task.title,
                onValueChange = {
                    task = task.copy(title = it)
                    if (titleError != null) validateFields()
                },
                label = { Text("Task Title") },
                isError = titleError != null,
                supportingText = { titleError?.let { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = task.description,
                onValueChange = {
                    task = task.copy(description = it)
                    if (descriptionError != null) validateFields()
                },
                label = { Text("Description") },
                isError = descriptionError != null,
                supportingText = { descriptionError?.let { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(100.dp)
            )

            // Priority dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = task.priority.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Priority") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    TaskPriority.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                task = task.copy(priority = option)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Due date and time field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = task.dueDate?.format(dateTimeFormatter) ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Due Date & Time") },
                    isError = dueDateError != null,
                    supportingText = { dueDateError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date & Time"
                        )
                    }
                )

                // Invisible overlay for the click
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDateTimePicker = true }
                )
            }

            Button(
                onClick = {
                    validateFields()
                    if (isFormValid) {
                        scope.launch {
                            if (isEditing) {
                                taskViewModel.updateTask(task)
                            } else {
                                taskViewModel.insertTask(task)
                            }
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text(if (isEditing) "Update Task" else "Add Task")
            }
        }
    }

    // Date picker dialog that leads to time picker
    if (showDateTimePicker) {
        val initialMillis = task.dueDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
            ?: System.currentTimeMillis()

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDateTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = LocalDateTime.ofInstant(
                                java.time.Instant.ofEpochMilli(millis),
                                ZoneId.systemDefault()
                            )

                            // Store the selected date temporarily
                            selectedDate = date

                            // Set initial time values based on existing task or current time
                            task.dueDate?.let {
                                selectedHour = it.hour
                                selectedMinute = it.minute
                            } ?: run {
                                val now = LocalDateTime.now()
                                selectedHour = now.hour
                                selectedMinute = now.minute
                            }

                            // Close date picker and open time picker
                            showDateTimePicker = false
                            showTimePicker = true
                        }
                    }
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateTimePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time picker dialog
    if (showTimePicker && selectedDate != null) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Combine date and time
                        val combinedDateTime = selectedDate!!.withHour(timePickerState.hour)
                            .withMinute(timePickerState.minute)

                        // Update task with the new datetime
                        task = task.copy(dueDate = combinedDateTime)
                        if (dueDateError != null) validateFields()
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}