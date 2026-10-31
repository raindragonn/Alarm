package com.bluepig.alarm.data.fake

import com.bluepig.alarm.datastore.PreferencesLocalDataSource
import com.bluepig.alarm.datastore.RemoteConfigPreferences

class FakePreferencesLocalDataSource(
    var accountName: String? = null,
    var remoteConfig: RemoteConfigPreferences =
        RemoteConfigPreferences(false, false, false, false, false, false, false),
    var lastInterstitialShowTime: Long = 0,
    var scheduledTimeZoneId: String? = null,
) : PreferencesLocalDataSource {
    override suspend fun getSelectedAccountName() = accountName
    override suspend fun setSelectedAccountName(name: String) {
        accountName = name
    }

    override suspend fun getRemoteConfig() = remoteConfig
    override suspend fun setRemoteConfig(config: RemoteConfigPreferences) {
        remoteConfig = config
    }

    override suspend fun getLastInterstitialShowTime() = lastInterstitialShowTime
    override suspend fun setLastInterstitialShowTime(timeInMillis: Long) {
        lastInterstitialShowTime = timeInMillis
    }

    override suspend fun getScheduledTimeZoneId() = scheduledTimeZoneId
    override suspend fun setScheduledTimeZoneId(id: String) {
        scheduledTimeZoneId = id
    }
}
