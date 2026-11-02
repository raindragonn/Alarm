package com.bluepig.alarm.database

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(
        @ApplicationContext context: Context
    ): AlarmDatabase = AlarmDatabase.build(context)

    @Provides
    fun providesAlarmLocalDataSource(
        database: AlarmDatabase
    ): AlarmLocalDataSource = AlarmLocalDataSourceImpl(database.alarmDao())
}
