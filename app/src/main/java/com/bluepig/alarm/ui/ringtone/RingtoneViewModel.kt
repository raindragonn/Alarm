package com.bluepig.alarm.ui.ringtone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.media.RingtoneMedia
import com.bluepig.alarm.domain.result.resultLoading
import com.bluepig.alarm.domain.usecase.GetRingtones
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RingtoneViewModel @Inject constructor(
    private val _getRingtones: GetRingtones
) : ViewModel() {

    private val _ringtonesState = MutableStateFlow<Result<List<RingtoneMedia>>>(resultLoading())
    val ringtonesState
        get() = _ringtonesState.asStateFlow()

    init {
        viewModelScope.launch {
            _ringtonesState.emit(_getRingtones.invoke().onSuccess {
                Timber.d("size = ${it.size}")
            })
        }
    }
}