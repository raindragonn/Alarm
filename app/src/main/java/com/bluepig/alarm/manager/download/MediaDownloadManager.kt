package com.bluepig.alarm.manager.download

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import com.bluepig.alarm.domain.entity.alarm.media.MusicMedia

@OptIn(UnstableApi::class)
interface MediaDownloadManager {
    fun getDownloadManager(): DownloadManager
    fun getDownloadNotificationHelper(): DownloadNotificationHelper
    fun getDataSourceFactory(): DataSource.Factory
    fun startDownload(musicMedia: MusicMedia)
    fun removeDownload(musicMedias: List<MusicMedia>)
    fun getMediaItem(musicMedia: MusicMedia): MediaItem
}