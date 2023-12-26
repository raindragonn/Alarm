package com.bluepig.alarm.ui.media.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.result.resultLoading
import com.bluepig.alarm.domain.usecase.SearchMusicInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSearchViewModel @Inject constructor(
    private val _searchMusicInfo: SearchMusicInfo,
) : ViewModel() {

    private val _musicInfoList: MutableStateFlow<Result<List<MusicInfo>>> =
        MutableStateFlow(Result.success(emptyList()))
    val musicInfoList = _musicInfoList.asStateFlow()

    fun search(query: String = "") {
        viewModelScope.launch {
            _musicInfoList.emit(resultLoading())
            _musicInfoList.emit(_searchMusicInfo.invoke(query))
        }
    }
}