package com.bluepig.alarm.manager.download

import android.content.Context
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import com.bluepig.alarm.R
import com.bluepig.alarm.domain.entity.file.File
import kotlinx.serialization.json.Json

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
                val data = Json.decodeFromString(
                    File.serializer(),
                    Util.fromUtf8Bytes(download.request.data)
                )

                downloadNotificationHelper.buildDownloadCompletedNotification(
                    context,
                    R.drawable.ic_download_done,
                    null,
                    data.title
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