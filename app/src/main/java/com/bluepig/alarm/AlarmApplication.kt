package com.bluepig.alarm

import android.app.Application
import com.bluepig.alarm.manager.config.ConfigManager
import com.bluepig.alarm.util.log.DebugTree
import com.bluepig.alarm.util.log.ReleaseTree
import com.bluepig.alarm.util.logger.BpLogger
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AlarmApplication : Application() {

    @Inject
    lateinit var configManager: ConfigManager

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        BpLogger.logAppCreatedTime()
        MobileAds.initialize(this)
        configManager.fetch()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}