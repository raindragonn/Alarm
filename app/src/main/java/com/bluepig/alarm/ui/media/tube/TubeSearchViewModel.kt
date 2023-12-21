package com.bluepig.alarm.ui.media.tube

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.result.resultLoading
import com.bluepig.alarm.domain.usecase.GetTubeMediaList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TubeSearchViewModel @Inject constructor(
    private val _getTubeMediaList: GetTubeMediaList,
) : ViewModel() {

    private val _tubeList: MutableStateFlow<Result<List<TubeMedia>>> =
        MutableStateFlow(Result.success(emptyList()))
    val tubeList = _tubeList.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            _tubeList.emit(resultLoading())
            _tubeList.emit(_getTubeMediaList.invoke(query))
        }
    }
}