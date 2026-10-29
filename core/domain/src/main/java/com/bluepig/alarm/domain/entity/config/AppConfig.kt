package com.bluepig.alarm.domain.entity.config

/** Firebase Remote Config에서 받은 값 (광고 위치별 스위치, YouTube 검색·링크 노출) */
data class AppConfig(
    val adsSwitch: Boolean,
    val adsAlarmBannerSwitch: Boolean,
    val adsAlarmListNativeSwitch: Boolean,
    val adsInterstitialMusicClickSwitch: Boolean,
    val adsMainBottomNativeSwitch: Boolean,
    val showOnlyYoutubeLink: Boolean,
    val showYoutubeSearch: Boolean,
)
