package com.bluepig.alarm.domain.util

import org.junit.rules.ExternalResource
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.TimeZone

/**
 * [CalendarHelper]의 현재 시각과 기본 시간대를 고정하고, 테스트가 끝나면 되돌린다.
 * 시각은 `2026-10-28T07:30` 형식의 벽시계 시각으로 주고받는다.
 */
class FixedClockRule : ExternalResource() {
    private val originalTimeZone = TimeZone.getDefault()
    private var zone = ZoneId.systemDefault()

    fun setNow(dateTime: String, zoneId: String = "Asia/Seoul") {
        zone = ZoneId.of(zoneId)
        TimeZone.setDefault(TimeZone.getTimeZone(zone))
        val now = millis(dateTime)
        CalendarHelper.currentTimeMillis = { now }
    }

    fun millis(dateTime: String): Long =
        LocalDateTime.parse(dateTime).atZone(zone).toInstant().toEpochMilli()

    fun dateTime(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(zone).toLocalDateTime().toString()

    override fun after() {
        CalendarHelper.currentTimeMillis = System::currentTimeMillis
        TimeZone.setDefault(originalTimeZone)
    }
}
