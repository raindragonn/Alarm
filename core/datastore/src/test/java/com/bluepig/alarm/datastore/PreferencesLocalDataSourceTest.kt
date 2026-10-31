package com.bluepig.alarm.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PreferencesLocalDataSourceTest {
    @get:Rule
    val folder = TemporaryFolder()

    private fun TestScope.dataStore() = PreferenceDataStoreFactory.create(
        scope = backgroundScope,
        produceFile = { folder.root.resolve("app_pref.preferences_pb") },
    )

    @Test
    fun `저장된 값이 없으면 기본값`() = runTest {
        val dataSource = PreferencesLocalDataSourceImpl(dataStore()) { true }

        assertNull(dataSource.getSelectedAccountName())
        assertEquals(0L, dataSource.getLastInterstitialShowTime())
        assertEquals(
            RemoteConfigPreferences(false, false, false, false, false, false, false),
            dataSource.getRemoteConfig(),
        )
    }

    // SharedPreferencesMigration은 SharedPreferences 키 이름을 그대로 DataStore 키로 옮긴다
    @Test
    fun `이전 SharedPreferences 키 이름으로 저장된 값을 읽는다`() = runTest {
        val dataStore = dataStore()
        dataStore.edit {
            it[stringPreferencesKey("SELECTED_ACCOUNT_NAME_KEY")] = "user@example.com"
            it[booleanPreferencesKey("ADS_SWITCH")] = true
            it[booleanPreferencesKey("ADS_ALARM_BANNER_SWITCH")] = true
            it[booleanPreferencesKey("ADS_ALARM_LIST_NATIVE_SWITCH")] = false
            it[booleanPreferencesKey("ADS_INTERSTITIAL_MUSIC_CLICK_SWITCH")] = true
            it[booleanPreferencesKey("ADS_MAIN_BOTTOM_NATIVE_SWITCH")] = false
            it[booleanPreferencesKey("SHOW_ONLY_YOUTUBE_LINK")] = true
            it[booleanPreferencesKey("SHOW_YOUTUBE_SEARCH")] = false
            it[longPreferencesKey("LAST_INTERSTITIAL_SHOW_TIME")] = 1_793_000_000_000
        }
        val dataSource = PreferencesLocalDataSourceImpl(dataStore) { true }

        assertEquals("user@example.com", dataSource.getSelectedAccountName())
        assertEquals(1_793_000_000_000, dataSource.getLastInterstitialShowTime())
        assertEquals(
            RemoteConfigPreferences(
                adsSwitch = true,
                adsAlarmBannerSwitch = true,
                adsAlarmListNativeSwitch = false,
                adsInterstitialMusicClickSwitch = true,
                adsMainBottomNativeSwitch = false,
                showOnlyYoutubeLink = true,
                showYoutubeSearch = false,
            ),
            dataSource.getRemoteConfig(),
        )
    }

    @Test
    fun `쓴 값을 다시 읽는다`() = runTest {
        val dataSource = PreferencesLocalDataSourceImpl(dataStore()) { true }
        val config = RemoteConfigPreferences(true, false, true, false, true, false, true)

        dataSource.setSelectedAccountName("")
        dataSource.setLastInterstitialShowTime(42)
        dataSource.setRemoteConfig(config)

        assertEquals("", dataSource.getSelectedAccountName())
        assertEquals(42L, dataSource.getLastInterstitialShowTime())
        assertEquals(config, dataSource.getRemoteConfig())
    }

    @Test
    fun `잠금 해제 전에는 파일을 열지 않고 기본값`() = runTest {
        val dataSource = PreferencesLocalDataSourceImpl(dataStore()) { false }

        dataSource.setScheduledTimeZoneId("Asia/Seoul")

        assertEquals(null, dataSource.getScheduledTimeZoneId())
    }
}
