package com.bluepig.alarm.domain.fake

import com.bluepig.alarm.domain.alarm.BpAlarmManager
import com.bluepig.alarm.domain.entity.alarm.Alarm

class FakeBpAlarmManager(var canScheduleExact: Boolean = true) : BpAlarmManager {
    val scheduled = mutableListOf<Alarm>()
    val cancelled = mutableListOf<Alarm>()

    override fun setupAlarm(alarm: Alarm) {
        scheduled += alarm
    }

    override fun cancelAlarm(alarm: Alarm) {
        cancelled += alarm
    }

    override fun canScheduleExactAlarms() = canScheduleExact
}
