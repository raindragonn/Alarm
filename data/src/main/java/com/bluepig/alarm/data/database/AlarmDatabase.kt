package com.bluepig.alarm.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bluepig.alarm.data.database.converter.AlarmMediaTypeConverter
import com.bluepig.alarm.data.database.converter.WeekTypeConverter
import com.bluepig.alarm.data.database.dao.AlarmDao
import com.bluepig.alarm.data.database.data.AlarmData

@Database(
    entities = [AlarmData::class],
    version = DatabaseConfig.VERSION,
    exportSchema = false,
)
@TypeConverters(
    WeekTypeConverter::class, AlarmMediaTypeConverter::class
)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        fun buildDataBase(
            context: Context
        ): AlarmDatabase =
            Room.databaseBuilder(
                context,
                AlarmDatabase::class.java,
                DatabaseConfig.DATABASE_NAME
            ).build()
    }
}