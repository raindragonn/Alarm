package com.bluepig.alarm.ui.alarm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.result.NotFoundPreViewAlarmException
import com.bluepig.alarm.domain.usecase.GetAlarmById
import com.bluepig.alarm.domain.usecase.GetCurrentTime
import com.bluepig.alarm.domain.usecase.SaveAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val _state: SavedStateHandle,
    private val _getAlarmById: GetAlarmById,
    private val _saveAlarm: SaveAlarm,
    private val _getCurrentTime: GetCurrentTime,
) : ViewModel() {

    private val _previewAlarm
        get() = kotlin.runCatching {
            _state.get<Alarm>(AlarmActivity.EXTRA_PREVIEW_ALARM)
                ?: throw NotFoundPreViewAlarmException
        }

    private val _alarmId
        get() = kotlin.runCatching {
            _state.get<Long>(AlarmActivity.EXTRA_ALARM_ID) ?: throw NullPointerException()
        }

    val isPreview
        get() = _previewAlarm.isSuccess

    private val _volume = MutableStateFlow(0)
    val volume
        get() = _volume.asStateFlow()

    private var _defaultVolume: Int? = null

    fun setDefaultVolume(volume: Int) {
        _defaultVolume = volume
    }

    fun getDefaultVolume() = _defaultVolume ?: 7

    fun getDateTime() = _getCurrentTime.invoke()

    fun getAlarmState() = flow {
        _alarmId.onSuccess { id ->
            val result = _getAlarmById.invoke(id)
            emit(result)
        }
        _previewAlarm.onSuccess {
            emit(_previewAlarm)
        }
    }

    fun setAlarmVolume(volume: Int) = viewModelScope.launch {
        _volume.emit(volume)
    }

    fun startAutoIncreaseVolume(maxVolume: Int, duration: Int = MAX_DURATION) =
        viewModelScope.launch {
            var volume = 0f
            var currentDuration = 0
            val addedVolume = (maxVolume / duration.toFloat())
            while (currentDuration < duration) {
                volume += addedVolume
                _volume.emit(volume.toInt())
                currentDuration++
                delay(1000L)
            }
        }

    fun updateAlarmExpired() = viewModelScope.launch {
        if (isPreview) return@launch
        val id = _alarmId.getOrNull() ?: return@launch
        val alarm = _getAlarmById.invoke(id).getOrNull() ?: return@launch

        _saveAlarm.invoke(alarm.getActiveCheckedAlarm())
    }

    companion object {
        private const val MAX_DURATION = 15
    }
}