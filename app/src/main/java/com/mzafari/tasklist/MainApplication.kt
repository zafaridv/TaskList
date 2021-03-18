package com.mzafari.tasklist

import android.app.Application
import com.mzafari.tasklist.database.MainDatabase
import com.mzafari.tasklist.repository.TaskRepository

class MainApplication: Application() {

    // get instance of database and repository when needed by lazy
    val database by lazy { MainDatabase.getDatabase(this) }
    val repository by lazy { TaskRepository(database.mainDao()) }
}