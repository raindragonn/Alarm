package com.bluepig.alarm

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.core.os.UserManagerCompat
import com.bluepig.alarm.manager.config.ConfigManager
import com.bluepig.alarm.notification.NotificationType
import com.bluepig.alarm.util.log.DebugTree
import com.bluepig.alarm.util.log.ReleaseTree
import com.bluepig.alarm.util.logger.BpLogger
import com.google.android.gms.ads.MobileAds
import dagger.Lazy
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AlarmApplication : Application() {

    // Firebase는 잠금 해제 전에 만들면 안 돼서 Lazy
    @Inject
    lateinit var configManager: Lazy<ConfigManager>

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        NotificationType.createAll(this)
        // 재부팅 후 잠금 해제 전(Direct Boot)에 알람이 울리면 credential storage를 못 씀 (C7)
        if (UserManagerCompat.isUserUnlocked(this)) {
            initAfterUnlock()
        } else {
            ContextCompat.registerReceiver(
                this,
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        unregisterReceiver(this)
                        initAfterUnlock()
                    }
                },
                IntentFilter(Intent.ACTION_USER_UNLOCKED),
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
        }
    }

    private fun initAfterUnlock() {
        BpLogger.logAppCreatedTime()
        MobileAds.initialize(this)
        configManager.get().fetch()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}
