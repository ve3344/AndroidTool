plugins {
    id("java-library")
    kotlin("kapt")
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization")

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
dependencies {
    api(fileTree("libs"))
    api(project(":json-rpc"))

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    api("com.github.ve3344:Logger:1.0.0")
}