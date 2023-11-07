package com.bluepig.alarm.util.ext

import android.app.AlarmManager
import android.content.Context
import android.view.LayoutInflater

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager