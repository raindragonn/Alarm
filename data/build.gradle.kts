plugins {
    id("alarm.android.library")
    id("alarm.hilt")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidx.room)
}

room {
    schemaDirectory("$projectDir/schemas")
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
    namespace = "com.bluepig.alarm.data"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
        apiKeys.forEach { buildConfigField("String", it, "\"${api.getValue("api.$it")}\"") }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    packaging {
        resources.excludes.add(
            "META-INF/DEPENDENCIES"
        )
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.kotlinx.serialization.json)

    // room
    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)

    implementation(libs.retrofit2)
    implementation(libs.retrofit2.serialization.converter)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.intercepter)
    implementation(libs.jsoup)

    implementation(libs.google.playservices.auth)
    implementation(libs.google.youtube)
    implementation(libs.google.api.client)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp3.mockwebserver)
}