package com.bluepig.alarm.domain.fake

import com.bluepig.alarm.domain.entity.config.AppConfig
import com.bluepig.alarm.domain.repository.AppConfigRepository

val allOnConfig = AppConfig(
    adsSwitch = true,
    adsAlarmBannerSwitch = true,
    adsAlarmListNativeSwitch = true,
    adsInterstitialMusicClickSwitch = true,
    adsMainBottomNativeSwitch = true,
    showOnlyYoutubeLink = true,
    showYoutubeSearch = true,
)

class FakeAppConfigRepository(
    var config: AppConfig = allOnConfig,
    var lastInterstitialShowTime: Long = 0,
) : AppConfigRepository {
    override suspend fun getConfig() = config
    override suspend fun saveConfig(config: AppConfig) {
        this.config = config
    }

    override suspend fun getLastInterstitialShowTime() = lastInterstitialShowTime
    override suspend fun setLastInterstitialShowTime(timeInMillis: Long) {
        lastInterstitialShowTime = timeInMillis
    }
}
