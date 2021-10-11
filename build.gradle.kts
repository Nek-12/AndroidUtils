import java.net.URI

plugins {
    `kotlin-dsl`
}

val compileSdk by extra { 31 }

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    }

    dependencies {
        val kotlinVersion by extra { "1.6.0-M1" }
        val navVersion by extra { "2.4.0-alpha09" }

        classpath( "com.android.tools.build:gradle:7.0.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0-M1")
    }
}

allprojects {
    repositories {
        google()
        maven { url = URI("https://jitpack.io") }
        mavenCentral()
    }
}

subprojects {
    if (name == "app") {
        apply(plugin = "com.android.application")
    } else {
        apply(plugin = "com.android.library")
    }
    apply (plugin= "kotlin-android")
    apply (plugin= "kotlin-kapt")
}
