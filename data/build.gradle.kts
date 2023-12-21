import com.bluepig.alarm.AppConfiguration
import groovy.json.JsonSlurper

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.kotlin.serialization)
    kotlin("kapt")
}

android {
    namespace = "com.bluepig.alarm.data"
    compileSdk = AppConfiguration.compileSdk

    defaultConfig {
        minSdk = AppConfiguration.minSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        all {
            val apiJson =
                JsonSlurper().parseText(file("../key.stores/api.keys").readText()) as org.apache.groovy.json.internal.LazyMap

            apiJson.entries.forEach {
                buildConfigField("String", it.key, "\"${it.value}\"")
            }
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", AppConfiguration.appName)
        }
        debug {
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
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.google.material)
    implementation(libs.kotlinx.serialization.json)

    // room
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // hilt
    implementation(libs.google.hilt)
    kapt(libs.google.hilt.compiler)

    implementation(libs.retrofit2)
    implementation(libs.retrofit2.serialization.converter)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging.intercepter)
    implementation(libs.jsoup)

    implementation(libs.google.playservices.auth)
    implementation(libs.google.youtube)
    implementation(libs.google.api.client)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}