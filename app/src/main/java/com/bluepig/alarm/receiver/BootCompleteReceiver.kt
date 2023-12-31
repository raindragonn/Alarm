package com.bluepig.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bluepig.alarm.domain.usecase.RefreshAlarms
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompleteReceiver : BroadcastReceiver() {
    @Inject
    lateinit var refreshAlarms: RefreshAlarms

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        CoroutineScope(Dispatchers.Main).launch {
            refreshAlarms.invoke()
        }
    }
}