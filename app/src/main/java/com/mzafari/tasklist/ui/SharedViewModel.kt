package com.mzafari.tasklist.ui

import androidx.lifecycle.*
import com.mzafari.tasklist.model.Page
import com.mzafari.tasklist.model.Task
import com.mzafari.tasklist.repository.TaskRepository
import kotlinx.coroutines.launch

/**
 * Shared ViewModel between MainActivity & TaskListFragment & TaskFormFragment
 * this is simple app with simple shared data between views so this is shared for whole app
 * @param taskRepository: get the repository as dependency and single source of data
 */
class SharedViewModel(private val taskRepository: TaskRepository):ViewModel() {

    //current displayed fragment
    var page:MutableLiveData<Page?> = MutableLiveData(null)


    //get tasks as livedata to make it observable on data changes
    val allTasks: LiveData<List<Task>> = taskRepository.allTasks.asLiveData()
    val doneTasks: LiveData<List<Task>> = taskRepository.doneTasks.asLiveData()
    val onProgressTasks: LiveData<List<Task>> = taskRepository.onProgressTasks.asLiveData()


    /**
     * setter method for current fragment (page)
     */
    fun setPage(page:Page){
        this.page.value = page
    }

    /**
     * launch a new coroutine and insert task
     */
    fun insertTask(task: Task) = viewModelScope.launch {
        taskRepository.insertTask(task)
    }

    /**
     * launch a new coroutine and edit task
     */
    fun updateTask(task:Task) = viewModelScope.launch {
        taskRepository.updateTask(task)
    }

    /**
     * launch a new coroutine and delete task
     */
    fun deleteTask(task:Task) = viewModelScope.launch {
        taskRepository.deleteTask(task)
    }


    /**
     * Custom factory to pass repository to main class
     */
    class SharedViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SharedViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }




}