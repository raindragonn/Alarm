package com.bluepig.alarm.feature.edit

import androidx.lifecycle.SavedStateHandle
import com.bluepig.alarm.domain.alarm.AlarmVolume
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.entity.alarm.testAlarm
import com.bluepig.alarm.domain.fake.FakeAlarmRepository
import com.bluepig.alarm.domain.fake.FakeBpAlarmManager
import com.bluepig.alarm.domain.usecase.GetAlarmById
import com.bluepig.alarm.domain.usecase.GetCurrentTime
import com.bluepig.alarm.domain.usecase.GetExpiredTime
import com.bluepig.alarm.domain.usecase.GetMaxVolume
import com.bluepig.alarm.domain.usecase.RemoveAlarm
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AlarmEditViewModelTest {
    @get:Rule
    val mainDispatcher = MainDispatcherRule()

    @get:Rule
    val clock = FixedClockRule()

    private val alarmManager = FakeBpAlarmManager()
    private val ringtone = RingtoneMedia(1, "Galaxy Bells", "content://ringtone/1")

    private fun viewModel(repository: FakeAlarmRepository, alarmId: Long = -1) = AlarmEditViewModel(
        SavedStateHandle(mapOf(AlarmEditViewModel.KEY_ALARM_ID to alarmId)),
        GetAlarmById(repository),
        GetCurrentTime(),
        GetMaxVolume(object : AlarmVolume {
            override fun maxVolume() = 15
        }),
        GetExpiredTime(),
        SaveAlarm(repository, alarmManager),
        RemoveAlarm(repository, alarmManager),
    )

    // GetCurrentTime이 끝없이 돌아서 advanceUntilIdle 대신 runCurrent
    private fun TestScope.editing(viewModel: AlarmEditViewModel): AlarmEditUiState.Editing {
        backgroundScope.launch { viewModel.uiState.collect {} }
        runCurrent()
        return viewModel.uiState.value as AlarmEditUiState.Editing
    }

    @Test
    fun `새 알람은 지금 시각, 최대 볼륨, 알람음 없음으로 시작`() = runTest {
        clock.setNow("2026-10-28T09:05")

        val draft = editing(viewModel(FakeAlarmRepository())).draft

        assertTrue(draft.isNew)
        assertEquals(9 to 5, draft.hourOfDay to draft.minute)
        assertEquals(15, draft.volume)
        assertNull(draft.media)
    }

    @Test
    fun `기존 알람은 저장된 값으로 시작`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val alarm = testAlarm(clock.millis("2026-10-28T07:30"), setOf(Week.MONDAY), id = 3)

        val draft = editing(viewModel(FakeAlarmRepository(alarm), alarmId = 3)).draft

        assertEquals(3L, draft.id)
        assertEquals(7 to 30, draft.hourOfDay to draft.minute)
        assertEquals(setOf(Week.MONDAY), draft.repeatWeek)
    }

    @Test
    fun `남은 시간은 분 단위로 올림`() = runTest {
        clock.setNow("2026-10-28T09:00:30")
        val viewModel = viewModel(FakeAlarmRepository())
        editing(viewModel)

        viewModel.onTimeChange(10, 30)

        assertEquals(90L, editing(viewModel).remainingMinutes)
    }

    @Test
    fun `알람음 없이 저장하면 알람음을 고르라고 알림`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository()
        val viewModel = viewModel(repository)
        editing(viewModel)

        viewModel.onSave()

        assertEquals(AlarmEditEffect.MediaRequired, viewModel.effect.first())
        assertTrue(repository.getAllAlarms().isEmpty())
    }

    @Test
    fun `저장하면 새 알람으로 저장하고 예약`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository()
        val viewModel = viewModel(repository)
        editing(viewModel)

        viewModel.onMediaSelected(ringtone)
        viewModel.onTimeChange(10, 0)
        viewModel.onSave()
        val effect = viewModel.effect.first() as AlarmEditEffect.Saved

        assertTrue(effect.isNew)
        assertEquals(clock.millis("2026-10-28T10:00"), repository.getAllAlarms().single().timeInMillis)
        assertEquals(1, alarmManager.scheduled.size)
    }

    @Test
    fun `메모를 지우면 읽어주기도 꺼짐`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val viewModel = viewModel(FakeAlarmRepository())
        editing(viewModel)

        viewModel.onMemoChange("물 마시기")
        viewModel.onMemoTtsChange(true)
        viewModel.onMemoChange("")

        assertFalse(editing(viewModel).draft.memoTtsEnabled)
    }

    @Test
    fun `삭제하면 지우고 예약을 취소`() = runTest {
        clock.setNow("2026-10-28T09:00")
        val repository = FakeAlarmRepository(testAlarm(clock.millis("2026-10-28T10:00"), id = 1))
        val viewModel = viewModel(repository, alarmId = 1)
        editing(viewModel)

        viewModel.onDelete()

        assertEquals(AlarmEditEffect.Deleted, viewModel.effect.first())
        assertTrue(repository.getAllAlarms().isEmpty())
        assertEquals(listOf(1L), alarmManager.cancelled.map { it.id })
    }
}
