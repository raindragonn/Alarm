package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.result.NotFoundActiveAlarmException
import com.bluepig.alarm.domain.result.NotFoundAlarmException
import com.bluepig.alarm.domain.result.runSuspendCatching
import com.bluepig.alarm.domain.util.CalendarHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetExpiredAlarmTime @Inject constructor(
    private val _repository: AlarmRepository,
) {
    operator fun invoke() = flow {
        while (true) {
            val expireTime = runSuspendCatching {
                val alarmList = _repository.getAllAlarms()
                if (alarmList.isEmpty()) throw NotFoundAlarmException

                val now = CalendarHelper.now.timeInMillis
                val remainingTimeList =
                    alarmList
                        .filter { it.isActive }
                        .map { it.timeInMillis - now }

                val filteredList =
                    remainingTimeList
                        .filter { it > 0 }
                        .sortedBy { it }

                filteredList.firstOrNull() ?: throw NotFoundActiveAlarmException
            }
            emit(expireTime)
            delay(100L)
        }
    }
}
