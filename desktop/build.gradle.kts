import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.compose")
    kotlin("plugin.serialization")

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


dependencies {
    implementation(project(":common"))
    implementation(compose.desktop.currentOs)
    api("net.dongliu:apk-parser:2.6.10")
    implementation("dev.mobile:dadb:1.2.6")

    implementation("br.com.devsrsouza.compose.icons.jetbrains:feather:1.0.0")
    implementation(fileTree("libs"))
    implementation("com.google.zxing:core:3.3.3")

    api( "com.google.auto.service:auto-service:1.0")
    kapt("com.google.auto.service:auto-service:1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")

}


tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

group = "me.lwb"
version = "1.1"





compose.desktop {
    application {
        mainClass = "me.lwb.androidtool.App"
        nativeDistributions {
            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Exe)
            packageName = "AndroidTool"
            packageVersion = "1.1.0"
        }
        buildTypes {
            release.proguard {
                isEnabled.set(false)
            }

        }
    }
}
