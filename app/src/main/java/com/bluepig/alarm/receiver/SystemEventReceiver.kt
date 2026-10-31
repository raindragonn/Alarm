package com.bluepig.alarm.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import com.bluepig.alarm.R
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.usecase.RefreshAlarms
import com.bluepig.alarm.notification.NotificationType
import com.bluepig.alarm.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

/**
 * 시스템 이벤트에 맞춰 알람을 다시 예약.
 * 재부팅(잠금 해제 전 포함, C7)·시각·시간대 변경(U1)·정확한 알람 권한 허용(C3)
 */
@AndroidEntryPoint
class SystemEventReceiver : BroadcastReceiver() {
    @Inject
    lateinit var refreshAlarms: RefreshAlarms

    @Inject
    lateinit var appScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in ACTIONS) return
        val pending = goAsync()
        appScope.launch {
            try {
                val missed = refreshAlarms()
                if (intent.action in BOOT_ACTIONS) notifyMissed(context, missed)
            } finally {
                pending.finish()
            }
        }
    }

    // 잠금 해제 전과 후 두 번 오므로 알람마다 같은 ID로 덮어씀
    @OptIn(UnstableApi::class)
    private fun notifyMissed(context: Context, missed: List<Alarm>) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return
        val open = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE,
        )
        missed.forEach { alarm ->
            val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(alarm.timeInMillis))
            val notification = NotificationCompat.Builder(context, NotificationType.ALARM_MISSED.channelId)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle(context.getString(R.string.notification_missed_title))
                .setContentText(context.getString(R.string.notification_missed_text, time))
                .setContentIntent(open)
                .setGroup(NotificationType.ALARM_MISSED.channelId)
                .setAutoCancel(true)
                .build()
            @Suppress("MissingPermission") // areNotificationsEnabled로 확인함
            manager.notify(MISSED_TAG, (alarm.id ?: 0L).toInt(), notification)
        }
    }

    private companion object {
        const val MISSED_TAG = "missed"
        val BOOT_ACTIONS = setOf(Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_LOCKED_BOOT_COMPLETED)
        val ACTIONS = BOOT_ACTIONS + setOf(
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED,
        )
    }
}
