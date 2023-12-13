package com.bluepig.alarm.data.di

import android.content.Context
import com.bluepig.alarm.data.database.dao.AlarmDao
import com.bluepig.alarm.data.network.api.SearchApi
import com.bluepig.alarm.data.network.parser.FilePageParser
import com.bluepig.alarm.data.repository.AlarmRepositoryImpl
import com.bluepig.alarm.data.repository.FileRepositoryImpl
import com.bluepig.alarm.data.repository.RingtoneRepositoryImpl
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.repository.FileRepository
import com.bluepig.alarm.domain.repository.RingtoneRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun providesRingtoneRepository(
        @ApplicationContext context: Context,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): RingtoneRepository {
        return RingtoneRepositoryImpl(
            context,
            dispatcher
        )
    }
}