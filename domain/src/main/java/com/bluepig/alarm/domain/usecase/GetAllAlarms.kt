package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllAlarms @Inject constructor(
    private val _repository: AlarmRepository,
) {
    // TODO: 현재 Alarm.timeInMillis를 기준으로 하기에 시간, 분 기준이 아니라 수정 필요.(시간, 분은 늦어도 날짜가 빠르면 먼저보여주게됨.)
    operator fun invoke() = _repository.getAllAlarmFlow()
        .map { alarmList ->
            alarmList
                .sortedBy { it.timeInMillis }
                .sortedByDescending { it.isActive }
        }
}