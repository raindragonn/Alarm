package com.bluepig.alarm.ui.alarm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.result.BpResult
import com.bluepig.alarm.domain.result.NotFoundPreViewAlarmException
import com.bluepig.alarm.domain.result.asyncSuccess
import com.bluepig.alarm.domain.result.isSuccess
import com.bluepig.alarm.domain.result.resultOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val _state: SavedStateHandle,
) : ViewModel() {

    private val _previewAlarm
        get() = resultOf {
            _state.get<Alarm>(AlarmActivity.EXTRA_PREVIEW_ALARM)
                ?: throw NotFoundPreViewAlarmException
        }

    private val _alarmId
        get() = resultOf {
            _state.get<Long>(AlarmActivity.EXTRA_ALARM_ALARM_ID) ?: throw NullPointerException()
        }

    val isPreview
        get() = _previewAlarm.isSuccess()

    private val _alarmState = MutableStateFlow<BpResult<Alarm>>(BpResult.Loading)
    val alarmState
        get() = _alarmState.asStateFlow()

    private val _volumeIncreaseState = MutableStateFlow(0)
    val volumeIncreaseState
        get() = _volumeIncreaseState.asStateFlow()

    fun setAlarmState() = viewModelScope.launch {
        _alarmId.asyncSuccess {

        }
        _previewAlarm.asyncSuccess {
            _alarmState.emit(_previewAlarm)
        }
    }

    fun startAutoIncreaseVolume(maxVolume: Int, duration: Int = MAX_DURATION) =
        viewModelScope.launch {
            var volume = 0f
            var currentDuration = 0
            val addedVolume = (maxVolume / duration.toFloat())
            while (currentDuration < duration) {
                volume += addedVolume
                _volumeIncreaseState.emit(volume.toInt())
                currentDuration++
                delay(1000L)
            }
        }

    companion object {
        private const val MAX_DURATION = 15
    }
}