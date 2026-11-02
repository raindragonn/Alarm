plugins {
    id("alarm.android.feature")
}

android {
    namespace = "com.bluepig.alarm.feature.alarm"
    resourcePrefix = "alarm_"
}

dependencies {
    // 유튜브 알람은 울림 화면이 영상을 틂 (소리는 영상이 나오기 전까지 서비스의 기본 알람음, C5)
    implementation(libs.youtube.player.core)
    implementation(libs.androidx.activity.compose)
}
