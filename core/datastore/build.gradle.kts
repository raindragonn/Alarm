plugins {
    id("alarm.android.library")
    id("alarm.hilt")
}

android {
    namespace = "com.bluepig.alarm.datastore"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
