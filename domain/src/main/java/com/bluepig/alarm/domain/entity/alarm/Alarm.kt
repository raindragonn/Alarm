package com.bluepig.alarm.domain.entity.alarm

import com.bluepig.alarm.domain.entity.base.BaseEntity
import com.bluepig.alarm.domain.entity.file.File
import com.bluepig.alarm.domain.util.CalendarHelper
import com.bluepig.alarm.domain.util.dayOfWeek
import com.bluepig.alarm.domain.util.hourOfDay
import com.bluepig.alarm.domain.util.minute
import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
data class Alarm(
    val id: Long? = null,
    val timeInMillis: Long = CalendarHelper.now.timeInMillis,
    val isActive: Boolean = true,
    val file: File,
    val repeatWeek: Set<Week> = setOf(),
    val volume: Int? = null,
    val hasVibration: Boolean = true,
    val memo: String = "",
) : BaseEntity, java.io.Serializable {
    fun getCalendar(): Calendar {
        return CalendarHelper.now.apply {
            timeInMillis = this@Alarm.timeInMillis
        }
    }

    /**
     * Get next time alarm
     * [repeatWeek]에 따라 [timeInMillis]를 새로 계산해서 반환한다.
     * @return
     */
    fun getNextTimeAlarm(): Alarm {
        if (isActive.not()) return this

        val toDayCalendar = CalendarHelper
            .todayFromHourAndMinute(getCalendar().hourOfDay, getCalendar().minute)
        val now = CalendarHelper.now

        val tomorrowOrToday = if (now.after(toDayCalendar)) 1 else 0
        val todayWeek = toDayCalendar.dayOfWeek
        val week = Week.fromCode(todayWeek)
        val todayIndex = Week.entries.indexOf(week)
        // repeatWeek이 설정된 경우 1 ~ 7일 이후, 안된 경우 내일(1) 이거나 오늘(0)
        val daysToAdd = (1..7).firstOrNull { dayCount ->
            val dayIndex = (todayIndex + dayCount) % 7
            repeatWeek.contains(Week.entries[dayIndex])
        } ?: tomorrowOrToday

        val newTimeInMillis = toDayCalendar.apply {
            add(Calendar.DAY_OF_YEAR, daysToAdd)
        }.timeInMillis

        return copy(
            timeInMillis = newTimeInMillis
        )
    }
    // TODO: Week 포함하는 경우 timeInMillis를 변경하는 것이 아닌 hour, minute을 통해 NextAlarmTimeInMillis를 구하는걸로 고려하기
}