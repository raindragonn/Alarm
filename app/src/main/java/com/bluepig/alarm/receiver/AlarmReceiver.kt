package com.bluepig.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bluepig.alarm.ui.alarm.AlarmActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val alarmId: Long = intent?.getLongExtra(EXTRA_ALARM_ID, -1) ?: -1
        if (context == null || alarmId < 0) return
        context.startActivity(AlarmActivity.getOpenIntent(context, alarmId))
    }

    companion object {
        const val EXTRA_ALARM_ID = "EXTRA_ALARM_ID"
    }
}