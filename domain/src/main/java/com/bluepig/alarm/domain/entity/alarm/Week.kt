package com.bluepig.alarm.domain.entity.alarm

import java.util.Calendar

enum class Week(val code: Int) {
    SUNDAY(Calendar.SUNDAY),
    MONDAY(Calendar.MONDAY),
    TUESDAY(Calendar.TUESDAY),
    WEDNESDAY(Calendar.WEDNESDAY),
    THURSDAY(Calendar.THURSDAY),
    FRIDAY(Calendar.FRIDAY),
    SATURDAY(Calendar.SATURDAY);

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