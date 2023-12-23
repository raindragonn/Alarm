package com.bluepig.alarm

import android.app.Application
import com.bluepig.alarm.util.log.DebugTree
import com.bluepig.alarm.util.log.ReleaseTree
import com.bluepig.alarm.util.logger.BpLogger
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AlarmApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupTimber()
        BpLogger.logAppCreatedTime()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}