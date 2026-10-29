package com.bluepig.alarm.data.repository

import com.bluepig.alarm.datastore.PreferencesLocalDataSource
import com.bluepig.alarm.datastore.RemoteConfigPreferences
import com.bluepig.alarm.domain.entity.config.AppConfig
import com.bluepig.alarm.domain.repository.AppConfigRepository
import javax.inject.Inject

internal class AppConfigRepositoryImpl @Inject constructor(
    private val _preferences: PreferencesLocalDataSource,
) : AppConfigRepository {

    override suspend fun getConfig(): AppConfig = _preferences.getRemoteConfig().let {
        AppConfig(
            adsSwitch = it.adsSwitch,
            adsAlarmBannerSwitch = it.adsAlarmBannerSwitch,
            adsAlarmListNativeSwitch = it.adsAlarmListNativeSwitch,
            adsInterstitialMusicClickSwitch = it.adsInterstitialMusicClickSwitch,
            adsMainBottomNativeSwitch = it.adsMainBottomNativeSwitch,
            showOnlyYoutubeLink = it.showOnlyYoutubeLink,
            showYoutubeSearch = it.showYoutubeSearch,
        )
    }

    override suspend fun saveConfig(config: AppConfig) = _preferences.setRemoteConfig(
        RemoteConfigPreferences(
            adsSwitch = config.adsSwitch,
            adsAlarmBannerSwitch = config.adsAlarmBannerSwitch,
            adsAlarmListNativeSwitch = config.adsAlarmListNativeSwitch,
            adsInterstitialMusicClickSwitch = config.adsInterstitialMusicClickSwitch,
            adsMainBottomNativeSwitch = config.adsMainBottomNativeSwitch,
            showOnlyYoutubeLink = config.showOnlyYoutubeLink,
            showYoutubeSearch = config.showYoutubeSearch,
        )
    )

    override suspend fun getLastInterstitialShowTime(): Long =
        _preferences.getLastInterstitialShowTime()

    override suspend fun setLastInterstitialShowTime(timeInMillis: Long) =
        _preferences.setLastInterstitialShowTime(timeInMillis)
}
