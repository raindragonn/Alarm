package com.bluepig.alarm.util.log

import timber.log.Timber

class DebugTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "[DEV_LOG] $tag", message, t)
    }
}