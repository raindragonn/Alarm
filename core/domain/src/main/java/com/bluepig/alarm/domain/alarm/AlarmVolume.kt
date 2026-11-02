package com.bluepig.alarm.domain.alarm

/** 알람 소리 볼륨 범위. 구현은 app(AudioManager) */
interface AlarmVolume {
    fun maxVolume(): Int
}
