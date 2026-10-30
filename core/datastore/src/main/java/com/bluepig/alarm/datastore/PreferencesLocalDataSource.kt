package com.bluepig.alarm.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject

/** 원격 설정에서 받아 저장해 둔 값 */
data class RemoteConfigPreferences(
    val adsSwitch: Boolean,
    val adsAlarmBannerSwitch: Boolean,
    val adsAlarmListNativeSwitch: Boolean,
    val adsInterstitialMusicClickSwitch: Boolean,
    val adsMainBottomNativeSwitch: Boolean,
    val showOnlyYoutubeLink: Boolean,
    val showYoutubeSearch: Boolean,
)

interface PreferencesLocalDataSource {
    suspend fun getSelectedAccountName(): String?
    suspend fun setSelectedAccountName(name: String)
    suspend fun getRemoteConfig(): RemoteConfigPreferences
    suspend fun setRemoteConfig(config: RemoteConfigPreferences)
    suspend fun getLastInterstitialShowTime(): Long
    suspend fun setLastInterstitialShowTime(timeInMillis: Long)
}

internal class PreferencesLocalDataSourceImpl @Inject constructor(
    private val _dataStore: DataStore<Preferences>,
) : PreferencesLocalDataSource {

    override suspend fun getSelectedAccountName(): String? = read()[SELECTED_ACCOUNT_NAME_KEY]

    override suspend fun setSelectedAccountName(name: String) = write {
        it[SELECTED_ACCOUNT_NAME_KEY] = name
    }

    override suspend fun getRemoteConfig(): RemoteConfigPreferences = read().let {
        RemoteConfigPreferences(
            adsSwitch = it[ADS_SWITCH] ?: false,
            adsAlarmBannerSwitch = it[ADS_ALARM_BANNER_SWITCH] ?: false,
            adsAlarmListNativeSwitch = it[ADS_ALARM_LIST_NATIVE_SWITCH] ?: false,
            adsInterstitialMusicClickSwitch = it[ADS_INTERSTITIAL_MUSIC_CLICK_SWITCH] ?: false,
            adsMainBottomNativeSwitch = it[ADS_MAIN_BOTTOM_NATIVE_SWITCH] ?: false,
            showOnlyYoutubeLink = it[SHOW_ONLY_YOUTUBE_LINK] ?: false,
            showYoutubeSearch = it[SHOW_YOUTUBE_SEARCH] ?: false,
        )
    }

    override suspend fun setRemoteConfig(config: RemoteConfigPreferences) = write {
        it[ADS_SWITCH] = config.adsSwitch
        it[ADS_ALARM_BANNER_SWITCH] = config.adsAlarmBannerSwitch
        it[ADS_ALARM_LIST_NATIVE_SWITCH] = config.adsAlarmListNativeSwitch
        it[ADS_INTERSTITIAL_MUSIC_CLICK_SWITCH] = config.adsInterstitialMusicClickSwitch
        it[ADS_MAIN_BOTTOM_NATIVE_SWITCH] = config.adsMainBottomNativeSwitch
        it[SHOW_ONLY_YOUTUBE_LINK] = config.showOnlyYoutubeLink
        it[SHOW_YOUTUBE_SEARCH] = config.showYoutubeSearch
    }

    override suspend fun getLastInterstitialShowTime(): Long =
        read()[LAST_INTERSTITIAL_SHOW_TIME] ?: 0

    override suspend fun setLastInterstitialShowTime(timeInMillis: Long) = write {
        it[LAST_INTERSTITIAL_SHOW_TIME] = timeInMillis
    }

    // 파일을 못 읽으면 기본값 (SharedPreferences와 같게)
    private suspend fun read(): Preferences = _dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .first()

    // 쓰기 실패는 무시 (SharedPreferences.apply와 같게)
    private suspend fun write(transform: (MutablePreferences) -> Unit) {
        try {
            _dataStore.edit { transform(it) }
        } catch (_: IOException) {
        }
    }

    // 키 이름은 SharedPreferences(APP_PREF)와 같아야 SharedPreferencesMigration으로 옮긴 값을 읽음
    private companion object {
        val SELECTED_ACCOUNT_NAME_KEY = stringPreferencesKey("SELECTED_ACCOUNT_NAME_KEY")
        val ADS_SWITCH = booleanPreferencesKey("ADS_SWITCH")
        val ADS_ALARM_BANNER_SWITCH = booleanPreferencesKey("ADS_ALARM_BANNER_SWITCH")
        val ADS_ALARM_LIST_NATIVE_SWITCH = booleanPreferencesKey("ADS_ALARM_LIST_NATIVE_SWITCH")
        val ADS_INTERSTITIAL_MUSIC_CLICK_SWITCH =
            booleanPreferencesKey("ADS_INTERSTITIAL_MUSIC_CLICK_SWITCH")
        val ADS_MAIN_BOTTOM_NATIVE_SWITCH = booleanPreferencesKey("ADS_MAIN_BOTTOM_NATIVE_SWITCH")
        val LAST_INTERSTITIAL_SHOW_TIME = longPreferencesKey("LAST_INTERSTITIAL_SHOW_TIME")
        val SHOW_ONLY_YOUTUBE_LINK = booleanPreferencesKey("SHOW_ONLY_YOUTUBE_LINK")
        val SHOW_YOUTUBE_SEARCH = booleanPreferencesKey("SHOW_YOUTUBE_SEARCH")
    }
}
