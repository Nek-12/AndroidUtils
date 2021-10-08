import java.net.URI

plugins {
    `kotlin-dsl`
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    val navVersion by extra { "2.4.0-alpha09" }
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    }
    dependencies {
        classpath( "com.android.tools.build:gradle:7.0.2")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
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

    (project.extensions.findByName("android") as? com.android.build.gradle.BaseExtension)?.run {
        compileSdkVersion(Versions.compileSdk)

        defaultConfig {
            minSdkVersion(Versions.minSdk)
            targetSdkVersion(Versions.targetSdk)
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        dexOptions {
            preDexLibraries = true
            javaMaxHeapSize = "4g"
        }

        compileOptions {
            targetCompatibility JavaVersion.VERSION_1_8
        }
    }

    kapt {
        useBuildCache = true
        correctErrorTypes = true
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.1.1")
        implementation("androidx.core:core-ktx:1.0.1")
        implementation("androidx.appcompat:appcompat:1.0.2")
        implementation("org.koin:koin-androidx-viewmodel:2.0.0-beta-1")

        testImplementation("junit:junit:4.12")

        androidTestImplementation("androidx.test:runner:1.1.1")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")
    }
}
