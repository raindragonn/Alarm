package com.bluepig.alarm.domain.alarm

import com.bluepig.alarm.domain.entity.alarm.Alarm

interface BpAlarmManager {
    fun setupAlarm(alarm: Alarm)
    fun cancelAlarm(alarm: Alarm)
}