import com.bluepig.alarm.AppConfiguration

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.google.hilt)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)

    alias(libs.plugins.androidx.navigation.safeargs)
    kotlin("kapt")
}

android {
    namespace = "com.bluepig.alarm"
    compileSdk = AppConfiguration.compileSdk

    defaultConfig {
        applicationId = "com.bluepig.alarm"
        minSdk = AppConfiguration.minSdk
        targetSdk = AppConfiguration.compileSdk
        versionCode = AppConfiguration.versionCode
        versionName = AppConfiguration.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        getByName("debug") {
            val debugJson =
                groovy.json.JsonSlurper()
                    .parseText(file("../key.stores/debug.keys").readText()) as Map<String, String>

            keyAlias = debugJson["KEY_ALIAS"]
            keyPassword = debugJson["KEY_PASSWORD"]
            storeFile = file("../key.stores/debug.jks")
            storePassword = debugJson["STORE_PASSWORD"]
        }
        create("release") {
            val releaseJson = groovy.json.JsonSlurper()
                .parseText(file("../key.stores/release.keys").readText()) as Map<String, String>

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
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", AppConfiguration.appName)
        }
        debug {
            signingConfig = signingConfigs["debug"]
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", AppConfiguration.appName + AppConfiguration.debugSuffix)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    /**
     *  Hilt Allow references to generated code
     */
    kapt {
        correctErrorTypes = true
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
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.ui)

    implementation(libs.google.material)
    implementation(libs.google.hilt)
    kapt(libs.google.hilt.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)

    implementation(libs.coil)
    implementation(libs.timber)

    implementation(libs.google.playservices.auth)
    implementation(libs.google.api.client)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}