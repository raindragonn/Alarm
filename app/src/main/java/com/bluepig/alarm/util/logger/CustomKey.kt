package com.bluepig.alarm.util.logger

enum class CustomKey {
    ALARM_TOTAL_COUNT,
    ALARM_ACTIVE_COUNT,
    ALARM_ACTIVE_RECENT_TIME,
    LAST_VISIT_SCREEN,
    APP_CREATED_TIME;

    val lowerCaseName
        get() = name.lowercase()
}