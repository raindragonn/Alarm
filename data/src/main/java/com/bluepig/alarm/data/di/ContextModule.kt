package com.bluepig.alarm.data.di

import android.content.Context
import com.bluepig.alarm.data.preference.AppPreferences
import com.bluepig.alarm.data.preference.AppPreferencesImpl
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTubeScopes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContextModule {

    @Provides
    @Singleton
    fun providesAppPreferences(
        @ApplicationContext context: Context
    ): AppPreferences {
        return AppPreferencesImpl(context)
    }

    @Provides
    @Singleton
    fun providesCredential(
        @ApplicationContext
        context: Context
    ): GoogleAccountCredential {
        return GoogleAccountCredential.usingOAuth2(
            context,
            listOf(YouTubeScopes.YOUTUBE_READONLY)
        ).setBackOff(ExponentialBackOff())
    }
}
