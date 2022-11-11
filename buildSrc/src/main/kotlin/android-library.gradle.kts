@file:Suppress("MagicNumber") // TODO: Move to Config object

import org.gradle.internal.impldep.com.amazonaws.util.XpathUtils.asNode

plugins {
    id("com.android.library")
    id("kotlin-android")
    `maven-publish`
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 22
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            setProperty(
                "archivesBaseName",
                project.name
            )
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjvm-default=all",
            "-Xbackend-threads=0", // parallel IR compilation
            "-opt-in=kotlin.Experimental",
            "-opt-in=kotlin.RequiresOptIn",
        )
    }

    sourceSets.all {
        java.srcDir("src/$name/kotlin")
    }

    buildFeatures {
        aidl = false
        buildConfig = false
        prefab = false
        renderScript = false
        mlModelBinding = false
        resValues = false
        androidResources = true
    }

    libraryVariants.all {
        kotlin {
            sourceSets {
                getByName(name) {
                    kotlin.srcDir("build/generated/ksp/$name/kotlin")
                }
            }
        }
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            artifact("$buildDir/outputs/aar/${project.name}-release.aar")
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = rootProject.version.toString()

            pom.withXml {
                val dependenciesNode = asNode().appendNode("dependencies")
                configurations.implementation.get().allDependencies.forEach {
                    if (it.name != "unspecified") {
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", it.version)
                    }
                }
            }
        }
    }
}

tasks.findByName("publishReleasePublicationToMavenLocal")!!.apply {
    dependsOn("bundleReleaseAar")
}
