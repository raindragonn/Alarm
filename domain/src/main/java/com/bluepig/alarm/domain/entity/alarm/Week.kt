package com.bluepig.alarm.domain.entity.alarm

enum class Week(val code: Int) {
    SUNDAY(1),
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),
    SATURDAY(7);

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