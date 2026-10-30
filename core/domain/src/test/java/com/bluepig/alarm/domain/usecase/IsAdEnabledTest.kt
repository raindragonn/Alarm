package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.config.AdPlacement
import com.bluepig.alarm.domain.fake.FakeAppConfigRepository
import com.bluepig.alarm.domain.fake.allOnConfig
import com.bluepig.alarm.domain.util.FixedClockRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class IsAdEnabledTest {
    @get:Rule
    val clock = FixedClockRule()

    @Test
    fun `전체 스위치가 꺼지면 모든 위치가 꺼진다`() = runTest {
        val isAdEnabled = IsAdEnabled(FakeAppConfigRepository(allOnConfig.copy(adsSwitch = false)))

        AdPlacement.entries.forEach { assertFalse(isAdEnabled(it)) }
    }

    @Test
    fun `위치별 스위치를 따른다`() = runTest {
        val config = allOnConfig.copy(
            adsMainBottomNativeSwitch = false,
            adsAlarmListNativeSwitch = true,
            adsAlarmBannerSwitch = false,
        )
        val isAdEnabled = IsAdEnabled(FakeAppConfigRepository(config))

        assertFalse(isAdEnabled(AdPlacement.MAIN_BOTTOM_NATIVE))
        assertTrue(isAdEnabled(AdPlacement.ALARM_LIST_NATIVE))
        assertFalse(isAdEnabled(AdPlacement.ALARM_BANNER))
    }

    @Test
    fun `전면 광고는 마지막으로 보인 뒤 1시간이 지나야 한다`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAppConfigRepository()
        val isAdEnabled = IsAdEnabled(repository)

        repository.lastInterstitialShowTime = clock.millis("2026-10-28T08:00")
        assertFalse(isAdEnabled(AdPlacement.MEDIA_SELECT_INTERSTITIAL))

        repository.lastInterstitialShowTime = clock.millis("2026-10-28T07:59")
        assertTrue(isAdEnabled(AdPlacement.MEDIA_SELECT_INTERSTITIAL))
    }

    @Test
    fun `전면 광고를 보인 시각을 지금으로 저장한다`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAppConfigRepository()

        MarkInterstitialShown(repository)()

        assertEquals(clock.millis("2026-10-28T09:00"), repository.lastInterstitialShowTime)
    }
}
