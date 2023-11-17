package com.bluepig.alarm.util.log

import timber.log.Timber

/**
 * Release tree
 * 릴리즈용 로그
 *
 * @constructor Create empty Release tree
 */
class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // TODO: 자체 로거 개발 이후 추가필요 (analytics, crashlytics)
    }
}