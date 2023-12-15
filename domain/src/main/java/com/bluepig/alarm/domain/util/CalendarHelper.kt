@file:Suppress("unused")

package com.bluepig.alarm.domain.util

import com.bluepig.alarm.domain.entity.alarm.Week
import java.util.Calendar
import java.util.Locale


object CalendarHelper {
    val now: Calendar
        get() = Calendar.getInstance(Locale.getDefault())

    fun todayFromHourAndMinute(hourOfDay: Int, minute: Int): Calendar = now.apply {
        set(Calendar.HOUR_OF_DAY, hourOfDay)
        set(Calendar.MINUTE, minute)
        setZeroSecond()
    }

    fun fromTimeInMillis(timeInMillis: Long): Calendar = now.apply {
        setTimeInMillis(timeInMillis)
    }

    fun setTomorrow(calendar: Calendar): Calendar {
        val newCalendar = now.apply {
            timeInMillis = calendar.timeInMillis
        }
        val now = CalendarHelper.now
        val nowYear = now.year
        val nowDayOfYear = now.dayOfYear

        if (now.after(newCalendar)) {
            newCalendar.set(Calendar.YEAR, nowYear)
            newCalendar.set(Calendar.DAY_OF_YEAR, nowDayOfYear)
            newCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return newCalendar
    }

    fun getNextTimeMillis(calendar: Calendar, repeatWeeks: Set<Week>): Long {
        val toDayCalendar = todayFromHourAndMinute(calendar.hourOfDay, calendar.minute)
        val now = CalendarHelper.now

        val tomorrowOrToday = if (now.after(toDayCalendar)) 1 else 0
        val todayWeek = toDayCalendar.dayOfWeek
        val week = Week.fromCode(todayWeek)
        val todayIndex = Week.entries.indexOf(week)
        // repeatWeek이 설정된 경우 1 ~ 7일 이후, 안된 경우 내일(1) 이거나 오늘(0)
        var daysToAdd = (1..7).firstOrNull { dayCount ->
            val dayIndex = (todayIndex + dayCount) % 7
            repeatWeeks.contains(Week.entries[dayIndex])
        } ?: tomorrowOrToday

        // 설정한 요일중 현재 요일을 포함하며 아직 시간이 지나지 않은 경우
        if (repeatWeeks.contains(week) && now.before(toDayCalendar)) {
            daysToAdd = 0
        }

        return toDayCalendar.apply {
            add(Calendar.DAY_OF_YEAR, daysToAdd)
        }.timeInMillis
    }
}

val Calendar.year: Int
    get() = get(Calendar.YEAR)
val Calendar.dayOfYear: Int
    get() = get(Calendar.DAY_OF_YEAR)
val Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)
val Calendar.hourOfDay: Int
    get() = get(Calendar.HOUR_OF_DAY)
val Calendar.minute: Int
    get() = get(Calendar.MINUTE)
val Calendar.second: Int
    get() = get(Calendar.SECOND)
val Calendar.milliSecond: Int
    get() = get(Calendar.MILLISECOND)

fun Calendar.setZeroSecond(): Calendar {
    return this.apply {
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}
