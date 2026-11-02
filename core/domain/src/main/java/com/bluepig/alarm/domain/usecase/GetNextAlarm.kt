package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/** 다음에 울릴 알람 시각과 그때까지 남은 시간 */
data class NextAlarm(val timeInMillis: Long, val remainingMillis: Long)

/**
 * 다음에 울릴 알람. 울릴 알람이 없으면 null. 알람 목록이 바뀔 때와 [GetCurrentTime]의 시각마다 다시 계산한다.
 * [RefreshAlarms]가 예약하는 시각과 같게, 반복 알람은 저장된 시각이 지났어도 다음 회차로 센다 (U2)
 */
class GetNextAlarm @Inject constructor(
    private val _repository: AlarmRepository,
    private val _getCurrentTime: GetCurrentTime,
) {
    operator fun invoke(): Flow<NextAlarm?> =
        combine(_repository.getAllAlarmFlow(), _getCurrentTime()) { alarms, now ->
            nextAlarm(alarms, now)
        }

    private fun nextAlarm(alarms: List<Alarm>, now: Long): NextAlarm? = alarms
        .filter { it.isActive }
        .map { it.getActiveCheckedAlarm() }
        .filter { it.isActive && it.timeInMillis > now }
        .minOfOrNull { it.timeInMillis }
        ?.let { NextAlarm(it, it - now) }
}
