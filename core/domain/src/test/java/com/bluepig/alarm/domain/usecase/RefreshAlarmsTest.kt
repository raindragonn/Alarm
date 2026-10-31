package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.Week.FRIDAY
import com.bluepig.alarm.domain.entity.alarm.Week.MONDAY
import com.bluepig.alarm.domain.entity.alarm.testAlarm
import com.bluepig.alarm.domain.fake.FakeAlarmRepository
import com.bluepig.alarm.domain.fake.FakeBpAlarmManager
import com.bluepig.alarm.domain.util.FixedClockRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class RefreshAlarmsTest {
    @get:Rule
    val clock = FixedClockRule()

    @Test
    fun `활성 알람만 다음 울릴 시각으로 예약한다`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository(
            testAlarm(clock.millis("2026-10-30T07:30"), setOf(MONDAY, FRIDAY), id = 1),
            testAlarm(clock.millis("2026-10-28T10:00"), isActive = false, id = 2),
            testAlarm(clock.millis("2026-10-29T07:30"), id = 3),
        )
        val alarmManager = FakeBpAlarmManager()

        RefreshAlarms(repository, alarmManager)()

        assertEquals(
            listOf(1L to "2026-10-30T07:30", 3L to "2026-10-29T07:30"),
            alarmManager.scheduled.map { it.id to clock.dateTime(it.timeInMillis) },
        )
    }

    @Test
    fun `시간대가 바뀌면 예약했던 시간대의 벽시계 시각 그대로 다시 예약한다`() = runTest {
        clock.setNow("2026-10-28T09:00", zoneId = "Asia/Seoul")
        val sevenThirtySeoul = clock.millis("2026-10-29T07:30")
        val repository = FakeAlarmRepository(testAlarm(sevenThirtySeoul, setOf(MONDAY), id = 1))
        repository.scheduledTimeZoneId = "Asia/Seoul"
        val alarmManager = FakeBpAlarmManager()

        clock.setNow("2026-10-28T09:00", zoneId = "Europe/Paris")
        RefreshAlarms(repository, alarmManager)()

        assertEquals(listOf("2026-11-02T07:30"), alarmManager.scheduled.map { clock.dateTime(it.timeInMillis) })
        assertEquals("Europe/Paris", repository.scheduledTimeZoneId)
    }

    @Test
    fun `하루 안에 지나간 회차는 놓친 알람으로 돌려준다`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository(
            testAlarm(clock.millis("2026-10-28T07:30"), id = 1),
            testAlarm(clock.millis("2026-10-20T07:30"), setOf(MONDAY), id = 2),
            testAlarm(clock.millis("2026-10-29T07:30"), id = 3),
        )

        val missed = RefreshAlarms(repository, FakeBpAlarmManager())()

        assertEquals(listOf(1L), missed.map { it.id })
    }

    @Test
    fun `지나간 1회성 알람은 꺼서 저장한다`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository(
            testAlarm(clock.millis("2026-10-28T07:30"), id = 1),
            testAlarm(clock.millis("2026-10-20T07:30"), setOf(MONDAY), id = 2),
        )

        RefreshAlarms(repository, FakeBpAlarmManager())()

        assertEquals(listOf(1L to false, 2L to true), repository.getAllAlarms().map { it.id to it.isActive })
    }
}
