package com.bluepig.alarm.manager.download

import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper

interface MediaDownloadManager {
    fun getDownloadManager(): DownloadManager
    fun getDownloadNotificationHelper(): DownloadNotificationHelper
    fun getDataSourceFactory(): DataSource.Factory
}