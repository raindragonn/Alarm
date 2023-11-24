package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.result.NotFoundActiveAlarmException
import com.bluepig.alarm.domain.result.NotFoundAlarmException
import com.bluepig.alarm.domain.result.asyncResultWithContextOf
import com.bluepig.alarm.domain.util.CalendarHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetExpiredAlarmTime @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _repository: AlarmRepository,
) {
    // TODO("실제 알람이 울린 이후 꺼짐 확인 및 요일 추가시 수정 필요")
    suspend operator fun invoke() = flow {
        while (true) {
            val expireTime = asyncResultWithContextOf(_dispatcher) {
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