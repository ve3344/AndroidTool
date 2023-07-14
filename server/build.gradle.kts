plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")

//
}

android {
    compileSdk = 32

    defaultConfig {
        minSdk = 21
        targetSdk = 32
        applicationId = "me.lwb.androidtool"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    api( "com.google.auto.service:auto-service:1.0")
    kapt("com.google.auto.service:auto-service:1.0")

    implementation(fileTree("libs"))
    implementation (project(":common"))
    implementation("com.faendir.rhino:rhino-android:1.6.0")
    implementation("com.google.code.gson:gson:2.9.0")

}


