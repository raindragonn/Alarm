package com.bluepig.alarm.ui.search.select

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.file.File
import com.bluepig.alarm.domain.result.BpResult
import com.bluepig.alarm.domain.result.NotFoundArgumentException
import com.bluepig.alarm.domain.usecase.GetFileUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FileSelectViewModel @Inject constructor(
    private val _state: SavedStateHandle,
    private val _getFileUrl: GetFileUrl
) : ViewModel() {
    private val _file: File
        get() = _state.get<File>("file")
            ?: throw NotFoundArgumentException("file")
    private val _userAgent: String
        get() = _state.get<String>("userAgent")
            ?: throw NotFoundArgumentException("userAgent")

    private val _fileUrlState: MutableStateFlow<BpResult<Pair<String, String>>> =
        MutableStateFlow(BpResult.Loading)
    val fileUrlState = _fileUrlState.asStateFlow()

    init {
        getFileUrl()
    }

    private fun getFileUrl() =
        viewModelScope.launch {
            val result = _getFileUrl.invoke(
                _file,
                _userAgent
            )
            _fileUrlState.emit(result)
        }
}