package com.mzafari.tasklist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mzafari.tasklist.model.Task

/**
 * Main database class for access to Room database and expose the DAO
 */
@Database(entities = arrayOf(Task::class),version = 1,exportSchema = false)
abstract class MainDatabase:RoomDatabase() {

    //expose DAOs through an abstract "getter" method for each @Dao
    abstract fun mainDao():MainDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    "commzafaritasks"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}