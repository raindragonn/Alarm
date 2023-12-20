package com.bluepig.alarm.ui.music

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.music.MusicInfo
import com.bluepig.alarm.domain.result.resultLoading
import com.bluepig.alarm.domain.usecase.SearchFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSearchViewModel @Inject constructor(
    private val _searchFile: SearchFile,
) : ViewModel() {

    private val _fileList: MutableStateFlow<Result<List<MusicInfo>>> =
        MutableStateFlow(Result.success(emptyList()))
    val fileList = _fileList.asStateFlow()

    fun search(query: String = "") {
        viewModelScope.launch {
            _fileList.emit(resultLoading())
            _fileList.emit(_searchFile.invoke(query))
        }
    }
}