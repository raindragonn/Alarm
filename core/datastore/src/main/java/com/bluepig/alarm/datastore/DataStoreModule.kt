package com.bluepig.alarm.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataStoreModule {

    @Binds
    abstract fun bindsPreferencesLocalDataSource(
        impl: PreferencesLocalDataSourceImpl
    ): PreferencesLocalDataSource

    companion object {
        // 이전 버전의 SharedPreferences 값을 처음 읽을 때 옮기고 그 파일은 지움
        @Provides
        @Singleton
        fun providesDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
            migrations = listOf(SharedPreferencesMigration(context, "APP_PREF")),
            produceFile = { context.preferencesDataStoreFile("app_pref") },
        )
    }
}
