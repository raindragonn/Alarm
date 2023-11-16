package com.bluepig.alarm.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.bluepig.alarm.player.SongPlayerManager
import com.bluepig.alarm.player.SongPlayerManagerImpl
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
        @ApplicationContext context: Context
    ): SongPlayerManager {
        return SongPlayerManagerImpl(context)
    }
}