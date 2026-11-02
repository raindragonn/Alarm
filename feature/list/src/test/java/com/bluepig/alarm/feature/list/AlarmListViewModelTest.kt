package com.bluepig.alarm.feature.list

import com.bluepig.alarm.domain.entity.alarm.testAlarm
import com.bluepig.alarm.domain.fake.FakeAlarmRepository
import com.bluepig.alarm.domain.fake.FakeBpAlarmManager
import com.bluepig.alarm.domain.usecase.GetAllAlarms
import com.bluepig.alarm.domain.usecase.GetCurrentTime
import com.bluepig.alarm.domain.usecase.GetNextAlarm
import com.bluepig.alarm.domain.usecase.SaveAlarm
import com.bluepig.alarm.domain.util.FixedClockRule
import com.bluepig.alarm.domain.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class AlarmListViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    @get:Rule
    val clock = FixedClockRule()

    private val alarmManager = FakeBpAlarmManager()

    private fun viewModel(repository: FakeAlarmRepository) = AlarmListViewModel(
        GetAllAlarms(repository),
        GetNextAlarm(repository, GetCurrentTime()),
        SaveAlarm(repository, alarmManager),
    )

    // GetCurrentTime이 끝없이 돌아서 advanceUntilIdle 대신 runCurrent
    private fun TestScope.state(viewModel: AlarmListViewModel): AlarmListUiState {
        backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()
        return viewModel.uiState.value
    }

    @Test
    fun `알람이 없으면 빈 상태`() = runTest {
        clock.setNow("2026-10-28T09:00")

        assertEquals(AlarmListUiState.Empty, state(viewModel(FakeAlarmRepository())))
    }

    @Test
    fun `다음 알람까지 남은 시간은 분 단위로 올림`() = runTest {
        clock.setNow("2026-10-28T09:00:30")
        val next = clock.millis("2026-10-28T10:30")
        val repository = FakeAlarmRepository(testAlarm(next, id = 1))

        val state = state(viewModel(repository)) as AlarmListUiState.Alarms

        assertEquals(NextAlarmUi(next, 90), state.next)
    }

    @Test
    fun `모든 알람이 꺼져 있으면 다음 알람 없음`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository(testAlarm(clock.millis("2026-10-28T10:00"), isActive = false, id = 1))

        val state = state(viewModel(repository)) as AlarmListUiState.Alarms

        assertEquals(null, state.next)
        assertEquals(1, state.alarms.size)
    }

    @Test
    fun `알람을 끄면 저장하고 예약을 취소한 뒤 저장 결과를 알림`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val alarm = testAlarm(clock.millis("2026-10-28T10:00"), id = 1)
        val repository = FakeAlarmRepository(alarm)
        val viewModel = viewModel(repository)

        viewModel.onActiveChange(alarm, false)
        val effect = viewModel.effect.first()

        assertFalse((effect as AlarmListEffect.AlarmSaved).alarm.isActive)
        assertFalse(repository.getById(1)!!.isActive)
        assertEquals(listOf(1L), alarmManager.cancelled.map { it.id })
    }
}
