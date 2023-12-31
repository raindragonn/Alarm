package com.bluepig.alarm.domain.entity.alarm

import java.util.Calendar

enum class Week(val code: Int, val isWeekend: Boolean = false) {
    SUNDAY(Calendar.SUNDAY, true),
    MONDAY(Calendar.MONDAY),
    TUESDAY(Calendar.TUESDAY),
    WEDNESDAY(Calendar.WEDNESDAY),
    THURSDAY(Calendar.THURSDAY),
    FRIDAY(Calendar.FRIDAY),
    SATURDAY(Calendar.SATURDAY, true);

    companion object {
        fun fromCode(code: Int): Week {
            return when (code) {
                SUNDAY.code -> SUNDAY
                MONDAY.code -> MONDAY
                TUESDAY.code -> TUESDAY
                WEDNESDAY.code -> WEDNESDAY
                THURSDAY.code -> THURSDAY
                FRIDAY.code -> FRIDAY
                SATURDAY.code -> SATURDAY
                else -> throw IllegalArgumentException()
            }
        }
    }
}