package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.alarm.BpAlarmManager
import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.result.AlarmSaveFailedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Save alarm
 * 알람 저장 - 지정된 요일에 따라 알람 시간을 계산한다. (지정된 요일까지 날짜 더하기 혹은 다음 날)
 * @property _dispatcher
 * @property _repository
 * @constructor Create empty Save alarm
 */
class SaveAlarm @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _repository: AlarmRepository,
    private val _alarmManager: BpAlarmManager,
) {
    suspend operator fun invoke(alarm: Alarm) =
        kotlin.runCatching {
            withContext(_dispatcher) {
                val checkedAlarm = alarm.getNextTimeAlarm()
                val id = _repository.insertAlarm(checkedAlarm)
                _repository.getById(id) ?: throw AlarmSaveFailedException(id)
            }
        }.onSuccess {
            if (it.isActive) {
                _alarmManager.setupAlarm(it)
            } else {
                _alarmManager.cancelAlarm(it)
            }
        }
}