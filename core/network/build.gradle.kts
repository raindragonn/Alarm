plugins {
    id("alarm.android.library")
    id("alarm.hilt")
    alias(libs.plugins.kotlin.serialization)
}

val apiKeys = listOf(
    "SEARCH_API_HOST",
    "SEARCH_API_ENDPOINT",
    "SEARCH_API_OPTION1",
    "SEARCH_API_OPTION1_VALUE",
    "SEARCH_API_OPTION2",
    "SEARCH_API_OPTION2_VALUE",
    "SEARCH_API_OPTION3",
    "SEARCH_API_OPTION3_VALUE",
    "SEARCH_API_OPTION4",
    "SEARCH_API_OPTION4_VALUE",
    "PARSING_DEFAULT_SELECT",
    "PARSING_DEFAULT_ATTR",
)
val api = localProperties(apiKeys.map { "api.$it" })

android {
    namespace = "com.bluepig.alarm.network"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
        apiKeys.forEach { buildConfigField("String", it, "\"${api.getValue("api.$it")}\"") }
    }

    buildTypes {
        release {
            resValue("string", "app_name", "@string/app_name_default")
        }
        debug {
            resValue("string", "app_name", "@string/app_name_dev")
        }
    }
    buildFeatures {
        buildConfig = true
        resValues = true
    }
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.retrofit2)
    implementation(libs.retrofit2.serialization.converter)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.intercepter)
    implementation(libs.jsoup)
    // 4shared 다운로드 페이지 User-Agent (Util.getUserAgent)
    implementation(libs.androidx.media3.common)

    implementation(libs.google.playservices.auth)
    implementation(libs.google.api.client)
    // TubeRemoteDataSource가 YouTube 모델을 그대로 돌려줌
    api(libs.google.youtube)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp3.mockwebserver)
}
