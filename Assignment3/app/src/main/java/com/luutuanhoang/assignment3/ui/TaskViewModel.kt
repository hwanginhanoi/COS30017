// TaskViewModel.kt
package com.luutuanhoang.assignment3.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.luutuanhoang.assignment3.data.Task
import com.luutuanhoang.assignment3.data.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    val allTasks: Flow<List<Task>> = repository.allTasks

    suspend fun getTaskById(id: Int): Task? {
        return repository.getTaskById(id)
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}