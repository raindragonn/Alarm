package com.bluepig.alarm.util.ext

import android.app.AlarmManager
import android.content.Context
import android.media.AudioManager
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import com.bluepig.alarm.R

val Context.inflater: LayoutInflater
    get() = LayoutInflater.from(this)

val Context.alarmManager: AlarmManager
    get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager

val Context.audioManager: AudioManager
    get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager

val Context.inputMethodManager: InputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

@get:UnstableApi
val Context.userAgent
    get() = Util.getUserAgent(this, getString(R.string.app_name))