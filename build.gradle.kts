import java.net.URI

plugins {
    `kotlin-dsl`
}

rootProject.group = "com.nek12.androidutils"
rootProject.version = "0.1.6"

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0-RC")
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
    group = rootProject.group
    version = rootProject.version

    when (name) {
        "app" -> apply(plugin = "com.android.application")
        "core-ktx" -> apply(plugin = "java-library")
        else -> apply(plugin= "android-library")
    }

}
