package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.alarm.BpAlarmManager
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.result.runSuspendCatching
import javax.inject.Inject

class RemoveAlarm @Inject constructor(
    private val _repository: AlarmRepository,
    private val _alarmManager: BpAlarmManager,
) {
    suspend operator fun invoke(alarm: Alarm) = runSuspendCatching {
        val alarmId = alarm.id ?: return@runSuspendCatching
        _alarmManager.cancelAlarm(alarm)
        _repository.deleteById(alarmId)
    }
}
