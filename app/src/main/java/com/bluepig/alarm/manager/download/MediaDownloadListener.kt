package com.bluepig.alarm.manager.download

import android.content.Context
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import com.bluepig.alarm.R

@UnstableApi
class MediaDownloadListener(
    private val context: Context,
    private val downloadNotificationHelper: DownloadNotificationHelper,
    private var nextNotificationId: Int,
) : DownloadManager.Listener {
    override fun onDownloadChanged(
        downloadManager: DownloadManager,
        download: Download,
        finalException: Exception?
    ) {
        val notification = when (download.state) {
            Download.STATE_COMPLETED, Download.STATE_FAILED -> {
                downloadNotificationHelper.buildDownloadCompletedNotification(
                    context,
                    R.drawable.ic_download_done,
                    null,
                    Util.fromUtf8Bytes(download.request.data)
                )
            }

            else -> {
                return
            }
        }
        NotificationUtil.setNotification(
            context,
            nextNotificationId++,
            notification
        )
    }

}