package com.mzafari.tasklist.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mzafari.tasklist.database.Table
import kotlinx.android.parcel.Parcelize

@Entity(tableName= Table.task)
@Parcelize
data class Task(
    @PrimaryKey(autoGenerate = true) var id:Int?=null,
    @ColumnInfo(name="name") var name:String,
    @ColumnInfo(name="deadline") var deadline: String,
    @ColumnInfo(name="status") var status: String
    ):Parcelable {
}