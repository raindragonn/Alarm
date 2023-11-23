package com.bluepig.alarm.ui.edit

import android.media.AudioManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.file.File
import com.bluepig.alarm.domain.result.BpResult
import com.bluepig.alarm.domain.result.NotFoundArgumentException
import com.bluepig.alarm.domain.usecase.SaveAlarm
import com.bluepig.alarm.domain.util.CalendarHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AlarmEditViewModel @Inject constructor(
    private val _state: SavedStateHandle,
    private val _saveAlarm: SaveAlarm,
    audioManager: AudioManager
) : ViewModel() {

    val file: File
        get() = _state.get<File>("file")
            ?: _state.get<Alarm>("alarm")?.file
            ?: throw NotFoundArgumentException("file")
    val alarm: Alarm?
        get() = _state.get<Alarm>("alarm")

    private val _timeInMillis =
        MutableStateFlow(
            alarm?.timeInMillis ?: CalendarHelper.now.timeInMillis
        )
    val timeInMillis
        get() = _timeInMillis.asStateFlow()

    private val _repeatWeek =
        MutableStateFlow(alarm?.repeatWeek ?: setOf())
    val repeatWeek
        get() = _repeatWeek.asStateFlow()

    private val _volume =
        MutableStateFlow(
            alarm?.volume ?: audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        )
    val volume
        get() = _volume.asStateFlow()

    private val _vibration =
        MutableStateFlow(alarm?.hasVibration ?: true)
    val vibration
        get() = _vibration.asStateFlow()

    private val _memo = MutableStateFlow(alarm?.memo ?: "")
    val memo
        get() = _memo.asStateFlow()

    fun setTimeInMillis(hourOfDay: Int, minute: Int) {
        _timeInMillis.value = CalendarHelper.fromHourAndMinute(hourOfDay, minute).timeInMillis
    }

    fun setRepeatWeek(week: Week) {
        val before = _repeatWeek.value.toMutableSet()
        if (before.contains(week)) {
            before.remove(week)
        } else {
            before.add(week)
        }
        _repeatWeek.value = before

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
        val saveAlarm = Alarm(
            id = alarm?.id,
            timeInMillis = _timeInMillis.value,
            file = file,
            repeatWeek = _repeatWeek.value,
            volume = _volume.value,
            hasVibration = _vibration.value,
            memo = _memo.value
        )
        Timber.d(saveAlarm.toString())

        return _saveAlarm.invoke(saveAlarm)
    }
}