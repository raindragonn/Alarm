package com.bluepig.alarm.ui.edit

import android.media.AudioManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.Weak
import com.bluepig.alarm.domain.entity.file.File
import com.bluepig.alarm.domain.result.BpResult
import com.bluepig.alarm.domain.result.NotFoundArgumentException
import com.bluepig.alarm.domain.usecase.SaveAlarm
import com.bluepig.alarm.domain.util.CalendarHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AlarmEditViewModel @Inject constructor(
    private val _state: SavedStateHandle,
    private val _saveAlarm: SaveAlarm,
    audioManager: AudioManager
) : ViewModel() {
    val file: File
        get() = _state.get<File>("file") ?: throw NotFoundArgumentException("file")

    private val _timeInMillis = MutableStateFlow(CalendarHelper.now)
    val timeInMillis
        get() = _timeInMillis.asStateFlow()

    private val _repeatWeak = MutableStateFlow(setOf<Weak>())
    val repeatWeak
        get() = _repeatWeak.asStateFlow()

    private val _volume =
        MutableStateFlow(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
    val volume
        get() = _volume.asStateFlow()

    private val _vibration = MutableStateFlow(true)
    val vibration
        get() = _vibration.asStateFlow()

    private val _memo = MutableStateFlow("")
    val memo
        get() = _memo.asStateFlow()

    fun setTimeInMillis(hourOfDay: Int, minute: Int) {
        _timeInMillis.value = CalendarHelper.fromHourAndMinute(hourOfDay, minute)
    }

    fun setRepeatWeak(weak: Weak) {
        val before = _repeatWeak.value.toMutableSet()
        if (before.contains(weak)) {
            before.remove(weak)
        } else {
            before.add(weak)
        }
        _repeatWeak.value = before

    }

    fun setVolume(volume: Int) {
        _volume.value = volume
    }

    fun setVibration(vibration: Boolean) {
        _vibration.value = vibration
    }

    fun setMemo(memo: String) {
        _memo.value = memo
    }


    suspend fun saveAlarm(): BpResult<Alarm> {
        val alarm = Alarm(
            timeInMillis = _timeInMillis.value.timeInMillis,
            file = file,
            repeatWeak = _repeatWeak.value,
            volume = _volume.value,
            hasVibration = _vibration.value,
            memo = _memo.value
        )

        return _saveAlarm.invoke(alarm)
    }
}