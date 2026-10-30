package com.bluepig.alarm.ui.media.tube

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.media.TubeMedia
import com.bluepig.alarm.domain.result.resultInit
import com.bluepig.alarm.domain.result.resultLoading
import com.bluepig.alarm.domain.usecase.GetAppConfig
import com.bluepig.alarm.domain.usecase.GetTubeAccount
import com.bluepig.alarm.domain.usecase.GetTubeMediaList
import com.bluepig.alarm.domain.usecase.SaveTubeAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TubeSearchViewModel @Inject constructor(
    private val _getTubeMediaList: GetTubeMediaList,
    private val _getTubeAccount: GetTubeAccount,
    private val _saveTubeAccount: SaveTubeAccount,
    private val _getAppConfig: GetAppConfig,
) : ViewModel() {

    private val _tubeList: MutableStateFlow<Result<List<TubeMedia>>> =
        MutableStateFlow(resultInit())
    val tubeList = _tubeList.asStateFlow()

    fun search(query: String) {
        viewModelScope.launch {
            _tubeList.emit(resultLoading())
            _tubeList.emit(_getTubeMediaList.invoke(query))
        }
    }

    suspend fun isOnlyLinkSearch() = _getAppConfig().showOnlyYoutubeLink

    suspend fun getSavedSelectName() = _getTubeAccount()

    suspend fun setSavedSelectName(name: String) = _saveTubeAccount(name)
}
