plugins {
    id("com.android.library")
    id("kotlin-android")
    `maven-publish`
}

android {
    compileSdk = Config.compileSdk
    resourcePrefix = "${Config.artifact}_"

    defaultConfig {
        minSdk = Config.minSdk
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
        jvmTarget = Config.jvmTarget.target
        freeCompilerArgs += Config.kotlinCompilerArgs
    }

    buildFeatures {
        aidl = false
        buildConfig = false
        prefab = false
        renderScript = false
        mlModelBinding = false
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
            artifact("${layout.buildDirectory.get()}/outputs/aar/${project.name}-release.aar")
            groupId = rootProject.group.toString()
            artifactId = project.name
            version = rootProject.version.toString()

            pom.withXml {
                val deps = configurations.implementation
                    .orNull
                    ?.allDependencies
                    .orEmpty()
                    .ifEmpty { return@withXml }
                val dependenciesNode = asNode().appendNode("dependencies")
                deps.forEach {
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
