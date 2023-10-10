buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.4")
    }
}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.0" apply false
}