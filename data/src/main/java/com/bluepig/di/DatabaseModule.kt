package com.bluepig.di

import android.content.Context
import com.bluepig.alarm.database.AlarmDatabase
import com.bluepig.alarm.database.dao.AlarmDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(
        @ApplicationContext context: Context
    ): AlarmDatabase {
        return AlarmDatabase.buildDataBase(context)
    }

    @Provides
    @Singleton
    fun providesAlarmDao(
        database: AlarmDatabase
    ): AlarmDao {
        return database.alarmDao()
    }
}