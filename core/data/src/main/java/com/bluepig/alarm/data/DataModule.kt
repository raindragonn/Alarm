package com.bluepig.alarm.data

import com.bluepig.alarm.data.repository.AlarmRepositoryImpl
import com.bluepig.alarm.data.repository.AppConfigRepositoryImpl
import com.bluepig.alarm.data.repository.MusicInfoRepositoryImpl
import com.bluepig.alarm.data.repository.RingtoneRepositoryImpl
import com.bluepig.alarm.data.repository.TubeRepositoryImpl
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.repository.AppConfigRepository
import com.bluepig.alarm.domain.repository.MusicInfoRepository
import com.bluepig.alarm.domain.repository.RingtoneRepository
import com.bluepig.alarm.domain.repository.TubeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindsAlarmRepository(impl: AlarmRepositoryImpl): AlarmRepository

    @Binds
    abstract fun bindsMusicInfoRepository(impl: MusicInfoRepositoryImpl): MusicInfoRepository

    @Binds
    abstract fun bindsRingtoneRepository(impl: RingtoneRepositoryImpl): RingtoneRepository

    @Binds
    abstract fun bindsTubeRepository(impl: TubeRepositoryImpl): TubeRepository

    @Binds
    abstract fun bindsAppConfigRepository(impl: AppConfigRepositoryImpl): AppConfigRepository

    companion object {
        // 블로킹 작업(Jsoup·YouTube·벨소리)용. Default·Main은 쓰는 곳이 없어 한정자 없이 하나만 (기술 0015)
        @Provides
        fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}
