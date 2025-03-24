// TaskApplication.kt
package com.luutuanhoang.assignment3

import android.app.Application
import com.luutuanhoang.assignment3.data.TaskDatabase
import com.luutuanhoang.assignment3.data.TaskRepository

class TaskApplication : Application() {
    val database by lazy { TaskDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database.taskDao()) }
}