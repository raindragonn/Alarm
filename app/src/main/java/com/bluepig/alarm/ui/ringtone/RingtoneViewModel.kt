package com.bluepig.alarm.ui.ringtone

import androidx.lifecycle.ViewModel
import com.bluepig.alarm.domain.usecase.GetRingtones
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RingtoneViewModel @Inject constructor(
    private val _getRingtones: GetRingtones
) : ViewModel() {

    suspend fun getRingtoneAlarm() = _getRingtones.invoke()
}