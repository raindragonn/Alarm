package com.bluepig.alarm.ui.media.select

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.media.AlarmMedia
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.result.NotFoundArgumentException
import com.bluepig.alarm.domain.result.resultLoading
import com.bluepig.alarm.domain.usecase.GetMusicMedia
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaSelectViewModel @Inject constructor(
    private val _state: SavedStateHandle,
    private val _getMusicMedia: GetMusicMedia
) : ViewModel() {

    private val _musicInfo: Result<MusicInfo>
        get() = kotlin.runCatching {
            _state.get<MusicInfo>(MediaSelectBottomSheetDialogFragment.KEY_ARGS_MUSIC_INFO)
                ?: throw NotFoundArgumentException(MediaSelectBottomSheetDialogFragment.KEY_ARGS_MUSIC_INFO)
        }

    private val _alarmMedia: Result<AlarmMedia>
        get() = kotlin.runCatching {
            _state.get<AlarmMedia>(MediaSelectBottomSheetDialogFragment.KEY_ARGS_ALARM_MEDIA)
                ?: throw NotFoundArgumentException(MediaSelectBottomSheetDialogFragment.KEY_ARGS_ALARM_MEDIA)
        }

    private val _musicMedia: MutableStateFlow<Result<AlarmMedia>> =
        MutableStateFlow(resultLoading())
    val musicMedia
        get() = _musicMedia.asStateFlow()

    init {
        viewModelScope.launch {
            _musicInfo.onSuccess { musicInfo ->
                val result = _getMusicMedia.invoke(
                    musicInfo,
                )
                _musicMedia.emit(result)
            }

            _alarmMedia.onSuccess { alarmMedia ->
                _musicMedia.emit(Result.success(alarmMedia))
            }
        }
    }
}