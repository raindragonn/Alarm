package com.bluepig.alarm.ui.search.select

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.file.BasicFile
import com.bluepig.alarm.domain.entity.file.SongFile
import com.bluepig.alarm.domain.result.BpResult
import com.bluepig.alarm.domain.result.NotFoundArgumentException
import com.bluepig.alarm.domain.usecase.GetSongFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileSelectViewModel @Inject constructor(
    private val _state: SavedStateHandle,
    private val _getSongFile: GetSongFile
) : ViewModel() {
    private val _basicFile: BasicFile
        get() = _state.get<BasicFile>("basicFile")
            ?: throw NotFoundArgumentException("basicFile")

    private val _songFile: MutableStateFlow<BpResult<SongFile>> =
        MutableStateFlow(BpResult.Loading)
    val songFile = _songFile.asStateFlow()

    fun getFileUrl(userAgent: String) =
        viewModelScope.launch {
            val result = _getSongFile.invoke(
                _basicFile,
                userAgent
            )
            _songFile.emit(result)
        }
}