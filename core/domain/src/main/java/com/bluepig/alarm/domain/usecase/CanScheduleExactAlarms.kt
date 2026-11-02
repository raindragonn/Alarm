package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.alarm.BpAlarmManager
import javax.inject.Inject

class CanScheduleExactAlarms @Inject constructor(
    private val _alarmManager: BpAlarmManager,
) {
    operator fun invoke(): Boolean = _alarmManager.canScheduleExactAlarms()
}
