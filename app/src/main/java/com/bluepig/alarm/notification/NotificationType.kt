package com.bluepig.alarm.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import com.bluepig.alarm.R

enum class NotificationType(
    val channelId: String,
    @StringRes val channelNameResource: Int,
    @StringRes val channelDescriptionResource: Int,
    private val importance: Int,
) {
    DOWNLOAD_NOTIFICATION(
        "Media Download",
        R.string.chennel_name_media_download,
        R.string.chennel_description_media_download,
        NotificationManager.IMPORTANCE_HIGH,
    ),

    /** 울리는 알람. 전체 화면 알림으로 울림 화면을 띄우고 소리·진동은 서비스가 냄 (기술 0026) */
    ALARM_RING(
        "alarm_ring",
        R.string.channel_name_alarm_ring,
        R.string.channel_description_alarm_ring,
        NotificationManager.IMPORTANCE_HIGH,
    ),
    ALARM_PREVIEW(
        "alarm_preview",
        R.string.channel_name_alarm_preview,
        R.string.channel_description_alarm_preview,
        NotificationManager.IMPORTANCE_LOW,
    ),

    /** 꺼져 있는 동안 지나간 회차 (C7) */
    ALARM_MISSED(
        "alarm_missed",
        R.string.channel_name_alarm_missed,
        R.string.channel_description_alarm_missed,
        NotificationManager.IMPORTANCE_DEFAULT,
    );

    fun createChannel(context: Context) {
        val channel = NotificationChannel(channelId, context.getString(channelNameResource), importance).apply {
            description = context.getString(channelDescriptionResource)
            if (this@NotificationType != DOWNLOAD_NOTIFICATION) {
                setSound(null, null)
                enableVibration(false)
            }
        }
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    companion object {
        fun createAll(context: Context) = entries.forEach { it.createChannel(context) }
    }
}
