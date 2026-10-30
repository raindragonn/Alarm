package com.bluepig.alarm.domain.usecase

import com.bluepig.alarm.domain.entity.alarm.Week.MONDAY
import com.bluepig.alarm.domain.entity.alarm.testAlarm
import com.bluepig.alarm.domain.fake.FakeAlarmRepository
import com.bluepig.alarm.domain.util.FixedClockRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

// 2026-10-28은 수요일
class GetNextAlarmTest {
    @get:Rule
    val clock = FixedClockRule()

    private suspend fun next(repository: FakeAlarmRepository) =
        GetNextAlarm(repository, GetCurrentTime())().first()

    @Test
    fun `가장 먼저 울릴 활성 알람의 시각과 남은 시간`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository(
            testAlarm(clock.millis("2026-10-28T08:00"), isActive = false, id = 1),
            testAlarm(clock.millis("2026-10-29T07:30"), id = 2),
            testAlarm(clock.millis("2026-10-28T10:00"), id = 3),
        )

        assertEquals(NextAlarm(clock.millis("2026-10-28T10:00"), 60 * 60 * 1000L), next(repository))
    }

    // U2: RefreshAlarms는 다음 회차를 예약하지만 DB 시각은 그대로 지난 시각으로 남아 있음
    @Test
    fun `저장된 시각이 지난 반복 알람은 다음 회차로 센다`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository(
            testAlarm(clock.millis("2026-10-26T07:30"), setOf(MONDAY), id = 1),
        )
        val nextMonday = clock.millis("2026-11-02T07:30")

        assertEquals(NextAlarm(nextMonday, nextMonday - clock.millis("2026-10-28T09:00")), next(repository))
    }

    @Test
    fun `시각이 지난 1회성 알람만 있으면 없음`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository(testAlarm(clock.millis("2026-10-28T08:00"), id = 1))

        assertNull(next(repository))
    }

    @Test
    fun `알람이 없으면 없음`() = runTest {
        clock.setNow("2026-10-28T09:00")

        assertNull(next(FakeAlarmRepository()))
    }

    @Test
    fun `수집 중에 알람을 추가하면 다시 계산한다`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository()
        val results = mutableListOf<NextAlarm?>()
        val job = launch { GetNextAlarm(repository, GetCurrentTime())().collect { results += it } }
        runCurrent()

        repository.insertAlarm(testAlarm(clock.millis("2026-10-28T09:30")))
        runCurrent()
        job.cancel()

        assertNull(results.first())
        assertEquals(30 * 60 * 1000L, results.last()?.remainingMillis)
    }
}
