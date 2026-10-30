package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.testAlarm
import com.bluepig.alarm.domain.fake.FakeAlarmRepository
import com.bluepig.alarm.domain.fake.FakeBpAlarmManager
import com.bluepig.alarm.domain.util.FixedClockRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SaveAlarmTest {
    @get:Rule
    val clock = FixedClockRule()

    private val repository = FakeAlarmRepository()
    private val alarmManager = FakeBpAlarmManager()

    @Test
    fun `다음 울릴 시각으로 저장하고 예약한다`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val saveAlarm = SaveAlarm(repository, alarmManager)

        val saved = saveAlarm(testAlarm(clock.millis("2026-10-28T07:30"))).getOrThrow()

        assertEquals("2026-10-29T07:30", clock.dateTime(saved.timeInMillis))
        assertEquals(saved, repository.getById(requireNotNull(saved.id)))
        assertEquals(listOf(saved), alarmManager.scheduled)
    }

    @Test
    fun `비활성 알람은 시각을 그대로 저장하고 예약을 취소한다`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val saveAlarm = SaveAlarm(repository, alarmManager)
        val alarm = testAlarm(clock.millis("2026-10-28T07:30"), isActive = false, id = 3)

        val saved = saveAlarm(alarm).getOrThrow()

        assertEquals(alarm, saved)
        assertEquals(listOf(alarm), alarmManager.cancelled)
        assertTrue(alarmManager.scheduled.isEmpty())
    }
}
