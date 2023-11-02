package com.bluepig.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("DEV_LOG", "AlarmReceiver.kt.onReceive: ${intent?.action }")
        context?.let {
            Toast.makeText(it, "예업", Toast.LENGTH_SHORT).show()
        }
    }
}