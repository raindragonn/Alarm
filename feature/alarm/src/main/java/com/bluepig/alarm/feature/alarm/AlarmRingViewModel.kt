package com.bluepig.alarm.feature.alarm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.usecase.GetAlarmById
import com.bluepig.alarm.domain.usecase.GetCurrentTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface AlarmRingUiState {
    data object Loading : AlarmRingUiState

    /** 지워진 알람 → 화면을 닫음 */
    data object NotFound : AlarmRingUiState

    /** [minuteMillis]: 지금 시각(분 단위로 내림) */
    data class Ringing(val alarm: Alarm, val isPreview: Boolean, val minuteMillis: Long) : AlarmRingUiState
}

@HiltViewModel
class AlarmRingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getAlarmById: GetAlarmById,
    getCurrentTime: GetCurrentTime,
) : ViewModel() {
    private val _preview: Alarm? = savedStateHandle[KEY_PREVIEW_ALARM]
    private val _alarmId: Long = savedStateHandle[KEY_ALARM_ID] ?: -1

    private val _alarm = flow {
        emit(_preview ?: getAlarmById(_alarmId).getOrNull())
    }

    val uiState: StateFlow<AlarmRingUiState> =
        combine(_alarm, getCurrentTime().map { it / MINUTE * MINUTE }.distinctUntilChanged()) { alarm, minute ->
            if (alarm == null) AlarmRingUiState.NotFound else AlarmRingUiState.Ringing(alarm, _preview != null, minute)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AlarmRingUiState.Loading)

    companion object {
        /** 이름은 이전 AlarmActivity extra와 같음 (울림 서비스·미리보기가 씀) */
        const val KEY_ALARM_ID = "EXTRA_ALARM_ID"
        const val KEY_PREVIEW_ALARM = "EXTRA_IS_PREVIEW"
        private const val MINUTE = 60_000L
    }
}
