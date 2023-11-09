package com.bluepig.alarm.ui.edit

import androidx.lifecycle.ViewModel
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.usecase.SaveAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmEditViewModel @Inject constructor(
    private val _saveAlarm: SaveAlarm
) : ViewModel() {
    //TODO("편집 시 alarm 정보는 savedStateHandle을 통해 가져올 것")

    suspend fun saveAlarm(alarm: Alarm) = _saveAlarm.invoke(alarm)

}