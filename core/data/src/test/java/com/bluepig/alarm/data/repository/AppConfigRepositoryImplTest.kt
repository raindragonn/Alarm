package com.bluepig.alarm.data.repository

import com.bluepig.alarm.data.fake.FakePreferencesLocalDataSource
import com.bluepig.alarm.datastore.RemoteConfigPreferences
import com.bluepig.alarm.domain.entity.config.AppConfig
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AppConfigRepositoryImplTest {
    // 필드마다 값을 다르게 둬서 서로 바뀐 매핑을 잡는다
    private val config = AppConfig(
        adsSwitch = true,
        adsAlarmBannerSwitch = false,
        adsAlarmListNativeSwitch = true,
        adsInterstitialMusicClickSwitch = false,
        adsMainBottomNativeSwitch = true,
        showOnlyYoutubeLink = false,
        showYoutubeSearch = true,
    )
    private val preferences = RemoteConfigPreferences(
        adsSwitch = true,
        adsAlarmBannerSwitch = false,
        adsAlarmListNativeSwitch = true,
        adsInterstitialMusicClickSwitch = false,
        adsMainBottomNativeSwitch = true,
        showOnlyYoutubeLink = false,
        showYoutubeSearch = true,
    )

    @Test
    fun `설정을 저장 형식으로 바꿔 쓰고 읽는다`() = runTest {
        val local = FakePreferencesLocalDataSource()
        val repository = AppConfigRepositoryImpl(local)

        repository.saveConfig(config)

        assertEquals(preferences, local.remoteConfig)
        assertEquals(config, repository.getConfig())
    }

    @Test
    fun `전면 광고를 마지막으로 보인 시각`() = runTest {
        val repository = AppConfigRepositoryImpl(FakePreferencesLocalDataSource())

        repository.setLastInterstitialShowTime(42)

        assertEquals(42L, repository.getLastInterstitialShowTime())
    }
}
