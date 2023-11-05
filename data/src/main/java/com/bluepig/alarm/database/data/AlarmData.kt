package com.bluepig.alarm.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm")
data class AlarmData(
    @PrimaryKey val id: Int? = null,
    @ColumnInfo(name = "timeInMillis") val date: Long,
    @ColumnInfo(name = "isActive") val isActive: Boolean,
    @ColumnInfo(name = "songName") val songName: String,
)
