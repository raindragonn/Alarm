package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.config.AdPlacement
import com.bluepig.alarm.domain.repository.AppConfigRepository
import com.bluepig.alarm.domain.util.CalendarHelper
import javax.inject.Inject

/** 전체 광고 스위치와 위치별 스위치가 모두 켜져야 보임. 전면 광고는 마지막으로 보인 뒤 1시간이 지나야 함 */
class IsAdEnabled @Inject constructor(
    private val _repository: AppConfigRepository,
) {
    suspend operator fun invoke(placement: AdPlacement): Boolean {
        val config = _repository.getConfig()
        if (!config.adsSwitch) return false

        return when (placement) {
            AdPlacement.MAIN_BOTTOM_NATIVE -> config.adsMainBottomNativeSwitch
            AdPlacement.ALARM_LIST_NATIVE -> config.adsAlarmListNativeSwitch
            AdPlacement.ALARM_BANNER -> config.adsAlarmBannerSwitch
            AdPlacement.MEDIA_SELECT_INTERSTITIAL -> config.adsInterstitialMusicClickSwitch &&
                    CalendarHelper.now.timeInMillis - _repository.getLastInterstitialShowTime() >
                    INTERSTITIAL_INTERVAL_MILLIS
        }
    }

    private companion object {
        const val INTERSTITIAL_INTERVAL_MILLIS = 60 * 60 * 1000L
    }
}
