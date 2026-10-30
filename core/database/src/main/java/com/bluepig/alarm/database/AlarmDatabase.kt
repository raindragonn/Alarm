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

        // 재부팅 후 첫 잠금 해제 전에도 읽히게 device-protected storage에 둠 (기술 0017).
        // 이전 버전이 credential storage에 만든 파일은 처음 열기 전에 옮김 (없으면 아무것도 안 함)
        fun build(context: Context): AlarmDatabase {
            val storage = context.createDeviceProtectedStorageContext()
            storage.moveDatabaseFrom(context, DATABASE_NAME)
            return Room.databaseBuilder(storage, AlarmDatabase::class.java, DATABASE_NAME).build()
        }
    }
}
