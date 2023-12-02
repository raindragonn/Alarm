package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.alarm.BpAlarmManager
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.result.asyncResultWithContextOf
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class RemoveAlarm @Inject constructor(
    @IoDispatcher private val _dispatcher: CoroutineDispatcher,
    private val _repository: AlarmRepository,
    private val _alarmManager: BpAlarmManager,
) {
    suspend operator fun invoke(alarm: Alarm) = asyncResultWithContextOf(_dispatcher) {
        val alarmId = alarm.id ?: return@asyncResultWithContextOf
        _alarmManager.cancelAlarm(alarm)
        _repository.deleteById(alarmId)
    }
}