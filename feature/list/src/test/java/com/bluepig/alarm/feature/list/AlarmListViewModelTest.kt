package com.bluepig.alarm.feature.list

import com.bluepig.alarm.domain.entity.alarm.testAlarm
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.fake.FakeAlarmRepository
import com.bluepig.alarm.domain.fake.FakeAppConfigRepository
import com.bluepig.alarm.domain.fake.FakeBpAlarmManager
import com.bluepig.alarm.domain.usecase.DismissOverlayNotice
import com.bluepig.alarm.domain.usecase.GetAllAlarms
import com.bluepig.alarm.domain.usecase.GetCurrentTime
import com.bluepig.alarm.domain.usecase.GetNextAlarm
import com.bluepig.alarm.domain.usecase.IsOverlayNoticeDismissed
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AlarmListViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    @get:Rule
    val clock = FixedClockRule()

    private val alarmManager = FakeBpAlarmManager()

    private fun viewModel(
        repository: FakeAlarmRepository,
        config: FakeAppConfigRepository = FakeAppConfigRepository(),
    ) = AlarmListViewModel(
        GetAllAlarms(repository),
        GetNextAlarm(repository, GetCurrentTime()),
        SaveAlarm(repository, alarmManager),
        IsOverlayNoticeDismissed(config),
        DismissOverlayNotice(config),
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

    @Test
    fun `켜진 유튜브 알람이 있으면 닫았어도 유튜브 권한 안내`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val youtube = testAlarm(clock.millis("2026-10-28T10:00"), id = 1).copy(media = TubeMedia("v", "", "영상"))
        val repository = FakeAlarmRepository(youtube, testAlarm(clock.millis("2026-10-28T11:00"), id = 2))

        val state = state(viewModel(repository, FakeAppConfigRepository(overlayNoticeDismissed = true)))

        assertEquals(OverlayNotice.YouTube, (state as AlarmListUiState.Alarms).overlayNotice)
    }

    @Test
    fun `꺼진 유튜브 알람만 있으면 일반 권한 안내`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val youtube = testAlarm(clock.millis("2026-10-28T10:00"), isActive = false, id = 1)
            .copy(media = TubeMedia("v", "", "영상"))
        val repository = FakeAlarmRepository(youtube, testAlarm(clock.millis("2026-10-28T11:00"), id = 2))

        val state = state(viewModel(repository))

        assertEquals(OverlayNotice.General, (state as AlarmListUiState.Alarms).overlayNotice)
    }

    @Test
    fun `일반 안내를 닫으면 저장하고 다시 띄우지 않음`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val config = FakeAppConfigRepository()
        val viewModel = viewModel(FakeAlarmRepository(testAlarm(clock.millis("2026-10-28T10:00"), id = 1)), config)
        state(viewModel)

        viewModel.onDismissOverlayNotice()
        runCurrent()

        assertEquals(null, (viewModel.uiState.value as AlarmListUiState.Alarms).overlayNotice)
        assertTrue(config.overlayNoticeDismissed)
    }

    @Test
    fun `켜진 알람이 없으면 권한 안내 없음`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository(testAlarm(clock.millis("2026-10-28T10:00"), isActive = false, id = 1))

        val state = state(viewModel(repository))

        assertEquals(null, (state as AlarmListUiState.Alarms).overlayNotice)
    }
}
