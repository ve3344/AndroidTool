plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.serialization")

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {

    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

    api("com.github.ve3344:Logger:1.0.0")
}