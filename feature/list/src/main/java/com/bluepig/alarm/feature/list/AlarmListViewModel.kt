package com.bluepig.alarm.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.usecase.GetAllAlarms
import com.bluepig.alarm.domain.usecase.GetNextAlarm
import com.bluepig.alarm.domain.usecase.SaveAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AlarmListUiState {
    data object Loading : AlarmListUiState
    data object Empty : AlarmListUiState

    /** [next]가 null이면 모든 알람이 꺼져 있음 */
    data class Alarms(val alarms: List<Alarm>, val next: NextAlarmUi?) : AlarmListUiState
}

/** 화면은 분 단위라 분이 바뀔 때만 상태가 바뀌게 남은 시간을 분으로 올림 */
data class NextAlarmUi(val timeInMillis: Long, val remainingMinutes: Long)

sealed interface AlarmListEffect {
    data class AlarmSaved(val alarm: Alarm) : AlarmListEffect
    data object SaveFailed : AlarmListEffect
}

@HiltViewModel
class AlarmListViewModel @Inject constructor(
    getAllAlarms: GetAllAlarms,
    getNextAlarm: GetNextAlarm,
    private val _saveAlarm: SaveAlarm,
) : ViewModel() {
    val uiState: StateFlow<AlarmListUiState> =
        combine(getAllAlarms(), getNextAlarm()) { alarms, next ->
            if (alarms.isEmpty()) {
                AlarmListUiState.Empty
            } else {
                AlarmListUiState.Alarms(
                    alarms,
                    next?.let { NextAlarmUi(it.timeInMillis, (it.remainingMillis + MINUTE - 1) / MINUTE) },
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AlarmListUiState.Loading)

    private val _effect = Channel<AlarmListEffect>(Channel.BUFFERED)
    val effect: Flow<AlarmListEffect> = _effect.receiveAsFlow()

    fun onActiveChange(alarm: Alarm, isActive: Boolean) {
        viewModelScope.launch {
            _saveAlarm(alarm.copy(isActive = isActive))
                .onSuccess { _effect.send(AlarmListEffect.AlarmSaved(it)) }
                .onFailure { _effect.send(AlarmListEffect.SaveFailed) }
        }
    }

    private companion object {
        const val MINUTE = 60_000L
    }
}
