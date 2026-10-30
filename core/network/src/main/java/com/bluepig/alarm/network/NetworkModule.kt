package com.bluepig.alarm.network

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeScopes
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkModule {

    @Binds
    abstract fun bindsMusicRemoteDataSource(impl: MusicRemoteDataSourceImpl): MusicRemoteDataSource

    @Binds
    abstract fun bindsTubeRemoteDataSource(impl: TubeRemoteDataSourceImpl): TubeRemoteDataSource

    companion object {
        @Provides
        @Singleton
        fun providesOkHttpClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
            return if (BuildConfig.DEBUG) {
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()
            } else {
                OkHttpClient.Builder()
                    .build()
            }
        }

        @Provides
        @Singleton
        fun providesRetrofit(
            client: OkHttpClient,
        ): Retrofit {
            val contentType = "application/json".toMediaType()
            val json = Json { ignoreUnknownKeys = true }
            val converterFactory = json.asConverterFactory(contentType)
            return Retrofit.Builder()
                .baseUrl(BuildConfig.SEARCH_API_HOST)
                .client(client)
                .addConverterFactory(converterFactory)
                .build()
        }

        @Provides
        @Singleton
        fun providesSearchApi(
            retrofit: Retrofit,
        ): SearchApi {
            return retrofit.create(SearchApi::class.java)
        }

        @Provides
        fun providesMusicInfoPageParser(): MusicInfoPageParser = MusicInfoPageParser()

        @Provides
        @Singleton
        fun providesCredential(
            @ApplicationContext context: Context
        ): GoogleAccountCredential {
            return GoogleAccountCredential.usingOAuth2(
                context,
                listOf(YouTubeScopes.YOUTUBE_READONLY)
            ).setBackOff(ExponentialBackOff())
        }

        @Provides
        @Singleton
        fun providesYouTube(
            @ApplicationContext context: Context,
            credential: GoogleAccountCredential,
        ): YouTube = YouTube.Builder(
            NetHttpTransport(), GsonFactory.getDefaultInstance(), credential
        ).setApplicationName(context.getString(R.string.app_name))
            .build()
    }
}
