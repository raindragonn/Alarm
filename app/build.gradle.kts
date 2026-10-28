plugins {
    id("alarm.android.application")
    id("alarm.hilt")

    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)

    alias(libs.plugins.androidx.navigation.safeargs)
}

val debugJson =
    groovy.json.JsonSlurper()
        .parseText(file("../key.stores/debug.keys").readText()) as Map<String, String>
val releaseJson = groovy.json.JsonSlurper()
    .parseText(file("../key.stores/release.keys").readText()) as Map<String, String>

android {
    namespace = "com.bluepig.alarm"

    defaultConfig {
        applicationId = "com.bluepig.alarm"
        versionCode = 10
        versionName = "1.1.1"
    }
    signingConfigs {
        getByName("debug") {
            keyAlias = debugJson["KEY_ALIAS"]
            keyPassword = debugJson["KEY_PASSWORD"]
            storeFile = file("../key.stores/debug.jks")
            storePassword = debugJson["STORE_PASSWORD"]
        }
        create("release") {
            keyAlias = releaseJson["KEY_ALIAS"]
            keyPassword = releaseJson["KEY_PASSWORD"]
            storeFile = file("../key.stores/release.jks")
            storePassword = releaseJson["STORE_PASSWORD"]
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
            resValue("string", "ads_app_id", releaseJson["ADS_APP_ID"]!!)
            resValue("string", "ads_main_bottom_native", releaseJson["ADS_MAIN_BOTTOM_NATIVE"]!!)
            resValue("string", "ads_alarm_list_native", releaseJson["ADS_ALARM_LIST_NATIVE"]!!)
            resValue("string", "ads_alarm_banner", releaseJson["ADS_ALARM_BANNER"]!!)
            resValue(
                "string",
                "ads_media_select_interstitial",
                releaseJson["ADS_MEDIA_SELECT_INTERSTITIAL"]!!
            )
        }
        debug {
            signingConfig = signingConfigs["debug"]
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"

            resValue("string", "app_name", "@string/app_name_dev")
            resValue("string", "ads_app_id", debugJson["ADS_APP_ID"]!!)
            resValue("string", "ads_main_bottom_native", debugJson["ADS_MAIN_BOTTOM_NATIVE"]!!)
            resValue("string", "ads_alarm_list_native", debugJson["ADS_ALARM_LIST_NATIVE"]!!)
            resValue("string", "ads_alarm_banner", debugJson["ADS_ALARM_BANNER"]!!)
            resValue(
                "string",
                "ads_media_select_interstitial",
                debugJson["ADS_MEDIA_SELECT_INTERSTITIAL"]!!
            )
        }
    }
    buildFeatures {
        viewBinding = true
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
