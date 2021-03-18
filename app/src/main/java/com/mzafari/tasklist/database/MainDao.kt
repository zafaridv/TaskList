package com.mzafari.tasklist.database

import androidx.room.*
import com.mzafari.tasklist.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Main Data access object for all database queries
 */
@Dao
interface MainDao {

    @Insert
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task:Task)

    @Delete
    suspend fun deleteTask(task:Task)

    @Query("SELECT * FROM ${Table.task}")
    fun getAllTasks():Flow<List<Task>>

    @Query("SELECT * FROM ${Table.task} WHERE status = :status")
    fun getTaskByStatus(status:String):Flow<List<Task>>

    @Query("SELECT * FROM ${Table.task} WHERE id = :taskId")
    suspend fun getTaskById(taskId:Int):Task?


}