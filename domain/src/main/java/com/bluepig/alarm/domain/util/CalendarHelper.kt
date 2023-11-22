package com.bluepig.alarm.domain.util

import java.util.Calendar


object CalendarHelper {
    val now: Calendar
        get() = Calendar.getInstance().apply {
            setZeroSecond()
        }

    fun fromHourAndMinute(hourOfDay: Int, minute: Int): Calendar =
        now.apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            setZeroSecond()
        }

    fun fromTimeInMillis(timeInMillis: Long): Calendar =
        now.apply {
            setTimeInMillis(timeInMillis)
            setZeroSecond()
        }
}

val Calendar.getYear: Int
    get() = get(Calendar.YEAR)
val Calendar.getDayOfYear: Int
    get() = get(Calendar.DAY_OF_YEAR)

fun Calendar.setTomorrow(): Calendar {
    return apply {
        val now = CalendarHelper.now
        val nowYear = now.getYear
        val nowDayOfYear = now.getDayOfYear

        if (now.after(this)) {
            set(Calendar.YEAR, nowYear)
            set(Calendar.DAY_OF_YEAR, nowDayOfYear)
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
}

fun Calendar.setZeroSecond(): Calendar {
    return this.apply {
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}
