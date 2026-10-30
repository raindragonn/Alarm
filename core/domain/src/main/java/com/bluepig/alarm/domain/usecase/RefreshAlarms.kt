package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.alarm.BpAlarmManager
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import javax.inject.Inject

class RefreshAlarms @Inject constructor(
    private val _repository: AlarmRepository,
    private val _alarmManager: BpAlarmManager,
) {
    suspend operator fun invoke() {
        _repository.getAllAlarms()
            .filter(Alarm::isActive)
            .map(Alarm::getActiveCheckedAlarm)
            .forEach {
                _alarmManager.setupAlarm(it)
            }
    }
}
