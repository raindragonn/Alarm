package com.bluepig.alarm.ui.list

import androidx.lifecycle.ViewModel
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.usecase.GetAllAlarms
import com.bluepig.alarm.domain.usecase.GetExpiredAlarmTime
import com.bluepig.alarm.domain.usecase.SaveAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmListViewModel @Inject constructor(
    private val _getAllAlarms: GetAllAlarms,
    private val _getExpiredAlarmTime: GetExpiredAlarmTime,
    private val _saveAlarm: SaveAlarm,
) : ViewModel() {

    fun getAllAlarmsFlow() = _getAllAlarms.invoke()
    fun getExpiredAlarmTime() = _getExpiredAlarmTime.invoke()

    suspend fun alarmActiveSwitching(alarm: Alarm) =
        _saveAlarm.invoke(alarm.copy(isActive = !alarm.isActive))

}