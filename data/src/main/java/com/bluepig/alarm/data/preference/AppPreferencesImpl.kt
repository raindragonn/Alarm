package com.bluepig.alarm.data.preference

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import com.bluepig.alarm.domain.preferences.AppPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
    @ApplicationContext private val _context: Context
) : AppPreferences {
    private val _pref by lazy { _context.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE) }

    override var selectedAccountName: String?
        get() = _pref.getString(SELECTED_ACCOUNT_NAME_KEY, null)
        set(value) {
            _pref.edit {
                putString(SELECTED_ACCOUNT_NAME_KEY, value)
            }
        }

    override var adsSwitch: Boolean
        get() = _pref.getBoolean(ADS_SWITCH, false)
        set(value) {
            _pref.edit { putBoolean(ADS_SWITCH, value) }
        }

    override var adsAlarmBannerSwitch: Boolean
        get() = _pref.getBoolean(ADS_ALARM_BANNER_SWITCH, false)
        set(value) {
            _pref.edit { putBoolean(ADS_ALARM_BANNER_SWITCH, value) }
        }

    override var adsAlarmListNativeSwitch: Boolean
        get() = _pref.getBoolean(ADS_ALARM_LIST_NATIVE_SWITCH, false)
        set(value) {
            _pref.edit { putBoolean(ADS_ALARM_LIST_NATIVE_SWITCH, value) }
        }

    override var adsInterstitialMusicClickSwitch: Boolean
        get() = _pref.getBoolean(ADS_INTERSTITIAL_MUSIC_CLICK_SWITCH, false)
        set(value) {
            _pref.edit { putBoolean(ADS_INTERSTITIAL_MUSIC_CLICK_SWITCH, value) }
        }
    override var lastInterstitialShowTime: Long
        get() = _pref.getLong(LAST_INTERSTITIAL_SHOW_TIME, 0)
        set(value) {
            _pref.edit { putLong(LAST_INTERSTITIAL_SHOW_TIME, value) }
        }

    override var adsMainBottomNativeSwitch: Boolean
        get() = _pref.getBoolean(ADS_MAIN_BOTTOM_NATIVE_SWITCH, false)
        set(value) {
            _pref.edit { putBoolean(ADS_MAIN_BOTTOM_NATIVE_SWITCH, value) }
        }

    override var showOnlyYoutubeLink: Boolean
        get() = _pref.getBoolean(SHOW_ONLY_YOUTUBE_LINK, false)
        set(value) {
            _pref.edit { putBoolean(SHOW_ONLY_YOUTUBE_LINK, value) }
        }

    override var showYoutubeSearch: Boolean
        get() = _pref.getBoolean(SHOW_YOUTUBE_SEARCH, false)
        set(value) {
            _pref.edit { putBoolean(SHOW_YOUTUBE_SEARCH, value) }
        }


    companion object {
        private const val PREF_NAME = "APP_PREF"
        private const val SELECTED_ACCOUNT_NAME_KEY = "SELECTED_ACCOUNT_NAME_KEY"

        private const val ADS_SWITCH = "ADS_SWITCH"
        private const val ADS_ALARM_BANNER_SWITCH = "ADS_ALARM_BANNER_SWITCH"
        private const val ADS_ALARM_LIST_NATIVE_SWITCH = "ADS_ALARM_LIST_NATIVE_SWITCH"
        private const val ADS_INTERSTITIAL_MUSIC_CLICK_SWITCH =
            "ADS_INTERSTITIAL_MUSIC_CLICK_SWITCH"
        private const val ADS_MAIN_BOTTOM_NATIVE_SWITCH = "ADS_MAIN_BOTTOM_NATIVE_SWITCH"

        private const val LAST_INTERSTITIAL_SHOW_TIME = "LAST_INTERSTITIAL_SHOW_TIME"

        private const val SHOW_ONLY_YOUTUBE_LINK = "SHOW_ONLY_YOUTUBE_LINK"
        private const val SHOW_YOUTUBE_SEARCH = "SHOW_YOUTUBE_SEARCH"
    }
}