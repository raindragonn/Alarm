package com.bluepig.alarm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bluepig.alarm.database.converter.FileTypeConverter
import com.bluepig.alarm.database.converter.WeakTypeConverter
import com.bluepig.alarm.database.dao.AlarmDao
import com.bluepig.alarm.database.data.AlarmData

@Database(
    entities = [AlarmData::class],
    version = DatabaseConfig.VERSION,
    exportSchema = false,
)
@TypeConverters(
    WeakTypeConverter::class, FileTypeConverter::class
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