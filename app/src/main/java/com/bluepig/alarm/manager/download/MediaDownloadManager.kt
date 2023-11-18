package com.bluepig.alarm.manager.download

import androidx.media3.exoplayer.offline.DownloadManager

interface MediaDownloadManager {
    fun getDownloadManager(): DownloadManager

}