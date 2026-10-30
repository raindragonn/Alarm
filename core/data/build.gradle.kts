plugins {
    id("alarm.android.library")
    id("alarm.hilt")
}

android {
    namespace = "com.bluepig.alarm.data"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))

    implementation(libs.kotlinx.serialization.json)

    // 외부 예외 → domain 예외 변환에 쓰는 타입 (기술 0016)
    implementation(libs.retrofit2)
    implementation(libs.google.api.client)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
