package com.bluepig.alarm.network

import android.content.Context
import android.content.pm.PackageManager
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.YouTubeRequestInitializer
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
import java.security.MessageDigest
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
        fun providesYouTube(
            @ApplicationContext context: Context,
        ): YouTube = YouTube.Builder(
            NetHttpTransport(), GsonFactory.getDefaultInstance(), androidAppHeaders(context)
        ).setApplicationName(context.getString(R.string.app_name))
            .setYouTubeRequestInitializer(YouTubeRequestInitializer(BuildConfig.YOUTUBE_API_KEY))
            .build()

        /** API 키의 Android 앱 제한은 이 두 헤더로 확인함 (기술 0030) */
        private fun androidAppHeaders(context: Context): HttpRequestInitializer {
            val cert by lazy { signingCertSha1(context) }
            return HttpRequestInitializer { request ->
                request.headers.set("X-Android-Package", context.packageName)
                request.headers.set("X-Android-Cert", cert)
            }
        }

        private fun signingCertSha1(context: Context): String {
            val info = context.packageManager.getPackageInfo(
                context.packageName, PackageManager.GET_SIGNING_CERTIFICATES
            )
            val cert = info.signingInfo?.apkContentsSigners?.firstOrNull() ?: return ""
            return MessageDigest.getInstance("SHA-1").digest(cert.toByteArray())
                .joinToString("") { "%02X".format(it) }
        }
    }
}
