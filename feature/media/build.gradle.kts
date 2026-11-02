plugins {
    id("alarm.android.feature")
}

android {
    namespace = "com.bluepig.alarm.feature.media"
    resourcePrefix = "media_"
}

dependencies {
    // 미리듣기 플레이어는 화면이 가짐 (ViewModel은 UseCase만)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.youtube.player.core)
    implementation(libs.coil3.compose)
}
