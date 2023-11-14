package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.di.IoDispatcher
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.result.asyncResultWithContextOf
import com.bluepig.alarm.domain.util.CalendarHelper
import com.bluepig.alarm.domain.util.setTomorrow
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * Save alarm
 * 알람 저장 - 이미 지난 시간의 경우 하루를 더한다.
 * @property _dispatcher
 * @property _repository
 * @constructor Create empty Save alarm
 */
class SaveAlarm @Inject constructor(
    @IoDispatcher
    private val _dispatcher: CoroutineDispatcher,
    private val _repository: AlarmRepository,
) {

    suspend operator fun invoke(alarm: Alarm) =
        asyncResultWithContextOf(_dispatcher) {
            val checkedAlarm = checkOverTimeAlarm(alarm)

            val id = _repository.insertAlarm(checkedAlarm)
            _repository.getById(id) ?: throw NullPointerException("Not Found Saved Alarm..id:$id")
        }

    /**
     * Check over time alarm
     * 현재 시간과 알람의 timeInMillis와 비교해서 이미 지난 알람의 경우 내일로 변경
     * @param alarm 확인할 알람
     * @return 업데이트된 [Alarm] 을 반환
     */
    private fun checkOverTimeAlarm(alarm: Alarm): Alarm {
        val alarmCalendar = alarm.getCalendar()
        val now = CalendarHelper.now

        return if (now.after(alarmCalendar)) {
            val nextTime = alarm.getCalendar().setTomorrow().timeInMillis
            alarm.copy(
                timeInMillis = nextTime
            )
        } else {
            alarm
        }
    }
}