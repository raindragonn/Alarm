package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.util.hourOfDay
import com.bluepig.alarm.domain.util.minute
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Get all alarms
 *
 * 현재 저장된 알람 리스트를 시간으로 정렬해서 가져온다.
 *
 * @property _repository
 * @constructor Create empty Get all alarms
 */
class GetAllAlarms @Inject constructor(
    private val _repository: AlarmRepository,
) {
    operator fun invoke() = _repository.getAllAlarmFlow()
        .map { alarmList ->
            alarmList.sortedBy {
                val calendar = it.getCalendar()
                (calendar.hourOfDay * 60) + calendar.minute
            }
        }
}