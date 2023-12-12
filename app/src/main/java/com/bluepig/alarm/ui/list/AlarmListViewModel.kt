package com.bluepig.alarm.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.result.resultLoading
import com.bluepig.alarm.domain.usecase.GetAllAlarms
import com.bluepig.alarm.domain.usecase.GetExpiredAlarmTime
import com.bluepig.alarm.domain.usecase.RefreshAlarms
import com.bluepig.alarm.domain.usecase.SaveAlarm
import com.bluepig.alarm.manager.timeguide.TimeGuideManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmListViewModel @Inject constructor(
    private val _getAllAlarms: GetAllAlarms,
    private val _getExpiredAlarmTime: GetExpiredAlarmTime,
    private val _saveAlarm: SaveAlarm,
    private val _timeGuideManager: TimeGuideManager,
    private val _refreshAlarms: RefreshAlarms,
) : ViewModel() {

    private val _alarmList: MutableStateFlow<Result<List<Alarm>>> =
        MutableStateFlow(resultLoading())
    val alarmList = _alarmList.asStateFlow()

    private val _expireTime: MutableStateFlow<Result<String>> =
        MutableStateFlow(resultLoading())
    val expireTime = _expireTime.asStateFlow()

    init {
        viewModelScope.launch {
            _getAllAlarms.invoke()
                .collect {
                    _alarmList.emit(kotlin.runCatching { it })
                }
        }
        viewModelScope.launch {
            _getExpiredAlarmTime.invoke()
                .collect {
                    val result = kotlin.runCatching {
                        _timeGuideManager.getRemainingTimeGuide(it.getOrThrow())
                    }
                    _expireTime.emit(result)
                }
        }
    }

    suspend fun alarmActiveSwitching(alarm: Alarm) =
        _saveAlarm.invoke(alarm.copy(isActive = !alarm.isActive))

    fun refresh() = viewModelScope.launch {
        _refreshAlarms.invoke()
    }
}