package com.bluepig.alarm

object AppConfiguration {
    const val compileSdk = 34
    const val targetSdk = 34
    const val minSdk = 24
    const val majorVersion = 1
    const val minorVersion = 0
    const val patchVersion = 4
    const val versionCode = 5
    const val isTest = false

    val versionName
        get() = "$majorVersion.$minorVersion.${patchVersion}${isTest.let { if (it) "_test" else "" }}"
}
