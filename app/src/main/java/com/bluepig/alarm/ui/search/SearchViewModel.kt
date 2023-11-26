package com.bluepig.alarm.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.file.BasicFile
import com.bluepig.alarm.domain.result.BpResult
import com.bluepig.alarm.domain.usecase.SearchFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val _searchFile: SearchFile,
) : ViewModel() {

    private val _fileList: MutableStateFlow<BpResult<List<BasicFile>>> =
        MutableStateFlow(BpResult.Success(emptyList()))
    val fileList = _fileList.asStateFlow()

    fun search(query: String = "") {
        viewModelScope.launch {
            _fileList.emit(BpResult.Loading)
            _fileList.emit(_searchFile.invoke(query))
        }
    }
}