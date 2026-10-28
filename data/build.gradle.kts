import com.bluepig.alarm.AppConfiguration
import groovy.json.JsonSlurper

plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidx.room)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.bluepig.alarm.data"
    compileSdk = AppConfiguration.compileSdk

    defaultConfig {
        minSdk = AppConfiguration.minSdk

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

    // hilt
    implementation(libs.google.hilt)
    ksp(libs.google.hilt.compiler)

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