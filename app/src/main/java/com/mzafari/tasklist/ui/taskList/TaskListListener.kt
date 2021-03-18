package com.mzafari.tasklist.ui.taskList

import android.view.View
import com.mzafari.tasklist.model.Task

interface TaskListListener {
    fun onTaskEditClicked(view: View, task: Task)
    fun onTaskDeleteClicked(task:Task)
}