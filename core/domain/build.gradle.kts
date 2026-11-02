plugins {
    id("alarm.jvm.library")
    `java-test-fixtures`
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutine.core)
    implementation(libs.kotlinx.serialization.json)

    // Fake·FixedClockRule은 feature 테스트도 씀
    testFixturesImplementation(libs.kotlinx.coroutine.core)
    testFixturesImplementation(libs.junit)
    testFixturesImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}