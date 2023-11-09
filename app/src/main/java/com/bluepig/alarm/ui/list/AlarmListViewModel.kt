package com.bluepig.alarm.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.result.BpResult
import com.bluepig.alarm.domain.result.resultOf
import com.bluepig.alarm.domain.usecase.GetAllAlarms
import com.bluepig.alarm.domain.usecase.SaveAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmListViewModel @Inject constructor(
    private val _getAllAlarms: GetAllAlarms,
    private val _saveAlarm: SaveAlarm
) : ViewModel() {

    private val _alarmList: MutableStateFlow<BpResult<List<Alarm>>> =
        MutableStateFlow(BpResult.Loading)
    val alarmList = _alarmList.asStateFlow()

    init {
        viewModelScope.launch {
            _getAllAlarms.invoke()
                .collect {
                    _alarmList.emit(resultOf { it })
                }
        }
    }

    suspend fun saveAlarm(alarm: Alarm) =
        _saveAlarm.invoke(alarm.copy(isActive = !alarm.isActive))

}