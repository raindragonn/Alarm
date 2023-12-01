package com.bluepig.alarm.manager.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.bluepig.alarm.domain.alarm.BpAlarmManager
import com.bluepig.alarm.domain.entity.alarm.Alarm
import com.bluepig.alarm.domain.util.CalendarHelper
import com.bluepig.alarm.receiver.AlarmReceiver
import com.bluepig.alarm.util.ext.alarmManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class BPAlarmManagerImpl @Inject constructor(
    @ApplicationContext
    private val _context: Context,
) : BpAlarmManager {
    private val _alarmManager: AlarmManager
        get() = _context.alarmManager

    override fun setupAlarm(alarm: Alarm) {
        val alarmId = alarm.id ?: return
        if (alarm.timeInMillis < CalendarHelper.now.timeInMillis) return
        val intent = getAlarmReceiverIntent(alarmId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !_alarmManager.canScheduleExactAlarms()) {
            Timber.e("ExactAlarms Permission denied!")
            return
        }
        _alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                alarm.timeInMillis,
                null,
            ),
            intent
        )
    }

    override fun cancelAlarm(alarm: Alarm) {
        val alarmId = alarm.id ?: return
        val intent = getAlarmReceiverIntent(alarmId)
        _alarmManager.cancel(intent)
    }

    private fun getAlarmReceiverIntent(alarmId: Long): PendingIntent {
        val intent = Intent(_context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarmId)
        }
        return PendingIntent.getBroadcast(
            _context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}