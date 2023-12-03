package com.bluepig.alarm.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.usecase.GetAllAlarms
import com.bluepig.alarm.manager.download.MediaDownloadManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val _getAllAlarms: GetAllAlarms,
    private val _mediaDownloadManager: MediaDownloadManager
) : ViewModel() {
    fun startDownload() = viewModelScope.launch {
        _getAllAlarms.invoke()
            .stateIn(this)
            .collect { list ->
                startDownloadFiles(list)
                removeDownloadFiles(list)
            }
    }

    private fun startDownloadFiles(list: List<Alarm>) {
        list.filter(Alarm::isActive)
            .forEach {
                _mediaDownloadManager.startDownload(it.file)
            }
    }

    private fun removeDownloadFiles(list: List<Alarm>) {
        val files = list.map { it.file }
        _mediaDownloadManager.removeDownload(files)
    }
}