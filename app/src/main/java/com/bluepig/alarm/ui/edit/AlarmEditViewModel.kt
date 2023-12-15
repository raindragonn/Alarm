package com.bluepig.alarm.ui.edit

import android.media.AudioManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.entity.alarm.Week
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.result.NotFoundAlarmException
import com.bluepig.alarm.domain.result.NotSelectAlarmMedia
import com.bluepig.alarm.domain.usecase.GetExpiredTime
import com.bluepig.alarm.domain.usecase.RemoveAlarm
import com.bluepig.alarm.domain.usecase.SaveAlarm
import com.bluepig.alarm.domain.util.CalendarHelper
import com.bluepig.alarm.manager.timeguide.TimeGuideManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class AlarmEditViewModel @Inject constructor(
    private val _state: SavedStateHandle,
    private val _saveAlarm: SaveAlarm,
    private val _removeAlarm: RemoveAlarm,
    private val _getExpiredTime: GetExpiredTime,
    private val _timeGuideManager: TimeGuideManager,
    audioManager: AudioManager
) : ViewModel() {

    private val _alarm: Alarm?
        get() = _state.get<Alarm>("alarm")

    val isEdit: Boolean
        get() = _alarm != null

    private val _timeInMillis =
        MutableStateFlow(
            _alarm?.timeInMillis ?: CalendarHelper.now.timeInMillis
        )
    val timeInMillis
        get() = _timeInMillis.asStateFlow()

    private val _repeatWeek =
        MutableStateFlow(_alarm?.repeatWeek ?: setOf())
    val repeatWeek
        get() = _repeatWeek.asStateFlow()

    private val _volume =
        MutableStateFlow(
            _alarm?.volume ?: audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        )
    val volume
        get() = _volume.asStateFlow()

    private val _vibration =
        MutableStateFlow(_alarm?.hasVibration ?: true)
    val vibration
        get() = _vibration.asStateFlow()

    private val _memo = MutableStateFlow(_alarm?.memo ?: "")
    val memo
        get() = _memo.asStateFlow()

    private val _alarmMedia = MutableStateFlow(_alarm?.media)
    val alarmMedia
        get() = _alarmMedia.asStateFlow()


    fun setTimeInMillis(hourOfDay: Int, minute: Int) {
        _timeInMillis.value = CalendarHelper.todayFromHourAndMinute(hourOfDay, minute).timeInMillis
    }

    fun getExpiredTime() = flow {
        while (true) {
            val calendar = CalendarHelper.fromTimeInMillis(_timeInMillis.value)
            val repeatWeek = _repeatWeek.value

            val expiredTime = _getExpiredTime.invoke(
                calendar,
                repeatWeek
            )
            val result = _timeGuideManager.getRemainingTimeGuide(expiredTime)
            emit(result)
            delay(100L)
        }
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

    fun setAlarmMedia(alarmMedia: AlarmMedia) {
        _alarmMedia.value = alarmMedia
    }

    fun getEditingAlarm(): Alarm? {
        return Alarm(
            id = _alarm?.id,
            timeInMillis = _timeInMillis.value,
            media = _alarmMedia.value ?: return null,
            repeatWeek = _repeatWeek.value,
            volume = _volume.value,
            hasVibration = _vibration.value,
            memo = _memo.value
        )
    }

    suspend fun saveAlarm(): Result<Alarm> {
        return kotlin.runCatching {
            val editingAlarm = getEditingAlarm() ?: throw NotSelectAlarmMedia
            _saveAlarm.invoke(editingAlarm).getOrThrow()
        }
    }

    suspend fun removeAlarm(): Result<Unit> {
        return kotlin.runCatching {
            val alarm = _alarm ?: throw NotFoundAlarmException
            _removeAlarm.invoke(alarm).getOrThrow()
        }
    }
}