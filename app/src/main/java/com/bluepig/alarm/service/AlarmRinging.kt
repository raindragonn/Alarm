package com.bluepig.alarm.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** 지금 울리는 알람 ID (미리보기는 [PREVIEW_ID]). 서비스가 멈추면 null → 울림 화면이 닫힘 */
object AlarmRinging {
    const val PREVIEW_ID = -2L

    private val _alarmId = MutableStateFlow<Long?>(null)
    val alarmId: StateFlow<Long?> = _alarmId.asStateFlow()

    internal fun set(id: Long?) {
        _alarmId.value = id
    }
}
