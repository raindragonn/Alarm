package com.bluepig.alarm.service

import android.app.Notification
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Scheduler
import com.bluepig.alarm.R
import com.bluepig.alarm.manager.download.MediaDownloadManager
import com.bluepig.alarm.notification.NotificationType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MediaDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    NotificationType.DOWNLOAD_NOTIFICATION.channelId,
    NotificationType.DOWNLOAD_NOTIFICATION.channelNameResource,
    NotificationType.DOWNLOAD_NOTIFICATION.channelDescriptionResource,
) {
    companion object {
        const val FOREGROUND_NOTIFICATION_ID = 1
        const val JOB_ID = 1
    }

    @Inject
    lateinit var mediaDownloadManager: MediaDownloadManager

    override fun getDownloadManager(): DownloadManager {
        return mediaDownloadManager.getDownloadManager()
    }

    override fun getScheduler(): Scheduler? {
        return if (Util.SDK_INT >= 21) PlatformScheduler(
            this,
            JOB_ID
        ) else null
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        return mediaDownloadManager.getDownloadNotificationHelper()
            .buildProgressNotification(
                this,
                R.drawable.ic_download,
                null,
                getString(R.string.notification_message_media_downlaod),
                downloads,
                notMetRequirements
            )
    }
}