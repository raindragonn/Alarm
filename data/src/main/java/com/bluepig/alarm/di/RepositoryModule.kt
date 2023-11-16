package com.bluepig.alarm.di

import com.bluepig.alarm.database.dao.AlarmDao
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.repository.FileRepository
import com.bluepig.alarm.network.api.SearchApi
import com.bluepig.alarm.network.parser.FilePageParser
import com.bluepig.alarm.repository.AlarmRepositoryImpl
import com.bluepig.alarm.repository.FileRepositoryImpl
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

    @Provides
    @Singleton
    fun providesFileRepository(
        @IoDispatcher dispatcher: CoroutineDispatcher,
        searchApi: SearchApi,
        pageParser: FilePageParser,
    ): FileRepository {
        return FileRepositoryImpl(
            dispatcher,
            searchApi,
            pageParser
        )
    }
}