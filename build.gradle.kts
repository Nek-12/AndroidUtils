import java.net.URI

plugins {
    `kotlin-dsl`
    `maven-publish`
}

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
    when (name) {
        "app" -> apply(plugin = "com.android.application")
        "core-ktx" -> apply(plugin = "java-library")
        else -> apply(plugin= "android-library")
    }

}

afterEvaluate {
    if (!plugins.hasPlugin("com.android.application")) {
        publishing {
            publications {
                create<MavenPublication>("release") {
                    groupId = "com.nek12"
                    artifactId = "androidextensions"
                    version = "0.1"
                    if (plugins.hasPlugin("java")) {
                        from(components["java"])
                    } else if (plugins.hasPlugin("android-library")) {
                        from(components["android"])
                    }
                }
            }
        }
    }
}
