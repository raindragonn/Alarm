plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false

    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.compose) apply false

    alias(libs.plugins.google.hilt) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.google.services) apply false

    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.androidx.navigation.safeargs) apply false
    alias(libs.plugins.androidx.room) apply false
}
