package com.mzafari.tasklist.repository

import androidx.annotation.WorkerThread
import com.mzafari.tasklist.database.MainDao
import com.mzafari.tasklist.model.Task
import com.mzafari.tasklist.model.TaskStatus
import kotlinx.coroutines.flow.Flow

/**
 * Provide all task data & separating the data from the UI
 */
class TaskRepository(private val dao:MainDao) {

    //access tasks and notifies when there is a changes
    val allTasks: Flow<List<Task>> = dao.getAllTasks()
    val doneTasks: Flow<List<Task>> = dao.getTaskByStatus(TaskStatus.done)
    val onProgressTasks: Flow<List<Task>> = dao.getTaskByStatus(TaskStatus.onProgress)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTask(task:Task){
        dao.insertTask(task)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteTask(task:Task){
        dao.deleteTask(task)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateTask(task:Task){
        dao.updateTask(task)
    }
}