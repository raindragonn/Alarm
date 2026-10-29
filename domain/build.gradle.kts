plugins {
    id("alarm.jvm.library")
    alias(libs.plugins.kotlin.serialization)
}

dependencies{
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutine.core)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}