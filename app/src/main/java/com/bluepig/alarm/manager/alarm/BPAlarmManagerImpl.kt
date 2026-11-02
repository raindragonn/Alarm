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
import com.bluepig.alarm.util.logger.BpLogger
import dagger.hilt.android.qualifiers.ApplicationContext
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
        // 31·32에서 권한을 거부한 경우만. 33+는 USE_EXACT_ALARM (기술 0025), 다시 허용하면 SystemEventReceiver가 재예약 (C3)
        if (!canScheduleExactAlarms()) {
            BpLogger.logException(Exception("ExactAlarms Permission denied!"))
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

    override fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || _alarmManager.canScheduleExactAlarms()

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