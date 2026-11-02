plugins {
    id("alarm.android.library")
    id("alarm.android.compose")
}

android {
    namespace = "com.bluepig.alarm.designsystem"
    resourcePrefix = "designsystem_"
}

dependencies {
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material3)

    implementation(libs.coil3.compose)
    implementation(libs.coil3.network.okhttp)
}
