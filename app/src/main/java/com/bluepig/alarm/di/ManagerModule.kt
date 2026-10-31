package com.bluepig.alarm.di

import android.content.Context
import android.media.AudioManager
import androidx.media3.common.util.UnstableApi
import com.bluepig.alarm.domain.alarm.AlarmVolume
import com.bluepig.alarm.domain.alarm.BpAlarmManager
import com.bluepig.alarm.domain.usecase.SaveAppConfig
import com.bluepig.alarm.manager.alarm.BPAlarmManagerImpl
import com.bluepig.alarm.manager.config.ConfigManager
import com.bluepig.alarm.manager.config.ConfigManagerImpl
import com.bluepig.alarm.manager.download.MediaDownloadManager
import com.bluepig.alarm.manager.download.MediaDownloadManagerImpl
import com.bluepig.alarm.manager.player.MusicPlayerManager
import com.bluepig.alarm.manager.player.MusicPlayerManagerImpl
import com.bluepig.alarm.manager.player.TtsPlayerManager
import com.bluepig.alarm.manager.player.TtsPlayerManagerImpl
import com.bluepig.alarm.util.ext.audioManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    fun providesMusicPlayerManager(
        @ApplicationContext context: Context,
        mediaDownloadManager: MediaDownloadManager,
    ): MusicPlayerManager {
        return MusicPlayerManagerImpl(context, mediaDownloadManager)
    }

    @Provides
    @Singleton
    fun providesMediaDownloadManager(
        @ApplicationContext context: Context
    ): MediaDownloadManager {
        return MediaDownloadManagerImpl(context)
    }

    @Provides
    @Singleton
    fun providesAudioManager(
        @ApplicationContext context: Context
    ): AudioManager {
        return context.audioManager
    }

    // 알람음은 STREAM_MUSIC으로 재생함
    @Provides
    fun providesAlarmVolume(audioManager: AudioManager): AlarmVolume = object : AlarmVolume {
        override fun maxVolume() = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    @Provides
    @Singleton
    fun providesBPAlarmManager(
        @ApplicationContext context: Context
    ): BpAlarmManager {
        return BPAlarmManagerImpl(context)
    }

    @Provides
    fun providesTtsPlayerManager(
        @ApplicationContext
        context: Context
    ): TtsPlayerManager {
        return TtsPlayerManagerImpl(context)
    }

    @Provides
    fun providesConfigManage(
        saveAppConfig: SaveAppConfig,
        appScope: CoroutineScope,
    ): ConfigManager {
        return ConfigManagerImpl(saveAppConfig, appScope)
    }

    // 화면보다 오래 살아야 하는 작업용 (원격 설정 저장)
    @Provides
    @Singleton
    fun providesAppScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
}
