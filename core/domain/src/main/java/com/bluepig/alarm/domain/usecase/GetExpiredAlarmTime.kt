package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.result.NotFoundActiveAlarmException
import com.bluepig.alarm.domain.result.NotFoundAlarmException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * 다음 알람까지 남은 시간. 알람 목록이 바뀔 때와 [GetCurrentTime]의 시각마다 다시 계산한다.
 * [RefreshAlarms]가 예약하는 시각과 같게, 반복 알람은 저장된 시각이 지났어도 다음 회차로 센다 (U2)
 */
class GetExpiredAlarmTime @Inject constructor(
    private val _repository: AlarmRepository,
    private val _getCurrentTime: GetCurrentTime,
) {
    operator fun invoke(): Flow<Result<Long>> =
        combine(_repository.getAllAlarmFlow(), _getCurrentTime()) { alarms, now ->
            remainingTime(alarms, now)
        }.catch { emit(Result.failure(it)) }

    private fun remainingTime(alarms: List<Alarm>, now: Long): Result<Long> {
        if (alarms.isEmpty()) return Result.failure(NotFoundAlarmException)
        val remaining = alarms
            .filter { it.isActive }
            .map { it.getActiveCheckedAlarm() }
            .filter { it.isActive }
            .map { it.timeInMillis - now }
            .filter { it > 0 }
            .minOrNull()
            ?: return Result.failure(NotFoundActiveAlarmException)
        return Result.success(remaining)
    }
}
