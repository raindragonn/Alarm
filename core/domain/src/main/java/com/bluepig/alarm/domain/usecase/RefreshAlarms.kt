package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.alarm.BpAlarmManager
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.repository.AlarmRepository
import com.bluepig.alarm.domain.util.CalendarHelper
import java.time.Instant
import java.time.ZoneId
import java.util.TimeZone
import javax.inject.Inject

/**
 * 활성 알람을 다시 예약한다 (재부팅·앱 시작·권한·시간대 변경).
 * 시간대가 바뀌었으면 예약했던 시간대의 벽시계 시각 그대로 옮기고 (U1),
 * 하루 안에 지나간 회차를 놓친 알람으로 돌려준다 (C7). 지나간 1회성 알람은 꺼서 저장한다
 */
class RefreshAlarms @Inject constructor(
    private val _repository: AlarmRepository,
    private val _alarmManager: BpAlarmManager,
) {
    suspend operator fun invoke(): List<Alarm> {
        val zoneId = TimeZone.getDefault().id
        val lastZoneId = _repository.getScheduledTimeZoneId()
        var alarms = _repository.getAllAlarms().filter(Alarm::isActive)
        if (lastZoneId != null && lastZoneId != zoneId) {
            alarms = alarms.map { it.copy(timeInMillis = sameWallClock(it.timeInMillis, lastZoneId, zoneId)) }
            alarms.forEach { _repository.insertAlarm(it) }
        }
        _repository.setScheduledTimeZoneId(zoneId)

        val now = CalendarHelper.now.timeInMillis
        val missed = alarms.filter { it.timeInMillis < now && now - it.timeInMillis < MISSED_WINDOW }
        val checked = alarms.map(Alarm::getActiveCheckedAlarm)
        checked.filterNot(Alarm::isActive).forEach { _repository.insertAlarm(it) }
        checked.forEach(_alarmManager::setupAlarm)
        return missed
    }

    private fun sameWallClock(timeInMillis: Long, from: String, to: String): Long =
        Instant.ofEpochMilli(timeInMillis).atZone(ZoneId.of(from)).toLocalDateTime()
            .atZone(ZoneId.of(to)).toInstant().toEpochMilli()

    private companion object {
        const val MISSED_WINDOW = 24 * 60 * 60 * 1000L
    }
}
