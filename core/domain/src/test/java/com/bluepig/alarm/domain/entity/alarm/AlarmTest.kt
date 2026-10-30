package com.bluepig.alarm.domain.entity.alarm

import com.bluepig.alarm.domain.entity.alarm.Week.FRIDAY
import com.bluepig.alarm.domain.entity.alarm.Week.MONDAY
import com.bluepig.alarm.domain.entity.alarm.Week.SUNDAY
import com.bluepig.alarm.domain.entity.alarm.Week.THURSDAY
import com.bluepig.alarm.domain.entity.alarm.Week.TUESDAY
import com.bluepig.alarm.domain.entity.alarm.Week.WEDNESDAY
import com.bluepig.alarm.domain.util.FixedClockRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// 2026-10-28은 수요일
class AlarmTest {
    @get:Rule
    val clock = FixedClockRule()

    private fun nextTime(alarmTime: String, vararg repeatWeek: Week): String {
        val alarm = testAlarm(clock.millis(alarmTime), repeatWeek.toSet())
        return clock.dateTime(alarm.getNextTimeAlarm().timeInMillis)
    }

    @Test
    fun `1회성 알람이 오늘 아직 안 지난 시각이면 오늘`() {
        clock.setNow("2026-10-28T09:00")
        assertEquals("2026-10-28T10:00", nextTime("2026-10-28T10:00"))
    }

    @Test
    fun `1회성 알람이 오늘 이미 지난 시각이면 내일`() {
        clock.setNow("2026-10-28T09:00")
        assertEquals("2026-10-29T07:30", nextTime("2026-10-28T07:30"))
    }

    @Test
    fun `저장된 날짜가 지났어도 시·분으로 다음 시각을 계산한다`() {
        clock.setNow("2026-10-28T09:00")
        assertEquals("2026-10-29T07:30", nextTime("2026-10-01T07:30"))
    }

    @Test
    fun `반복 요일에 오늘이 있고 아직 안 지났으면 오늘`() {
        clock.setNow("2026-10-28T09:00")
        assertEquals("2026-10-28T10:00", nextTime("2026-10-21T10:00", WEDNESDAY))
    }

    @Test
    fun `반복 요일이 오늘뿐이고 이미 지났으면 다음 주 같은 요일`() {
        clock.setNow("2026-10-28T09:00")
        assertEquals("2026-11-04T07:30", nextTime("2026-10-21T07:30", WEDNESDAY))
    }

    @Test
    fun `반복 요일 중 가장 가까운 다음 요일`() {
        clock.setNow("2026-10-28T09:00")
        assertEquals("2026-10-30T07:30", nextTime("2026-10-21T07:30", MONDAY, FRIDAY))
    }

    @Test
    fun `반복 요일이 주를 넘기면 다음 주 첫 요일`() {
        clock.setNow("2026-10-30T09:00")
        val weekdays = arrayOf(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)
        assertEquals("2026-11-02T07:30", nextTime("2026-10-23T07:30", *weekdays))
    }

    @Test
    fun `자정 직전에 자정 이후 시각이면 다음 날`() {
        clock.setNow("2026-10-28T23:50")
        assertEquals("2026-10-29T00:10", nextTime("2026-10-28T00:10"))
        assertEquals("2026-10-29T00:10", nextTime("2026-10-22T00:10", THURSDAY))
    }

    @Test
    fun `연말 자정 넘김은 다음 해 1월 1일`() {
        clock.setNow("2026-12-31T23:50")
        assertEquals("2027-01-01T00:10", nextTime("2026-12-31T00:10"))
    }

    @Test
    fun `서머타임이 끝나는 날에도 같은 벽시계 시각`() {
        // 2026-11-01(일) 02:00에 서머타임 종료
        clock.setNow("2026-10-31T09:00", zoneId = "America/New_York")
        assertEquals("2026-11-01T07:00", nextTime("2026-10-25T07:00", SUNDAY))
    }

    @Test
    fun `비활성 알람은 그대로`() {
        clock.setNow("2026-10-28T09:00")
        val alarm = testAlarm(clock.millis("2026-10-28T07:30"), isActive = false)
        assertEquals(alarm, alarm.getNextTimeAlarm())
        assertEquals(alarm, alarm.getActiveCheckedAlarm())
    }

    @Test
    fun `울린 1회성 알람은 시각을 그대로 두고 비활성`() {
        clock.setNow("2026-10-28T07:30:10")
        val alarm = testAlarm(clock.millis("2026-10-28T07:30"))
        assertEquals(alarm.copy(isActive = false), alarm.getActiveCheckedAlarm())
    }

    @Test
    fun `울린 반복 알람은 다음 회차로`() {
        clock.setNow("2026-10-28T07:30:10")
        val checked = testAlarm(clock.millis("2026-10-28T07:30"), setOf(WEDNESDAY))
            .getActiveCheckedAlarm()
        assertTrue(checked.isActive)
        assertEquals("2026-11-04T07:30", clock.dateTime(checked.timeInMillis))
    }

    @Test
    fun `아직 안 울린 1회성 알람은 활성 그대로`() {
        clock.setNow("2026-10-28T09:00")
        val alarm = testAlarm(clock.millis("2026-10-29T07:30"))
        assertEquals(alarm, alarm.getActiveCheckedAlarm())
    }
}
