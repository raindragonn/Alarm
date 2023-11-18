package com.bluepig.alarm.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import com.bluepig.alarm.R

enum class NotificationType(
    val channelId: String,
    @StringRes val channelNameResource: Int,
    @StringRes val channelDescriptionResource: Int
) {
    DOWNLOAD_NOTIFICATION(
        "Media Download",
        R.string.chennel_name_media_download,
        R.string.chennel_description_media_download
    );

    @UnstableApi
    fun createChannel(
        context: Context
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(channelNameResource),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(channelDescriptionResource)
            }
            NotificationManagerCompat.from(context)
                .createNotificationChannel(channel)

        }
    }
}