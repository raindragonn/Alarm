package com.bluepig.alarm.util.ext

import android.app.AlarmManager
import android.content.Context
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager