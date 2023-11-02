package com.bluepig.alarm.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bluepig.alarm.BuildConfig
import com.bluepig.alarm.databinding.ActivityMainBinding
import com.bluepig.alarm.receiver.AlarmReceiver
import com.bluepig.alarm.util.viewBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        const val ACTION_ALARM = BuildConfig.APPLICATION_ID + ".ACTION_ALARM"
    }

    private val _binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)
    private val alarmManager: AlarmManager by lazy { getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)

        initViews()
    }

    private fun initViews() {
        _binding.btnAlarmSet.setOnClickListener {

            val hour = _binding.tp.hour
            val minute = _binding.tp.minute

            val tpCalendar = Calendar.getInstance()
            tpCalendar.apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val format = SimpleDateFormat("yyyy.MM.dd.HH.mm", Locale.getDefault())
                .format(tpCalendar.time)
            val isBefore = Calendar.getInstance().before(tpCalendar)

            if (!isBefore) return@setOnClickListener


            val pendingAlarm =
                Intent()
                    .apply {
                        setClass(applicationContext, AlarmReceiver::class.java)
//                        action = ACTION_ALARM
                        action = "ACTION_ALARM"
                    }
                    .let {
                        PendingIntent.getBroadcast(
                            applicationContext,
                            1000,
                            it,
                            pendingIntentUpdateCurrentFlag()
                        )
                    }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                Log.e("DEV_LOG", "initViews: error")
                return@setOnClickListener
            }

            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(tpCalendar.timeInMillis, null),
                pendingAlarm
            )

            Toast.makeText(this, "$format 에 울립니다.", Toast.LENGTH_SHORT).show()
        }

        _binding.btnAlarmCancel.setOnClickListener {

        }
    }

    fun pendingIntentUpdateCurrentFlag(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }
}