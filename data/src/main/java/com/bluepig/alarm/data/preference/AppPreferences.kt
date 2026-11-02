package com.bluepig.alarm.data.preference

interface AppPreferences {
    var selectedAccountName: String?

    var adsSwitch: Boolean
    var adsAlarmBannerSwitch: Boolean
    var adsAlarmListNativeSwitch: Boolean
    var adsInterstitialMusicClickSwitch: Boolean
    var lastInterstitialShowTime: Long
    var adsMainBottomNativeSwitch: Boolean

    var showOnlyYoutubeLink: Boolean
    var showYoutubeSearch: Boolean
}