@file:Suppress("unused")

package com.bluepig.alarm.domain.util

import java.util.Calendar
import java.util.Locale


object CalendarHelper {
    val now: Calendar
        get() = Calendar.getInstance(Locale.getDefault())

    fun fromHourAndMinute(hourOfDay: Int, minute: Int): Calendar = now.apply {
        set(Calendar.HOUR_OF_DAY, hourOfDay)
        set(Calendar.MINUTE, minute)
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
