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
    // YouTube 계정 선택 Intent
    implementation(libs.google.playservices.auth)
    implementation(libs.androidx.activity.compose)
    implementation(libs.coil3.compose)
}
