package com.bluepig.di

import com.bluepig.alarm.database.dao.AlarmDao
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.repository.AlarmRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesAlarmRepository(
        @IoDispatcher dispatcher: CoroutineDispatcher,
        alarmDao: AlarmDao
    ): AlarmRepository {
        return AlarmRepositoryImpl(
            dispatcher,
            alarmDao
        )
    }
}