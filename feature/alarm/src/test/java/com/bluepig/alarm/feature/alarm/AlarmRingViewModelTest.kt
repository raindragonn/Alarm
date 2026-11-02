package com.bluepig.alarm.feature.alarm

import androidx.lifecycle.SavedStateHandle
import com.bluepig.alarm.domain.entity.alarm.testAlarm
import com.bluepig.alarm.domain.fake.FakeAlarmRepository
import com.bluepig.alarm.domain.usecase.GetAlarmById
import com.bluepig.alarm.domain.usecase.GetCurrentTime
import com.bluepig.alarm.domain.util.FixedClockRule
import com.bluepig.alarm.domain.util.MainDispatcherRule
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AlarmRingViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    @get:Rule
    val clock = FixedClockRule()

    private fun viewModel(repository: FakeAlarmRepository, vararg args: Pair<String, Any>) =
        AlarmRingViewModel(SavedStateHandle(mapOf(*args)), GetAlarmById(repository), GetCurrentTime())

    // GetCurrentTime이 끝없이 돌아서 advanceUntilIdle 대신 runCurrent
    private fun TestScope.state(viewModel: AlarmRingViewModel): AlarmRingUiState {
        backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()
        return viewModel.uiState.value
    }

    @Test
    fun `알람 id로 울리는 알람을 불러오고 지금 시각을 분 단위로 내린다`() = runTest {
        clock.setNow("2026-10-28T07:30:45")
        val alarm = testAlarm(clock.millis("2026-10-28T07:30"), id = 1)

        val state = state(viewModel(FakeAlarmRepository(alarm), AlarmRingViewModel.KEY_ALARM_ID to 1L))

        assertEquals(AlarmRingUiState.Ringing(alarm, isPreview = false, clock.millis("2026-10-28T07:30")), state)
    }

    @Test
    fun `미리보기는 넘겨받은 알람을 쓴다`() = runTest {
        clock.setNow("2026-10-28T07:30")
        val preview = testAlarm(clock.millis("2026-10-28T08:00"))

        val state = state(viewModel(FakeAlarmRepository(), AlarmRingViewModel.KEY_PREVIEW_ALARM to preview))

        assertEquals(AlarmRingUiState.Ringing(preview, isPreview = true, clock.millis("2026-10-28T07:30")), state)
    }

    @Test
    fun `지워진 알람이면 NotFound`() = runTest {
        val state = state(viewModel(FakeAlarmRepository(), AlarmRingViewModel.KEY_ALARM_ID to 1L))

        assertEquals(AlarmRingUiState.NotFound, state)
    }
}
