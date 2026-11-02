package com.bluepig.alarm.domain.alarm

import com.bluepig.alarm.domain.entity.alarm.Alarm

interface BpAlarmManager {
    fun setupAlarm(alarm: Alarm)
    fun cancelAlarm(alarm: Alarm)

    /** API 31·32에서 정확한 알람 권한을 거부했으면 false (33+는 USE_EXACT_ALARM, 기술 0025) */
    fun canScheduleExactAlarms(): Boolean
}