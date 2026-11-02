package com.bluepig.alarm.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** 지금 울리는 알람 ID (미리보기는 [PREVIEW_ID]). 서비스가 멈추면 null → 울림 화면이 닫힘 */
object AlarmRinging {
    const val PREVIEW_ID = -2L

    private val _alarmId = MutableStateFlow<Long?>(null)
    val alarmId: StateFlow<Long?> = _alarmId.asStateFlow()

    private val _speakingMemo = MutableStateFlow(false)

    /** 메모를 읽는 중. 울림 화면은 다 읽은 뒤 영상을 재생 */
    val speakingMemo: StateFlow<Boolean> = _speakingMemo.asStateFlow()

    internal fun set(id: Long?) {
        _alarmId.value = id
    }

    internal fun setSpeakingMemo(speaking: Boolean) {
        _speakingMemo.value = speaking
    }
}
