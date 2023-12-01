package com.bluepig.alarm.di

import android.content.Context
import android.media.AudioManager
import androidx.media3.common.util.UnstableApi
import com.bluepig.alarm.domain.alarm.BpAlarmManager
import com.bluepig.alarm.manager.alarm.BPAlarmManagerImpl
import com.bluepig.alarm.manager.download.MediaDownloadManager
import com.bluepig.alarm.manager.download.MediaDownloadManagerImpl
import com.bluepig.alarm.manager.player.SongPlayerManager
import com.bluepig.alarm.manager.player.SongPlayerManagerImpl
import com.bluepig.alarm.util.ext.audioManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    @Singleton
    fun providesSongPlayerManager(
        @ApplicationContext context: Context,
        mediaDownloadManager: MediaDownloadManager,
    ): SongPlayerManager {
        return SongPlayerManagerImpl(context, mediaDownloadManager)
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

    @Provides
    @Singleton
    fun providesBPAlarmManager(
        @ApplicationContext context: Context
    ): BpAlarmManager {
        return BPAlarmManagerImpl(context)
    }
}