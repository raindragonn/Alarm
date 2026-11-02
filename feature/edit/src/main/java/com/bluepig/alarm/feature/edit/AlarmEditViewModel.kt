package com.bluepig.alarm.feature.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.usecase.CanScheduleExactAlarms
import com.bluepig.alarm.domain.usecase.GetAlarmById
import com.bluepig.alarm.domain.usecase.GetCurrentTime
import com.bluepig.alarm.domain.usecase.GetExpiredTime
import com.bluepig.alarm.domain.usecase.GetMaxVolume
import com.bluepig.alarm.domain.usecase.RemoveAlarm
import com.bluepig.alarm.domain.usecase.SaveAlarm
import com.bluepig.alarm.domain.util.CalendarHelper
import com.bluepig.alarm.domain.util.hourOfDay
import com.bluepig.alarm.domain.util.minute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 편집 중인 알람. 알람음을 고르기 전에는 [media]가 null */
data class AlarmDraft(
    val id: Long?,
    val hourOfDay: Int,
    val minute: Int,
    val repeatWeek: Set<Week>,
    val media: AlarmMedia?,
    val volume: Int,
    val isVolumeAutoIncrease: Boolean,
    val hasVibration: Boolean,
    val memo: String,
    val memoTtsEnabled: Boolean,
) {
    val isNew get() = id == null

    fun toAlarm(): Alarm? = media?.let {
        Alarm(
            id = id,
            timeInMillis = CalendarHelper.todayFromHourAndMinute(hourOfDay, minute).timeInMillis,
            media = it,
            repeatWeek = repeatWeek,
            volume = volume,
            isVolumeAutoIncrease = isVolumeAutoIncrease,
            hasVibration = hasVibration,
            memo = memo,
            memoTtsEnabled = memoTtsEnabled && memo.isNotBlank(),
        )
    }
}

sealed interface AlarmEditUiState {
    data object Loading : AlarmEditUiState

    /** [remainingMinutes]: 지금 저장하면 울리기까지 남은 시간(분 올림) */
    data class Editing(val draft: AlarmDraft, val maxVolume: Int, val remainingMinutes: Long) : AlarmEditUiState
}

sealed interface AlarmEditEffect {
    data class Saved(val alarm: Alarm, val isNew: Boolean) : AlarmEditEffect
    data object Deleted : AlarmEditEffect
    data class Preview(val alarm: Alarm) : AlarmEditEffect
    data object MediaRequired : AlarmEditEffect
    data object SaveFailed : AlarmEditEffect

    /** API 31·32에서 정확한 알람 권한이 꺼져 있어 저장하지 않음 (C6, 기술 0025) */
    data object ExactAlarmPermissionRequired : AlarmEditEffect

    /** 열려는 알람이 지워졌음 */
    data object NotFound : AlarmEditEffect
}

@HiltViewModel
class AlarmEditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getAlarmById: GetAlarmById,
    getCurrentTime: GetCurrentTime,
    getMaxVolume: GetMaxVolume,
    private val _getExpiredTime: GetExpiredTime,
    private val _saveAlarm: SaveAlarm,
    private val _removeAlarm: RemoveAlarm,
    private val _canScheduleExactAlarms: CanScheduleExactAlarms,
) : ViewModel() {
    private val _maxVolume = getMaxVolume()
    private val _draft = MutableStateFlow<AlarmDraft?>(null)

    private val _effect = Channel<AlarmEditEffect>(Channel.BUFFERED)
    val effect: Flow<AlarmEditEffect> = _effect.receiveAsFlow()

    val uiState: StateFlow<AlarmEditUiState> =
        combine(_draft.filterNotNull(), getCurrentTime()) { draft, _ ->
            val calendar = CalendarHelper.todayFromHourAndMinute(draft.hourOfDay, draft.minute)
            val remaining = _getExpiredTime(calendar, draft.repeatWeek)
            AlarmEditUiState.Editing(draft, _maxVolume, (remaining + MINUTE - 1) / MINUTE)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AlarmEditUiState.Loading)

    private var _job: Job? = null

    init {
        val id = savedStateHandle.get<Long>(KEY_ALARM_ID)?.takeIf { it >= 0 }
        viewModelScope.launch {
            if (id == null) {
                _draft.value = newDraft()
            } else {
                getAlarmById(id)
                    .onSuccess { _draft.value = it.toDraft() }
                    .onFailure { _effect.send(AlarmEditEffect.NotFound) }
            }
        }
    }

    fun onTimeChange(hourOfDay: Int, minute: Int) = edit { copy(hourOfDay = hourOfDay, minute = minute) }

    fun onDayToggle(week: Week) = edit {
        copy(repeatWeek = if (week in repeatWeek) repeatWeek - week else repeatWeek + week)
    }

    fun onVolumeChange(volume: Int) = edit { copy(volume = volume) }

    fun onVolumeAutoIncreaseChange(enabled: Boolean) = edit { copy(isVolumeAutoIncrease = enabled) }

    fun onVibrationChange(enabled: Boolean) = edit { copy(hasVibration = enabled) }

    /** 메모를 지우면 읽어주기도 끔 (제품 0005) */
    fun onMemoChange(memo: String) = edit { copy(memo = memo, memoTtsEnabled = memoTtsEnabled && memo.isNotBlank()) }

    fun onMemoTtsChange(enabled: Boolean) = edit { copy(memoTtsEnabled = enabled && memo.isNotBlank()) }

    fun onMediaSelected(media: AlarmMedia) = edit { copy(media = media) }

    fun onPreview() {
        val alarm = _draft.value?.toAlarm()
        viewModelScope.launch {
            _effect.send(if (alarm == null) AlarmEditEffect.MediaRequired else AlarmEditEffect.Preview(alarm))
        }
    }

    fun onSave() = runOnce {
        val draft = _draft.value ?: return@runOnce
        val alarm = draft.toAlarm() ?: return@runOnce _effect.send(AlarmEditEffect.MediaRequired)
        if (!_canScheduleExactAlarms()) return@runOnce _effect.send(AlarmEditEffect.ExactAlarmPermissionRequired)
        _saveAlarm(alarm)
            .onSuccess { _effect.send(AlarmEditEffect.Saved(it, draft.isNew)) }
            .onFailure { _effect.send(AlarmEditEffect.SaveFailed) }
    }

    fun onDelete() = runOnce {
        val alarm = _draft.value?.takeIf { !it.isNew }?.toAlarm() ?: return@runOnce
        _removeAlarm(alarm).onSuccess { _effect.send(AlarmEditEffect.Deleted) }
    }

    private fun edit(block: AlarmDraft.() -> AlarmDraft) = _draft.update { it?.block() }

    // 저장·삭제 연타로 두 번 실행되지 않게
    private fun runOnce(block: suspend () -> Unit) {
        if (_job?.isActive == true) return
        _job = viewModelScope.launch { block() }
    }

    private fun newDraft(): AlarmDraft {
        val now = CalendarHelper.now
        return AlarmDraft(
            id = null,
            hourOfDay = now.hourOfDay,
            minute = now.minute,
            repeatWeek = emptySet(),
            media = null,
            volume = _maxVolume,
            isVolumeAutoIncrease = true,
            hasVibration = true,
            memo = "",
            memoTtsEnabled = false,
        )
    }

    private fun Alarm.toDraft(): AlarmDraft {
        val calendar = getCalendar()
        return AlarmDraft(
            id = id,
            hourOfDay = calendar.hourOfDay,
            minute = calendar.minute,
            repeatWeek = repeatWeek,
            media = media,
            volume = volume,
            isVolumeAutoIncrease = isVolumeAutoIncrease,
            hasVibration = hasVibration,
            memo = memo,
            memoTtsEnabled = memoTtsEnabled,
        )
    }

    companion object {
        /** 새 알람은 -1 */
        const val KEY_ALARM_ID = "alarmId"
        private const val MINUTE = 60_000L
    }
}
