package com.bluepig.alarm.manager.download

import androidx.media3.common.MediaItem
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import com.bluepig.alarm.domain.entity.file.SongFile

interface MediaDownloadManager {
    fun getDownloadManager(): DownloadManager
    fun getDownloadNotificationHelper(): DownloadNotificationHelper
    fun getDataSourceFactory(): DataSource.Factory
    fun startDownload(songFile: SongFile)
    fun removeDownload(songFiles: List<SongFile>)
    fun getMediaItem(songFile: SongFile): MediaItem
}