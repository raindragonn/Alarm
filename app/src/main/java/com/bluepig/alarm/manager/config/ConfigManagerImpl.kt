package com.bluepig.alarm.manager.config

import com.bluepig.alarm.BuildConfig
import com.bluepig.alarm.R
import com.bluepig.alarm.domain.entity.config.AppConfig
import com.bluepig.alarm.domain.usecase.SaveAppConfig
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ConfigManagerImpl @Inject constructor(
    private val _saveAppConfig: SaveAppConfig,
    private val _appScope: CoroutineScope,
) : ConfigManager {
    private val _remoteConfig by lazy {
        Firebase.remoteConfig
            .apply {
                setConfigSettingsAsync(
                    remoteConfigSettings {
                        minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
                    }
                )
                setDefaultsAsync(R.xml.remote_config_defaults)
            }
    }

    override fun fetch() {
        _remoteConfig
            .fetchAndActivate()
            .addOnCompleteListener { _ ->
                val config = readConfig()
                _appScope.launch { _saveAppConfig(config) }
            }
    }

    private fun readConfig() = AppConfig(
        adsSwitch = _remoteConfig.getBoolean("ads_switch"),
        adsAlarmBannerSwitch = _remoteConfig.getBoolean("ads_alarm_banner_switch"),
        adsAlarmListNativeSwitch = _remoteConfig.getBoolean("ads_alarm_list_native_switch"),
        adsInterstitialMusicClickSwitch =
            _remoteConfig.getBoolean("ads_interstitial_music_click_switch"),
        adsMainBottomNativeSwitch = _remoteConfig.getBoolean("ads_main_bottom_native_switch"),
        showOnlyYoutubeLink = _remoteConfig.getBoolean("show_only_youtube_link"),
        showYoutubeSearch = _remoteConfig.getBoolean("show_youtube_search"),
    )
}
