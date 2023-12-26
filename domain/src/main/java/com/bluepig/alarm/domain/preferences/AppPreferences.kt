package com.bluepig.alarm.domain.preferences

interface AppPreferences {
    var selectedAccountName: String?
    var adsSwitch: Boolean
    var adsAlarmBannerSwitch: Boolean
    var adsAlarmListNativeSwitch: Boolean
    var adsInterstitialMusicClickSwitch: Boolean
    var adsMainBottomNativeSwitch: Boolean
    var showOnlyYoutubeLink: Boolean
    var showYoutubeSearch: Boolean
}