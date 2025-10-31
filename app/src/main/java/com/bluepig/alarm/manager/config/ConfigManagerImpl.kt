package com.bluepig.alarm.manager.config

import com.bluepig.alarm.BuildConfig
import com.bluepig.alarm.R
import com.bluepig.alarm.data.preference.AppPreferences
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import javax.inject.Inject

class ConfigManagerImpl @Inject constructor(
    private val _appPreferences: AppPreferences,
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
                updatePref()
            }
    }

    private fun updatePref() {
        _remoteConfig.getBoolean("ads_switch")
            .let {
                _appPreferences.adsSwitch = it
            }
        _remoteConfig.getBoolean("ads_alarm_banner_switch")
            .let {
                _appPreferences.adsAlarmBannerSwitch = it
            }
        _remoteConfig.getBoolean("ads_alarm_list_native_switch")
            .let {
                _appPreferences.adsAlarmListNativeSwitch = it
            }
        _remoteConfig.getBoolean("ads_interstitial_music_click_switch")
            .let {
                _appPreferences.adsInterstitialMusicClickSwitch = it
            }
        _remoteConfig.getBoolean("ads_main_bottom_native_switch")
            .let {
                _appPreferences.adsMainBottomNativeSwitch = it
            }
        _remoteConfig.getBoolean("show_only_youtube_link")
            .let {
                _appPreferences.showOnlyYoutubeLink = it
            }
        _remoteConfig.getBoolean("show_youtube_search")
            .let {
                _appPreferences.showYoutubeSearch = it
            }
    }
}