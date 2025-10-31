package com.bluepig.alarm

object AppConfiguration {
    const val compileSdk = 35
    const val targetSdk = 35
    const val minSdk = 24
    const val majorVersion = 1
    const val minorVersion = 1
    const val patchVersion = 1
    const val versionCode = 9
    const val isTest = false

    val versionName
        get() = "$majorVersion.$minorVersion.${patchVersion}${isTest.let { if (it) "_test" else "" }}"
}
