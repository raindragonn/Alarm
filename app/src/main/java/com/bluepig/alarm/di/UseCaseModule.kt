package com.bluepig.alarm.di

import android.content.Context
import androidx.media3.common.util.UnstableApi
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.repository.MusicInfoRepository
import com.bluepig.alarm.domain.usecase.GetMusicMedia
import com.bluepig.alarm.usecase.GetMusicMediaImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun providesGetMusicMedia(
        @ApplicationContext context: Context,
        @IoDispatcher dispatcher: CoroutineDispatcher,
        musicInfoRepository: MusicInfoRepository
    ): GetMusicMedia {
        return GetMusicMediaImpl(
            context,
            dispatcher,
            musicInfoRepository
        )
    }
}