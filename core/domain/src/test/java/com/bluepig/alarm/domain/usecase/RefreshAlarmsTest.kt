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
}
