package com.bluepig.alarm.feature.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.usecase.DismissOverlayNotice
import com.bluepig.alarm.domain.usecase.GetAllAlarms
import com.bluepig.alarm.domain.usecase.GetNextAlarm
import com.bluepig.alarm.domain.usecase.IsOverlayNoticeDismissed
import com.bluepig.alarm.domain.usecase.SaveAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AlarmListUiState {
    data object Loading : AlarmListUiState
    data object Empty : AlarmListUiState

    /** [next]가 null이면 모든 알람이 꺼져 있음 */
    data class Alarms(
        val alarms: List<Alarm>,
        val next: NextAlarmUi?,
        val overlayNotice: OverlayNotice? = null,
    ) : AlarmListUiState
}

/**
 * 다른 앱 위에 표시 권한 안내 (제품 0017). 권한이 있으면 화면이 숨김.
 * 켜진 유튜브 알람이 있으면 [YouTube](닫기 없음), 그 밖의 켜진 알람만 있으면 [General](닫으면 다시 안 띄움)
 */
enum class OverlayNotice { YouTube, General }

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
    isOverlayNoticeDismissed: IsOverlayNoticeDismissed,
    private val _dismissOverlayNotice: DismissOverlayNotice,
) : ViewModel() {
    // 읽기 전(null)에는 일반 안내를 띄우지 않음 (닫은 사람에게 잠깐 보이지 않게)
    private val _noticeDismissed = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<AlarmListUiState> =
        combine(getAllAlarms(), getNextAlarm(), _noticeDismissed) { alarms, next, dismissed ->
            if (alarms.isEmpty()) {
                AlarmListUiState.Empty
            } else {
                AlarmListUiState.Alarms(
                    alarms,
                    next?.let { NextAlarmUi(it.timeInMillis, (it.remainingMillis + MINUTE - 1) / MINUTE) },
                    overlayNotice(alarms.filter { it.isActive }, dismissed),
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AlarmListUiState.Loading)

    private val _effect = Channel<AlarmListEffect>(Channel.BUFFERED)
    val effect: Flow<AlarmListEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            val dismissed = isOverlayNoticeDismissed()
            _noticeDismissed.update { it ?: dismissed }
        }
    }

    fun onDismissOverlayNotice() {
        _noticeDismissed.value = true
        viewModelScope.launch { _dismissOverlayNotice() }
    }

    fun onActiveChange(alarm: Alarm, isActive: Boolean) {
        viewModelScope.launch {
            _saveAlarm(alarm.copy(isActive = isActive))
                .onSuccess { _effect.send(AlarmListEffect.AlarmSaved(it)) }
                .onFailure { _effect.send(AlarmListEffect.SaveFailed) }
        }
    }

    private fun overlayNotice(active: List<Alarm>, dismissed: Boolean?): OverlayNotice? = when {
        active.any { it.media is TubeMedia } -> OverlayNotice.YouTube
        active.isNotEmpty() && dismissed == false -> OverlayNotice.General
        else -> null
    }

    private companion object {
        const val MINUTE = 60_000L
    }
}
