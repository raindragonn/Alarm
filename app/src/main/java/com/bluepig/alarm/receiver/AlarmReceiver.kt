package com.bluepig.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bluepig.alarm.service.AlarmRingService

/** 예약한 시각에 울림 서비스를 시작. 화면은 서비스의 전체 화면 알림이 띄움 (C1) */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1)
        if (alarmId < 0) return
        AlarmRingService.ring(context, alarmId)
    }

    companion object {
        const val EXTRA_ALARM_ID = "EXTRA_ALARM_ID"
    }
}
