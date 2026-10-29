package com.bluepig.alarm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AlarmData::class],
    version = 1,
    exportSchema = true,
)
internal abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        private const val DATABASE_NAME = "alarm-database"

        fun build(context: Context): AlarmDatabase =
            Room.databaseBuilder(context, AlarmDatabase::class.java, DATABASE_NAME).build()
    }
}
