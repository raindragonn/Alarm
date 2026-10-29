plugins {
    id("alarm.android.application")
    id("alarm.hilt")

    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)

    alias(libs.plugins.androidx.navigation.safeargs)
}

val adsKeys = listOf(
    "ADS_APP_ID",
    "ADS_MAIN_BOTTOM_NATIVE",
    "ADS_ALARM_LIST_NATIVE",
    "ADS_ALARM_BANNER",
    "ADS_MEDIA_SELECT_INTERSTITIAL",
)
val secrets = localProperties(
    listOf("debug", "release").flatMap { buildType ->
        (listOf("KEY_ALIAS", "KEY_PASSWORD", "STORE_PASSWORD") + adsKeys).map { "$buildType.$it" }
    }
)

android {
    namespace = "com.bluepig.alarm"

    defaultConfig {
        applicationId = "com.bluepig.alarm"
        // CI 배포는 -PversionCode로 덮어씀 (기술 0010)
        versionCode = providers.gradleProperty("versionCode").orNull?.toInt() ?: 10
        versionName = "1.1.1"
    }
    signingConfigs {
        listOf("debug", "release").forEach { buildType ->
            maybeCreate(buildType).apply {
                keyAlias = secrets.getValue("$buildType.KEY_ALIAS")
                keyPassword = secrets.getValue("$buildType.KEY_PASSWORD")
                storeFile = rootProject.file("key.stores/$buildType.jks").takeIf { it.exists() }
                    ?: throw GradleException("key.stores/$buildType.jks 파일이 없습니다")
                storePassword = secrets.getValue("$buildType.STORE_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs["release"]
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "@string/app_name_default")
            adsKeys.forEach { resValue("string", it.lowercase(), secrets.getValue("release.$it")) }
        }
        debug {
            signingConfig = signingConfigs["debug"]
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"

            resValue("string", "app_name", "@string/app_name_dev")
            adsKeys.forEach { resValue("string", it.lowercase(), secrets.getValue("debug.$it")) }
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        resValues = true
    }
    packaging {
        // google-api-client 2.x가 끌어오는 google-auth-library jar끼리 겹침
        resources.excludes += listOf("META-INF/DEPENDENCIES", "META-INF/INDEX.LIST")
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":domain"))

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.ui)

    implementation(libs.google.material)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.config.ktx)

    implementation(libs.coil)
    implementation(libs.timber)
    implementation(libs.youtube.player.core)
    implementation(libs.youtube.player.ui)

    implementation(libs.google.playservices.auth)
    implementation(libs.google.playservices.ads)
    implementation(libs.google.api.client)

    testImplementation(libs.junit)

    debugImplementation(libs.leak.canary)
}
